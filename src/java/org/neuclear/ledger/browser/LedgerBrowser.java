package org.neuclear.ledger.browser;

import org.neuclear.ledger.Book;
import org.neuclear.ledger.LowlevelLedgerException;

import java.util.Iterator;
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

$Id: LedgerBrowser.java,v 1.2 2004/01/02 23:18:34 pelle Exp $
$Log: LedgerBrowser.java,v $
Revision 1.2  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

Revision 1.1  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added BookBrowser pattern to ledger, simplifying the statement writing process.

*/

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:09:59 PM
 */
public interface LedgerBrowser {
    public BookBrowser browse(Book book) throws LowlevelLedgerException;
    public BookBrowser browseFrom(Book book,Timestamp from) throws LowlevelLedgerException;
//    public BookBrowser browseUntil(Book book,Timestamp until);
    public BookBrowser browseRange(Book  book,Timestamp from, Timestamp until) throws LowlevelLedgerException;
}
