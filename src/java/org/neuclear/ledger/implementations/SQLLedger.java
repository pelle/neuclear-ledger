/*
 * Created on Jul 14, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.neuclear.ledger.implementations;

import java.sql.*;
import java.util.Date;
import java.util.Iterator;
import java.io.IOException;

import org.neuclear.ledger.*;
import org.neuclear.commons.sql.SQLTools;
import org.neuclear.commons.sql.ConnectionSource;

/**
 * @author pelleb
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SQLLedger extends Ledger {

	/**
	 *
	 */
/*    public SQLLedger(String id) throws SQLException, IOException, UnknownLedgerException {
        this(SQLTools.getConnection(),id);
    }
*/
	public SQLLedger(ConnectionSource con,String id) throws LowlevelLedgerException, UnknownLedgerException {
            super(id,getLedgerName(con,id));
            this.con=con;
    }
    private static String getLedgerName(ConnectionSource con,String id) throws  UnknownLedgerException,  LowlevelLedgerException {
        try {
            PreparedStatement stmt=con.getConnection().prepareStatement("select title from ledger where id=?");
            stmt.setString(1,id);
            ResultSet rs=stmt.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else throw new UnknownLedgerException(id);
        } catch (SQLException e) {
            throw new LowlevelLedgerException(e);
        } catch (IOException e) {
            throw new LowlevelLedgerException(e);
        }
    }
    /**
     * This decides if new books are automatically created.
     * @return
     */
    public boolean allowAutoBookCreation() {
        return false;
    }

    public boolean bookExists(String bookID) throws LowlevelLedgerException {
     try {
         PreparedStatement stmt=prepQuery("select id from account where id=?");
         stmt.setString(1,bookID);
         ResultSet rs=stmt.executeQuery();
         return rs.next();
     } catch (SQLException e) {
         throw new LowlevelLedgerException(this,e);
     }
    }

    public Book createNewBook(String bookID, String title) throws BookExistsException,LowlevelLedgerException {
        if (bookExists(bookID))
            throw new BookExistsException(this,bookID);
        try {
            getConnection().setAutoCommit(false);
            PreparedStatement stmt=prepQuery("insert into account values (?,?,3)");
            stmt.setString(1,bookID);
            stmt.setString(2,title);
            stmt.execute();
            getConnection().commit();
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }

        return createBookInstance(bookID,title);
    }

    /* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#performTransaction(org.neuclear.ledger.UnPostedTransaction)
	 */
	public PostedTransaction performTransaction(UnPostedTransaction transaction) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
		String newid;
        if (!transaction.isBalanced()){
             throw new UnBalancedTransactionException(this,transaction);
        }
        try {
            getConnection().setAutoCommit(false);

            long xid=insertTransaction(transaction);
            Iterator items=transaction.getItems();
            while (items.hasNext()) {
                TransactionItem item = (TransactionItem) items.next();
                insertTransactionItem(xid, item);
            }
            getConnection().commit();
            newid=Long.toString(xid);
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException e1) {
                throw new LowlevelLedgerException(this,e1);
            }
            throw new LowlevelLedgerException(this,e);
        }

        return this.createTransaction(transaction,newid);
	}

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * @param transaction Transaction to perform
     * @return Unique ID
     */
    public PostedHeldTransaction performHeldTransaction(UnPostedHeldTransaction transaction) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        String newid;
        if (!transaction.isBalanced()){
             throw new UnBalancedTransactionException(this,transaction);
        }
        try {
            getConnection().setAutoCommit(false);

            long xid=insertHeldTransaction(transaction);
            Iterator items=transaction.getItems();
            while (items.hasNext()) {
                TransactionItem item = (TransactionItem) items.next();
                insertHeldTransactionItem(xid, item);
            }
            getConnection().commit();
            newid=Long.toString(xid);
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException e1) {
                throw new LowlevelLedgerException(this,e1);
            }
            System.err.println(e.getSQLState());
            e.printStackTrace(System.err);
            throw new LowlevelLedgerException(this,e);
        }

        return this.createHeldTransaction(transaction,newid);
    }
    /**
     * Cancels a Held Transaction.
     * @param hold
     * @throws LowlevelLedgerException
     * @throws UnknownTransactionException
     */
    public void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        try {
            PreparedStatement update=prepQuery("update held_transaction set cancelled=1 where id=? and ledgerid=?");
            update.setString(1,hold.getXid());
            update.setString(2,getId());
            int affected=update.executeUpdate();
            if (affected==0)
                throw new UnknownTransactionException(this,hold.getXid());
            if (affected>1)    {
                getConnection().rollback();
                throw new LowlevelLedgerException(this,"performCancelHold: For some reason multiple rows were updated. Transaction Rolled Back.");
            }
            getConnection().commit();
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
    }

    public PostedTransaction performCompleteHold(PostedHeldTransaction hold,double amount, Date time, String comment) throws TransactionExpiredException, InvalidTransactionException, LowlevelLedgerException {
        try {
            PreparedStatement query=prepQuery("select * from held_transaction where cancelled=0 and transactionid is null and id=? and ledgerid=?");
            query.setString(1,hold.getXid());
            query.setString(2,getId());
            ResultSet rs=query.executeQuery();
            if (!rs.next())
                throw new  TransactionExpiredException(this,hold);
            PostedTransaction tran=createHeldComplete(hold,amount,time,comment);

            PreparedStatement update=prepQuery("update held_transaction set transactionid=? where id=? and ledgerid=?");
            update.setString(1,tran.getXid());
            update.setString(2,hold.getXid());
            update.setString(3,getId());
            int affected=update.executeUpdate();
            if (affected==0)
                throw new UnknownTransactionException(this,hold.getXid());
            if (affected>1)    {
                getConnection().rollback();
                throw new LowlevelLedgerException(this,"performCompleteHold: For some reason multiple rows were updated. Transaction Rolled Back.");
            }
            getConnection().commit();

            return tran;

        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        } catch (UnknownTransactionException e) {
            throw new LowlevelLedgerException(this,e);
        }

    }

    private long insertTransaction(UnPostedTransaction transaction) throws SQLException, LowlevelLedgerException{

        PreparedStatement tranInsert;
        tranInsert=prepQuery("insert into transaction (value_date,comment,ledgerid) values (?,?,?)");
        tranInsert.setTimestamp(1,SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(2,transaction.getComment());
        tranInsert.setString(3,getId());
        tranInsert.execute();
        PreparedStatement tranID=prepQuery("select last_insert_id()");
        ResultSet rs=tranID.executeQuery();
        if (rs.next())
            return rs.getLong(1);
        else {
            getConnection().rollback();
            throw new LowlevelLedgerException(this,"We couldnt get the id of the transaction. Safer to Rollback.");
        }
    }

    private long insertHeldTransaction(UnPostedHeldTransaction transaction) throws SQLException, LowlevelLedgerException{

        PreparedStatement tranInsert;
        tranInsert=prepQuery("insert into held_transaction (value_date,comment,held_until,ledgerid) values (?,?,?,?)");
        tranInsert.setTimestamp(3,SQLTools.toTimestamp(transaction.getExpiryTime()));

        tranInsert.setTimestamp(1,SQLTools.toTimestamp(transaction.getTransactionTime()));
        tranInsert.setString(2,transaction.getComment());
        tranInsert.setString(4,getId());
        tranInsert.execute();
        PreparedStatement tranID=prepQuery("select id from held_transaction where id=last_insert_id()");
        ResultSet rs=tranID.executeQuery();
        if (rs.next())
            return rs.getLong(1);
        else {
            getConnection().rollback();
            throw new LowlevelLedgerException(this,"We couldnt get the id of the transaction. Safer to Rollback.");
        }
    }

    private void insertTransactionItem(long xid, TransactionItem item) throws SQLException, LowlevelLedgerException{
        PreparedStatement itemInsert=prepQuery("insert into entry (transactionid,accountid,amount,ack) values (?,?,?,1)");
        itemInsert.setLong(1,xid);
        itemInsert.setString(2,item.getBook().getBookID());
        itemInsert.setDouble(3,item.getAmount());
        itemInsert.execute();
    }

    private void insertHeldTransactionItem(long xid, TransactionItem item) throws SQLException, LowlevelLedgerException{
        PreparedStatement itemInsert=prepQuery("insert into held_entry (held_transactionid,accountid,amount,ack) values (?,?,?,1)");
        itemInsert.setLong(1,xid);
        itemInsert.setString(2,item.getBook().getBookID());
        itemInsert.setDouble(3,item.getAmount());
        itemInsert.execute();
    }

    /**
		 * Searches for a Transaction based on its Transaction ID
		 * @param idstring A valid ID
		 * @return The Transaction object
		 */
	public PostedTransaction findTransaction(String idstring) throws  LowlevelLedgerException, UnknownTransactionException {
		long id=Long.parseLong(idstring);
		try {

			PreparedStatement stmt=prepQuery("select value_date,comment from transaction where id=? and ledgerid=?");
			stmt.setLong(1,id);
            stmt.setString(2,getId());
			ResultSet rs=stmt.executeQuery();
			if (!rs.next()) {
				throw new UnknownTransactionException(this,idstring);
			}
			Date started=rs.getTimestamp(1);
			String comment=rs.getString(2);

			UnPostedTransaction transaction=new UnPostedTransaction(this,comment,started);
            stmt=prepQuery("select accountid,amount from entry where transactionid=?");
            stmt.setLong(1,id);
            rs=stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)),rs.getDouble(2));

			return this.createTransaction(transaction,idstring);

		} catch (SQLException e) {
			throw new LowlevelLedgerException(this,e);
		} catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(this,e);
        } catch (UnknownBookException e) {
            throw new LowlevelLedgerException(this,e);
        }
	}

    /**
		 * Searches for a Held Transaction based on its Transaction ID
		 * @param idstring A valid ID
		 * @return The Transaction object
		 */
	public PostedHeldTransaction findHeldTransaction(String idstring) throws  LowlevelLedgerException, UnknownTransactionException {
		long id=Long.parseLong(idstring);
		try {

			PreparedStatement stmt=prepQuery("select value_date,held_until,comment from held_transaction where id=? and ledgerid=?");
			stmt.setLong(1,id);
            stmt.setString(2,getId());
			ResultSet rs=stmt.executeQuery();
			if (!rs.next()) {
				throw new UnknownTransactionException(this,idstring);
			}
			Date started=rs.getTimestamp(1);
			Date ended=rs.getTimestamp(2);
			String comment=rs.getString(3);

			UnPostedHeldTransaction transaction=new UnPostedHeldTransaction(this,comment,started,ended);
            stmt=prepQuery("select accountid,amount from held_entry where held_transactionid=?");
            stmt.setLong(1,id);
            rs=stmt.executeQuery();
            while (rs.next())
                transaction.addItem(getBook(rs.getString(1)),rs.getDouble(2));

			return this.createHeldTransaction(transaction,idstring);

		} catch (SQLException e) {
			throw new LowlevelLedgerException(this,e);
		} catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(this,e);
        } catch (UnknownBookException e) {
            throw new LowlevelLedgerException(this,e);
        }
	}

	private PreparedStatement prepQuery(String sql) throws SQLException,LowlevelLedgerException {
		return getConnection().prepareStatement(sql);
	}

	/* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#getBalance(org.neuclear.ledger.Book, java.util.Date)
	 */
	public double getBalance(Book book, Date time) throws LowlevelLedgerException {
        try {
            PreparedStatement stmt=prepQuery("select sum(e.amount) from entry e,transaction t where e.transactionid=t.id and e.accountid=? and t.value_date<= ? and t.ledgerid=?");

            stmt.setString(1,book.getBookID());
            stmt.setTimestamp(2,new Timestamp(time.getTime()));
            stmt.setString(3,getId());

            ResultSet rs=stmt.executeQuery();
            if (rs.next()){
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
        return 0;
 	}

	/* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#getBalance(org.neuclear.ledger.Book)
	 */
	public double getBalance(Book book) throws LowlevelLedgerException {
       return getBalance(book,new Date());
	}

	public double getAvailableBalance(Book book, Date time) throws LowlevelLedgerException {
        try {
            PreparedStatement stmt=prepQuery("select sum(u.amount) from (" +
                    "select sum(e.amount) as amount from entry e,transaction t " +
                        "where e.transactionid=t.id and e.accountid=? and t.value_date<= ? and t.ledgerid=? " +
                    "union " +
                    "select sum(e.amount) as amount from held_entry e, held_transaction t " +
                    "where " +
                        "e.held_transactionid=t.id and e.accountid=? and t.value_date<= ? " +
                        "and e.amount<0 and t.held_until>= ? and t.cancelled=0 and t.transactionid is null and t.ledgerid=?" +
                    ") u ");
            Timestamp ts=SQLTools.toTimestamp(time);
            stmt.setString(1,book.getBookID());
            stmt.setTimestamp(2,ts);
            stmt.setString(3,getId());
            stmt.setString(4,book.getBookID());
            stmt.setTimestamp(5,ts);
            stmt.setTimestamp(6,ts);
            stmt.setString(7,getId());

            ResultSet rs=stmt.executeQuery();
            if (rs.next()){
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
        return 0;
	}


    /* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#getAvailableBalance(org.neuclear.ledger.Book)
	 */
	public double getAvailableBalance(Book book) throws LowlevelLedgerException {
		return getAvailableBalance(book,new Date());
	}

	/* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#beginLinkedTransaction()
	 */
	public void beginLinkedTransaction() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.neuclear.ledger.Ledger#endLinkedTransactions()
	 */
	public void endLinkedTransactions() {
		// TODO Auto-generated method stub

	}

    public String toString() {
        return "SQL Ledger: "+getName();
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
	private ConnectionSource con;

    public Book getBook(String bookID) throws UnknownBookException,LowlevelLedgerException {
        try {
            PreparedStatement stmt=prepQuery("select screenname from account where id=?");
            stmt.setString(1,bookID);
            ResultSet rs=stmt.executeQuery();
            if(rs.next())
                return createBookInstance(bookID,rs.getString(1));
        } catch (SQLException e) {
            throw new LowlevelLedgerException(this,e);
        }
        throw new UnknownBookException(this,bookID);
    }
}
