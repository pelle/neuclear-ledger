package org.neuclear.ledger.browser;

import org.neuclear.commons.sql.statements.StatementFactory;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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

$Id: QueryBookBrowser.java,v 1.2 2004/03/21 00:48:35 pelle Exp $
$Log: QueryBookBrowser.java,v $
Revision 1.2  2004/03/21 00:48:35  pelle
The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.

Revision 1.1  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

Revision 1.1  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added BookBrowser pattern to ledger, simplifying the statement writing process.

*/

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:30:19 PM
 */
public class QueryBookBrowser extends BookBrowser {
    public QueryBookBrowser(Ledger ledger, String book, StatementFactory fact) throws SQLException {
        this(ledger, book, executeQuery(fact, ledger.getId(), book));
    }

    public QueryBookBrowser(Ledger ledger, String book, StatementFactory fact, Timestamp from, Timestamp until) throws SQLException {
        this(ledger, book, executeRangeQuery(fact, ledger.getId(), book, from, until));
    }

    public QueryBookBrowser(Ledger ledger, String book, StatementFactory fact, Timestamp from) throws SQLException {
        this(ledger, book, executeFromQuery(fact, ledger.getId(), book, from));
    }

    private QueryBookBrowser(Ledger ledger, String book, ResultSet rs) throws SQLException {
        super(book);
        this.rs = rs;
        next = rs.next();
        this.ledger = ledger;
    }

    private static ResultSet executeRangeQuery(StatementFactory fact, String ledgerid, String book, Timestamp from, Timestamp until) throws SQLException {
        final PreparedStatement stmt = fact.prepareStatement(RANGE_QUERY);
        stmt.setTimestamp(3, from);
        stmt.setTimestamp(4, until);
        return executeQuery(stmt, ledgerid, book);
    }

    private static ResultSet executeFromQuery(StatementFactory fact, String ledgerid, String book, Timestamp from) throws SQLException {
        final PreparedStatement stmt = fact.prepareStatement(FROM_QUERY);
        stmt.setTimestamp(3, from);
        return executeQuery(stmt, ledgerid, book);
    }

    private static ResultSet executeUntilQuery(StatementFactory fact, String ledgerid, String book, Timestamp until) throws SQLException {
        final PreparedStatement stmt = fact.prepareStatement(UNTIL_QUERY);
        stmt.setTimestamp(3, until);
        return executeQuery(stmt, ledgerid, book);
    }

    private static ResultSet executeQuery(StatementFactory fact, String ledgerid, String book) throws SQLException {
        return executeQuery(fact.prepareStatement(FULL_QUERY), ledgerid, book);
    }

    private static ResultSet executeQuery(PreparedStatement stmt, String ledgerid, String book) throws SQLException {
        stmt.setString(1, book);
        stmt.setString(2, ledgerid);
        return stmt.executeQuery();

    }

    public boolean next() throws LowlevelLedgerException {
        try {
            if (!rs.next())
                return false;
            setRow(rs.getString(1), rs.getString(3), rs.getString(4), rs.getTimestamp(2), rs.getBigDecimal(5));
            return true;
        } catch (SQLException e) {
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    private final ResultSet rs;
    private final Ledger ledger;
    private boolean next;
    private static final String BASE_QUERY = "select t.id,t.valuetime, r.bookid,t.comment,s.amount from entry s,entry r, transaction t where s.transactionid=t.id and r.transactionid=t.id and r.id<>s.id\nand s.bookid = ? and t.ledgerid=?";
    private static final String UNTIL_CLAUSE = " AND t.valuetime<=?";
    private static final String FROM_CLAUSE = " AND t.valuetime>?";
    private static final String ORDERBY = " order by t.valuetime,t.id";

    private static final String FULL_QUERY = BASE_QUERY + ORDERBY;
    private static final String RANGE_QUERY = BASE_QUERY + FROM_CLAUSE + UNTIL_CLAUSE + ORDERBY;
    private static final String FROM_QUERY = BASE_QUERY + FROM_CLAUSE + ORDERBY;
    private static final String UNTIL_QUERY = BASE_QUERY + UNTIL_CLAUSE + ORDERBY;

}
