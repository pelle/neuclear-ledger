package org.neuclear.ledger.browser;

import org.neuclear.ledger.LowlevelLedgerException;

import java.math.BigDecimal;
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

$Id: BookBrowser.java,v 1.2 2004/03/21 00:48:35 pelle Exp $
$Log: BookBrowser.java,v $
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
 * Time: 4:26:52 PM
 */
public abstract class BookBrowser {
    public BookBrowser(String book) {
        this.book = book;
    }

    public abstract boolean next() throws LowlevelLedgerException;


    protected final void setRow(String xid, String counterparty, String comment, Timestamp valuetime, BigDecimal amount) {
        this.xid = xid;
        this.counterparty = counterparty;
        this.comment = comment;
        this.valuetime = valuetime;
        this.amount = amount;
    }

    public String getBook() {
        return book;
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

    private final String book;

    private String xid;
    private String counterparty;
    private String comment;
    private Timestamp valuetime;
    private BigDecimal amount;
}
