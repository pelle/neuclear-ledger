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
import org.neuclear.commons.NeuClearException;
import org.neuclear.ledger.*;
import org.neuclear.ledger.InvalidTransactionException;

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
public final class SQLLedger extends Ledger {

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
    public SQLLedger(final ConnectionSource con, final String id) throws LowlevelLedgerException, UnknownLedgerException {
        super(id, getLedgerName(con, id));
        this.con = new SQLContext(con);
    }

    private static String getLedgerName(final ConnectionSource con, final String id) throws UnknownLedgerException, LowlevelLedgerException {
        try {
            final PreparedStatement stmt = con.getConnection().prepareStatement("select title from ledger where id=?");
            stmt.setString(1, id);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new UnknownLedgerException(id);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(e);
        } catch (IOException e) {
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
            final PreparedStatement stmt = prepQuery("select id from account where id=?");
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
            final PreparedStatement stmt = prepQuery("insert into account values (?,?,3)");
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
        final String newid;
        if (!transaction.isBalanced()) {
            throw new UnBalancedTransactionException(this, transaction);
        }
        try {
            final long xid = insertTransaction(transaction);
            final Iterator items = transaction.getItems();
            while (items.hasNext()) {
                final TransactionItem item = (TransactionItem) items.next();
                insertTransactionItem(xid, item);
            }
            newid = Long.toString(xid);
        } catch (SQLException e) {
            rollbackUT();
            throw new LowlevelLedgerException(this, e);
        }
        return this.createTransaction(transaction, newid);
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
        final String newid;
        if (!transaction.isBalanced()) {
            throw new UnBalancedTransactionException(this, transaction);
        }
        try {
            final long xid = insertHeldTransaction(transaction);
            final Iterator items = transaction.getItems();
            while (items.hasNext()) {
                final TransactionItem item = (TransactionItem) items.next();
                insertHeldTransactionItem(xid, item);
            }
            newid = Long.toString(xid);
        } catch (SQLException e) {
            rollbackUT();
            System.err.println(e.getSQLState());
            e.printStackTrace(System.err);
            throw new LowlevelLedgerException(this, e);
        }
        return this.createHeldTransaction(transaction, newid);
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

    private long insertTransaction(final UnPostedTransaction transaction) throws SQLException, LowlevelLedgerException {
        final PreparedStatement tranInsert = prepQuery("insert into transaction (value_date,comment,ledgerid) values (?,?,?)");
        tranInsert.setTimestamp(1, SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(2, transaction.getComment());
        tranInsert.setString(3, getId());
        tranInsert.execute();
        final PreparedStatement tranID = prepQuery("select last_insert_id()");
        final ResultSet rs = tranID.executeQuery();
        if (rs.next())
            return rs.getLong(1);
         else {
            rollbackUT();
            throw new LowlevelLedgerException(this, "We couldnt get the id of the transaction. Safer to Rollback.");
        }
    }

    private long insertHeldTransaction(final UnPostedHeldTransaction transaction) throws SQLException, LowlevelLedgerException {
        final PreparedStatement tranInsert = prepQuery("insert into held_transaction (value_date,comment,held_until,ledgerid) values (?,?,?,?)");
        tranInsert.setTimestamp(3, SQLTools.toTimestamp(transaction.getExpiryTime()));

        tranInsert.setTimestamp(1, SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(2, transaction.getComment());
        tranInsert.setString(4, getId());
        tranInsert.execute();
        final PreparedStatement tranID = prepQuery("select id from held_transaction where id=last_insert_id()");
        final ResultSet rs = tranID.executeQuery();
        if (rs.next())
            return rs.getLong(1);
         else {
            rollbackUT();
            throw new LowlevelLedgerException(this, "We couldnt get the id of the transaction. Safer to Rollback.");
        }
    }

    private void insertTransactionItem(final long xid, final TransactionItem item) throws SQLException, LowlevelLedgerException {
        final PreparedStatement itemInsert = prepQuery("insert into entry (transactionid,accountid,amount,ack) values (?,?,?,1)");
        itemInsert.setLong(1, xid);
        itemInsert.setString(2, item.getBook().getBookID());
        itemInsert.setDouble(3, item.getAmount());
        itemInsert.execute();
    }

    private void insertHeldTransactionItem(final long xid, final TransactionItem item) throws SQLException, LowlevelLedgerException {
        final PreparedStatement itemInsert = prepQuery("insert into held_entry (held_transactionid,accountid,amount,ack) values (?,?,?,1)");
        itemInsert.setLong(1, xid);
        itemInsert.setString(2, item.getBook().getBookID());
        itemInsert.setDouble(3, item.getAmount());
        itemInsert.execute();
    }

    /**
     * Searches for a Transaction based on its Transaction ID
     * 
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public final PostedTransaction findTransaction(final String idstring) throws LowlevelLedgerException, UnknownTransactionException {
        final long id = Long.parseLong(idstring);
        try {

            PreparedStatement stmt = prepQuery("select value_date,comment from transaction where id=? and ledgerid=?");
            stmt.setLong(1, id);
            stmt.setString(2, getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new UnknownTransactionException(this, idstring);
            }
            final Date started = rs.getTimestamp(1);
            final String comment = rs.getString(2);

            final UnPostedTransaction transaction = new UnPostedTransaction(this, comment, started);
            stmt = prepQuery("select accountid,amount from entry where transactionid=?");
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)), rs.getDouble(2));

            return this.createTransaction(transaction, idstring);

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
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public final PostedHeldTransaction findHeldTransaction(final String idstring) throws LowlevelLedgerException, UnknownTransactionException {
        final long id = Long.parseLong(idstring);
        try {

            PreparedStatement stmt = prepQuery("select value_date,held_until,comment from held_transaction where id=? and ledgerid=?");
            stmt.setLong(1, id);
            stmt.setString(2, getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new UnknownTransactionException(this, idstring);
            }
            final Date started = rs.getTimestamp(1);
            final Date ended = rs.getTimestamp(2);
            final String comment = rs.getString(3);

            final UnPostedHeldTransaction transaction = new UnPostedHeldTransaction(this, comment, started, ended);
            stmt = prepQuery("select accountid,amount from held_entry where held_transactionid=?");
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)), rs.getDouble(2));

            return this.createHeldTransaction(transaction, idstring);

        } catch (SQLException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(this, e);
        } catch (UnknownBookException e) {
            throw new LowlevelLedgerException(this, e);
        }
    }

    private PreparedStatement prepQuery(final String sql) throws SQLException, LowlevelLedgerException {
        return getConnection().prepareStatement(sql);
    }

    /* (non-Javadoc)
     * @see org.neuclear.ledger.Ledger#getBalance(org.neuclear.ledger.Book, java.util.Date)
     */
    public final double getBalance(final Book book, final Date time) throws LowlevelLedgerException {
        try {
            final PreparedStatement stmt = prepQuery("select sum(e.amount) from entry e,transaction t where e.transactionid=t.id and e.accountid=? and t.value_date<= ? and t.ledgerid=?");

            stmt.setString(1, book.getBookID());
            stmt.setTimestamp(2, new Timestamp(time.getTime()));
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
                    "where e.transactionid=t.id and e.accountid=? and t.value_date<= ? and t.ledgerid=?"
                    + "union " +
                    "select sum( e.amount) as amount from held_entry e, held_transaction t " +
                    "where " +
                    "e.held_transactionid=t.id and e.accountid=? and t.value_date<= ? " +
                    "and e.amount<0 and t.held_until>= ? and t.cancelled=0 and t.transactionid is null and t.ledgerid=?"
                    + ") u "
            );
            final Timestamp ts = SQLTools.toTimestamp(time);
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

    private Connection getConnection() throws LowlevelLedgerException {
        try {
            return con.getConnection();
        } catch (SQLException e) {
            throw new LowlevelLedgerException(e);
        } catch (IOException e) {
            throw new LowlevelLedgerException(e);
        }
    }


    public final Book getBook(final String bookID) throws UnknownBookException, LowlevelLedgerException {
        try {
            final PreparedStatement stmt = prepQuery("select screenname from account where id=?");
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
        try {
            con.close();
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
    }

    private final SQLContext con;
}
