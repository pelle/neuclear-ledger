package org.neuclear.ledger.browser;

import org.neuclear.ledger.LowlevelLedgerException;

import java.util.Date;

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

$Id: LedgerBrowser.java,v 1.6 2004/05/05 14:04:50 pelle Exp $
$Log: LedgerBrowser.java,v $
Revision 1.6  2004/05/05 14:04:50  pelle
Added BookListBrowser

Revision 1.5  2004/05/03 23:54:18  pelle
HibernateLedgerController now supports multiple ledgers.
Fixed many unit tests.

Revision 1.4  2004/03/26 23:36:34  pelle
The simple browse(book) now works on hibernate, I have implemented the other two, which currently don not constrain the query correctly.

Revision 1.3  2004/03/21 00:48:35  pelle
The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.

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
    public BookBrowser browse(String book) throws LowlevelLedgerException;

    public BookBrowser browse(String ledger, String book) throws LowlevelLedgerException;

    public BookBrowser browseFrom(String ledger, String book, Date from) throws LowlevelLedgerException;

    public BookBrowser browseRange(String ledger, String book, Date from, Date until) throws LowlevelLedgerException;

    public BookBrowser browseFrom(String book, Date from) throws LowlevelLedgerException;

    public BookBrowser browseRange(String book, Date from, Date until) throws LowlevelLedgerException;

    public BookListBrowser browseBooks(String ledger) throws LowlevelLedgerException;
}
