package org.neuclear.ledger.browser;

import org.neuclear.ledger.Book;

import java.sql.Timestamp;
import java.math.BigDecimal;

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

$Id: StatementEntry.java,v 1.1 2003/12/31 00:39:04 pelle Exp $
$Log: StatementEntry.java,v $
Revision 1.1  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added Statement pattern to ledger, simplifying the statement writing process.

*/

/**
 * This class is used exclusively by the Browser to Generate rows in a ledger statement
 */
public final class StatementEntry {
    StatementEntry(Book book) {
        this.book = book;
    }

    void setRow(String xid,String counterparty,String comment,Timestamp valuetime, BigDecimal amount) {
        this.xid=xid;
        this.counterparty=counterparty;
        this.comment=comment;
        this.valuetime=valuetime;
        this.amount=amount;
    }
    public Book getBook() {
        return book;
    }

    public String getLedgerid() {
        return book.getLedger().getId();
    }

    public String getXid() {
        return xid;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getValuetime() {
        return valuetime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    private final Book book;

    private String xid;
    private String counterparty;
    private String comment;
    private Timestamp valuetime;
    private BigDecimal amount;

}
