package org.neuclear.ledger.browser;

import org.neuclear.ledger.Book;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.commons.Utility;

import java.sql.*;
import java.security.Principal;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: QueryStatement.java,v 1.1 2003/12/31 00:39:04 pelle Exp $
$Log: QueryStatement.java,v $
Revision 1.1  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added Statement pattern to ledger, simplifying the statement writing process.

*/

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:30:19 PM
 */
public class QueryStatement extends Statement {
    public QueryStatement(Book book,Connection con) throws SQLException {
        this(book,executeQuery(con,book));
    }
    public QueryStatement(Book book,Connection con,Timestamp from, Timestamp until) throws SQLException {
        this(book,executeRangeQuery(con,book,from,until));
    }
    public QueryStatement(Book book,Connection con,Timestamp from) throws SQLException {
        this(book,executeFromQuery(con,book,from));
    }
    private QueryStatement(Book book,ResultSet rs) throws SQLException {
        super(book);
        this.rs=rs;
        next=rs.next();
    }

    private static ResultSet executeRangeQuery(Connection con,Book book,Timestamp from, Timestamp until) throws SQLException {
        final PreparedStatement stmt = con.prepareStatement(RANGE_QUERY);
        stmt.setTimestamp(3,from);
        stmt.setTimestamp(4,until);
        return executeQuery(stmt,book);
    }
    private static ResultSet executeFromQuery(Connection con,Book book,Timestamp from) throws SQLException {
        final PreparedStatement stmt = con.prepareStatement(FROM_QUERY);
        stmt.setTimestamp(3,from);
        return executeQuery(stmt,book);
    }
    private static ResultSet executeUntilQuery(Connection con,Book book, Timestamp until) throws SQLException {
        final PreparedStatement stmt = con.prepareStatement(UNTIL_QUERY);
        stmt.setTimestamp(3,until);
        return executeQuery(stmt,book);
    }
    private static ResultSet executeQuery(Connection con,Book book) throws SQLException {
        return executeQuery(con.prepareStatement(FULL_QUERY),book);
    }
    private static ResultSet executeQuery(PreparedStatement stmt,Book book) throws SQLException {
        stmt.setString(1,book.getBookID());
        stmt.setString(2,book.getLedger().getId());
        return stmt.executeQuery();

    }

    public boolean next() throws LowlevelLedgerException {
        try {
            if (!rs.next())
                return false;
            setRow(rs.getString(1),rs.getString(3),rs.getString(4),rs.getTimestamp(2),rs.getBigDecimal(5));
            return true;
        } catch (SQLException e) {
            throw new LowlevelLedgerException(getBook().getLedger(),e);
        }
    }
    private final ResultSet rs;
    private boolean next;
    private static final String BASE_QUERY = "select t.id,t.valuetime, r.bookid,t.comment,s.amount from entry s,entry r, transaction t where s.transactionid=t.id and r.transactionid=t.id and r.id<>s.id\nand s.bookid = ? and t.ledgerid=?";
    private static final String UNTIL_CLAUSE = " AND t.valuetime<=?";
    private static final String FROM_CLAUSE = " AND t.valuetime>?";
    private static final String ORDERBY = " order by t.valuetime,t.id";

    private static final String FULL_QUERY = BASE_QUERY+ORDERBY;
    private static final String RANGE_QUERY = BASE_QUERY+FROM_CLAUSE+UNTIL_CLAUSE+ORDERBY;
    private static final String FROM_QUERY = BASE_QUERY+FROM_CLAUSE+ORDERBY;
    private static final String UNTIL_QUERY = BASE_QUERY+UNTIL_CLAUSE+ORDERBY;

}