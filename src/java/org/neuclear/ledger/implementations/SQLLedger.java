/*
 * Created on Jul 14, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.neuclear.ledger.implementations;

import org.neuclear.commons.sql.ConnectionSource;
import org.neuclear.commons.sql.SQLTools;
import org.neuclear.commons.sql.SQLContext;
import org.neuclear.commons.sql.DefaultConnectionSource;
import org.neuclear.commons.sql.statements.StatementFactory;
import org.neuclear.commons.sql.entities.EntityModel;
import org.neuclear.commons.sql.entities.Schema;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.ledger.*;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.browser.LedgerBrowser;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.QueryBookBrowser;
import org.neuclear.id.NSTools;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

import javax.transaction.*;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.Iterator;

/**
 * @author pelleb
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class SQLLedger extends Ledger implements LedgerBrowser {

    /**
     * public SQLLedger(String id) throws SQLException, IOException, UnknownLedgerException {
     * this(SQLTools.getConnection(),id);
     * }
     * public SQLLedger(String id) throws SQLException, IOException, UnknownLedgerException {
     * this(SQLTools.getConnection(),id);
     * }
     */
/*    public SQLLedger(String id) throws SQLException, IOException, UnknownLedgerException {
        this(SQLTools.getConnection(),id);
    }
*/
    public SQLLedger(final StatementFactory fact, final String id) throws LowlevelLedgerException, UnknownLedgerException {
        super(id, "sql ledger");
        this.fact=fact;
        create(fact);
        createLedger(id);
    }

    public void createLedger(String name) throws LowlevelLedgerException {
        try {
             final PreparedStatement query=prepQuery("select * from ledger where id= ?");
             query.setString(1,name);
             ResultSet rs=query.executeQuery();
             if (!rs.next()) {
                 final PreparedStatement stmt = prepQuery("insert into ledger (id,title,created) values (?,?,now())");
                 stmt.setString(1, name);
                 stmt.setString(2, name);
                 stmt.execute();
             }
         } catch (SQLException e) {
             rollbackUT();
             throw new LowlevelLedgerException(this, e);
         }

    }
    public static synchronized void create(StatementFactory fact){
        createSchema().create(fact);
    }
    public static Schema createSchema() {
        Schema schema=new Schema("mysql");

        EntityModel ledgerModel=schema.addEntityModel("ledger");
        ledgerModel.addTitle();
//            ledgerModel.addComment();
        ledgerModel.addTimeStamp();
        EntityModel bookModel=schema.addEntityModel("book");
        bookModel.addTitle();
        bookModel.addTimeStamp();
        EntityModel xactModel=schema.addEntityModel("transaction");
        xactModel.addComment();
        xactModel.addValueTime();
        xactModel.addReference(ledgerModel);
        EntityModel entryModel=schema.addEntityModel("entry",false);
        entryModel.addMoney();
        entryModel.addReference(bookModel);
        entryModel.addReference(xactModel);

        EntityModel hxactModel=schema.addEntityModel("held_transaction");
        hxactModel.addComment();
        hxactModel.addValueTime();
        hxactModel.addTimeStamp("held_until");
        hxactModel.addReference(ledgerModel);
        hxactModel.addReference(xactModel);
        hxactModel.addBoolean("cancelled");
        EntityModel hentryModel=schema.addEntityModel("held_entry",false);
        hentryModel.addMoney();
        hentryModel.addReference(bookModel);
        hentryModel.addReference(hxactModel);
        return schema;
    }
    private static String getLedgerName(final StatementFactory fact, final String id) throws UnknownLedgerException, LowlevelLedgerException {
        try {
            final PreparedStatement stmt = fact.prepareStatement("select title from ledger where id=?");
            stmt.setString(1, id);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new UnknownLedgerException(id);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * This decides if new books are automatically created.
     * 
     * @return 
     */
    public final boolean allowAutoBookCreation() {
        return false;
    }

    public final boolean bookExists(final String bookID) throws LowlevelLedgerException {
        try {
            final PreparedStatement stmt = prepQuery("select id from book where id=?");
            stmt.setString(1, bookID);
            final ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        }
    }

    public final Book createNewBook(final String bookID, final String title) throws BookExistsException, LowlevelLedgerException {
        if (bookExists(bookID))
            throw new BookExistsException(this, bookID);
       try {
            final PreparedStatement stmt = prepQuery("insert into book values (?,?,now())");
            stmt.setString(1, bookID);
            stmt.setString(2, title);
            stmt.execute();
            return createBookInstance(bookID, title);
        } catch (SQLException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        }

    }

    /* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#performTransaction(org.neuclear.ledger.UnPostedTransaction)
	 */
    public final PostedTransaction performTransaction(final UnPostedTransaction transaction) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        if (!transaction.isBalanced()) {
            throw new UnBalancedTransactionException(this, transaction);
        }
        try {
            final String xid = insertTransaction(transaction);
            final Iterator items = transaction.getItems();
            while (items.hasNext()) {
                final TransactionItem item = (TransactionItem) items.next();
                insertTransactionItem(xid, item);
            }
            return this.createTransaction(transaction, xid);
        } catch (SQLException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        }
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * 
     * @param transaction Transaction to perform
     * @return Unique ID
     */
    public final PostedHeldTransaction performHeldTransaction(final UnPostedHeldTransaction transaction) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        if (!transaction.isBalanced()) {
            throw new UnBalancedTransactionException(this, transaction);
        }
        try {
            final String xid = insertHeldTransaction(transaction);
            final Iterator items = transaction.getItems();
            while (items.hasNext()) {
                final TransactionItem item = (TransactionItem) items.next();
                insertHeldTransactionItem(xid, item);
            }
            return this.createHeldTransaction(transaction, xid);
        } catch (SQLException e) {
            rollbackUT();
            System.err.println(e.getSQLState());
            e.printStackTrace(System.err);
            throw new LowlevelLedgerException(this, e);
        }
    }

    /**
     * Cancels a Held Transaction.
     * 
     * @param hold 
     * @throws LowlevelLedgerException     
     * @throws UnknownTransactionException 
     */
    public final void performCancelHold(final PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        try {
            final PreparedStatement update = prepQuery("update held_transaction set cancelled=1 where id=? and ledgerid=?");
            update.setString(1, hold.getXid());
            update.setString(2, getId());
            final int affected = update.executeUpdate();
            if (affected == 0)       {
                rollbackUT();
                throw new UnknownTransactionException(this, hold.getXid());
            }
            if (affected > 1) {
                rollbackUT();
                throw new LowlevelLedgerException(this, "performCancelHold: For some reason multiple rows were updated. Transaction Rolled Back.");
            }
        } catch (SQLException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        }
    }

    public final PostedTransaction performCompleteHold(final PostedHeldTransaction hold, final double amount, final Date time, final String comment) throws TransactionExpiredException, InvalidTransactionException, LowlevelLedgerException {
        try {
            final PreparedStatement query = prepQuery("select * from held_transaction where cancelled=0 and transactionid is null and id=? and ledgerid=?");
            query.setString(1, hold.getXid());
            query.setString(2, getId());
            final ResultSet rs = query.executeQuery();
            if (!rs.next())
                throw new TransactionExpiredException(this, hold);
            final PostedTransaction tran = createHeldComplete(hold, amount, time, comment);

            final PreparedStatement update = prepQuery("update held_transaction set transactionid=? where id=? and ledgerid=?");
            update.setString(1, tran.getXid());
            update.setString(2, hold.getXid());
            update.setString(3, getId());
            final int affected = update.executeUpdate();
            if (affected == 0)       {
                rollbackUT();
                throw new UnknownTransactionException(this, hold.getXid());
            }
            if (affected > 1) {
                rollbackUT();
                throw new LowlevelLedgerException(this, "performCompleteHold: For some reason multiple rows were updated. Transaction Rolled Back.");
            }
            return tran;

        } catch (SQLException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        } catch (UnknownTransactionException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        }

    }

    private String createTransactionID(){
            final Digest dig = new SHA1Digest();
            final StringBuffer buffy = new StringBuffer(getId());
            buffy.append('!');
            buffy.append(System.currentTimeMillis());
            buffy.append(CryptoTools.createRandomID());
            return buffy.toString();
    }
    private String insertTransaction(final UnPostedTransaction transaction) throws SQLException, LowlevelLedgerException {
        final PreparedStatement tranInsert = prepQuery("insert into transaction (id,valuetime,comment,ledgerid) values (?,?,?,?)");
        final String xid = createTransactionID();
        tranInsert.setString(1,xid);
        tranInsert.setTimestamp(2, SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(3, transaction.getComment());
        tranInsert.setString(4, getId());
        tranInsert.execute();
        return xid;
    }

    private String insertHeldTransaction(final UnPostedHeldTransaction transaction) throws SQLException, LowlevelLedgerException {
        final String xid = createTransactionID();
        final PreparedStatement tranInsert = prepQuery("insert into held_transaction (id,valuetime,comment,held_until,ledgerid,cancelled) values (?,?,?,?,?,0)");

        tranInsert.setString(1,xid);
        tranInsert.setTimestamp(2, SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(3, transaction.getComment());
        tranInsert.setTimestamp(4, SQLTools.toTimestamp(transaction.getExpiryTime()));
        tranInsert.setString(5, getId());
        tranInsert.execute();
        return xid;
    }

    private void insertTransactionItem(final String xid, final TransactionItem item) throws SQLException, LowlevelLedgerException {
        final PreparedStatement itemInsert = prepQuery("insert into entry (transactionid,bookid,amount) values (?,?,?)");
        itemInsert.setString(1, xid);
        itemInsert.setString(2, item.getBook().getBookID());
        itemInsert.setDouble(3, item.getAmount());
        itemInsert.execute();
    }

    private void insertHeldTransactionItem(final String xid, final TransactionItem item) throws SQLException, LowlevelLedgerException {
        final PreparedStatement itemInsert = prepQuery("insert into held_entry (held_transactionid,bookid,amount) values (?,?,?)");
        itemInsert.setString(1, xid);
        itemInsert.setString(2, item.getBook().getBookID());
        itemInsert.setDouble(3, item.getAmount());
        itemInsert.execute();
    }

    /**
     * Searches for a Transaction based on its Transaction ID
     * 
     * @param xid A valid ID
     * @return The Transaction object
     */
    public final PostedTransaction findTransaction(final String xid) throws LowlevelLedgerException, UnknownTransactionException {
        try {

            PreparedStatement stmt = prepQuery("select valuetime,comment from transaction where id=? and ledgerid=?");
            stmt.setString(1, xid);
            stmt.setString(2, getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new UnknownTransactionException(this, xid);
            }
            final Date started = rs.getTimestamp(1);
            final String comment = rs.getString(2);

            final UnPostedTransaction transaction = new UnPostedTransaction(this, comment, started);
            stmt = prepQuery("select bookid,amount from entry where transactionid=?");
            stmt.setString(1, xid);
            rs = stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)), rs.getDouble(2));

            return this.createTransaction(transaction, xid);

        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (UnknownBookException e) {
            throw new LowlevelLedgerException(this, e);
        }
    }

    /**
     * Searches for a Held Transaction based on its Transaction ID
     * 
     * @param xid A valid ID
     * @return The Transaction object
     */
    public final PostedHeldTransaction findHeldTransaction(final String xid) throws LowlevelLedgerException, UnknownTransactionException {
        try {

            PreparedStatement stmt = prepQuery("select valuetime,held_until,comment from held_transaction where id=? and ledgerid=?");
            stmt.setString(1, xid);
            stmt.setString(2, getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new UnknownTransactionException(this, xid);
            }
            final Date started = rs.getTimestamp(1);
            final Date ended = rs.getTimestamp(2);
            final String comment = rs.getString(3);

            final UnPostedHeldTransaction transaction = new UnPostedHeldTransaction(this, comment, started, ended);
            stmt = prepQuery("select bookid,amount from held_entry where held_transactionid=?");
            stmt.setString(1, xid);
            rs = stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)), rs.getDouble(2));

            return this.createHeldTransaction(transaction, xid);

        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (UnknownBookException e) {
            throw new LowlevelLedgerException(this, e);
        }
    }

    private PreparedStatement prepQuery(final String sql) throws SQLException, LowlevelLedgerException {
        return fact.prepareStatement(sql);
    }

    /* (non-Javadoc)
     * @see org.neuclear.ledger.Ledger#getBalance(org.neuclear.ledger.Book, java.util.Date)
     */
    public final double getBalance(final Book book, final Date time) throws LowlevelLedgerException {
        try {
            final PreparedStatement stmt = prepQuery("select sum(e.amount) from entry e,transaction t where e.transactionid=t.id and e.bookid=? and t.valuetime<= ? and t.ledgerid=?");

            stmt.setString(1, book.getBookID());
            stmt.setTimestamp(2, new Timestamp(time.getTime()+1));
            stmt.setString(3, getId());

            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see org.neuclear.ledger.Ledger#getBalance(org.neuclear.ledger.Book)
     */
    public final double getBalance(final Book book) throws LowlevelLedgerException {
        return getBalance(book, new Date());
    }

    public final double getAvailableBalance(final Book book, final Date time) throws LowlevelLedgerException {
        double balance = 0.0;
        try {
            final PreparedStatement stmt = prepQuery("select sum(u.amount) from (" +
                    "select  sum( e.amount) as amount from entry e,transaction t " +
                    "where e.transactionid=t.id and e.bookid=? and t.valuetime<= ? and t.ledgerid=?"
                    + "union " +
                    "select sum( e.amount) as amount from held_entry e, held_transaction t " +
                    "where " +
                    "e.held_transactionid=t.id and e.bookid=? and t.valuetime<= ? " +
                    "and e.amount<0 and t.held_until>= ? and t.cancelled=0 and t.transactionid is null and t.ledgerid=?"
                    + ") u "
            );
            final Timestamp ts = new Timestamp(time.getTime()+1);
            stmt.setString(1, book.getBookID());
            stmt.setTimestamp(2, ts);
            stmt.setString(3, getId());
            stmt.setString(4, book.getBookID());
            stmt.setTimestamp(5, ts);
            stmt.setTimestamp(6, ts);
            stmt.setString(7, getId());

            final ResultSet rs = stmt.executeQuery();
//            System.out.println("Avaliable Balance at: "+ts.toString());
            while (rs.next()) {
//                System.out.println(rs.getString(3)+": "+rs.getString(2)+" "+rs.getDouble(1)+" "+rs.getTimestamp(4));
                balance = +rs.getDouble(1); // Subselects seem to be causing a problem
            }
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        }
        return balance;
    }


    /* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#getAvailableBalance(org.neuclear.ledger.Book)
	 */
    public final double getAvailableBalance(final Book book) throws LowlevelLedgerException {
        return getAvailableBalance(book, new Date());
    }


    public final String toString() {
        return "SQL Ledger: " + getName();
    }

    public final Book getBook(final String bookID) throws UnknownBookException, LowlevelLedgerException {
        try {
            final PreparedStatement stmt = prepQuery("select title from book where id=?");
            stmt.setString(1, bookID);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return createBookInstance(bookID, rs.getString(1));
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        }
        throw new UnknownBookException(this, bookID);
    }

    /**
     * Rolls back a JTA UserTransaction on the ledger. Not to be confused with a Ledger Transaction.
     * @param ut
     * @throws LowlevelLedgerException
     */
    public void rollbackUT(UserTransaction ut) throws LowlevelLedgerException {
        super.rollbackUT(ut);
    }

    public BookBrowser browse(Book book) throws LowlevelLedgerException {
        try {
            return new QueryBookBrowser(book,fact);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
    }

    public BookBrowser browseFrom(Book book, Timestamp from) throws LowlevelLedgerException {
        try {
            return new QueryBookBrowser(book,fact,from);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
    }

    public BookBrowser browseRange(Book book, Timestamp from, Timestamp until) throws LowlevelLedgerException {
        try {
            return new QueryBookBrowser(book,fact,from,until);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
    }

    private final StatementFactory fact;
}
