package org.neuclear.ledger.browser;

import org.neuclear.ledger.LowlevelLedgerException;

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

$Id: Browser.java,v 1.1 2004/05/05 20:46:23 pelle Exp $
$Log: Browser.java,v $
Revision 1.1  2004/05/05 20:46:23  pelle
BookListBrowser works both in SimpleLedgerController and HibernateLedgerController
Added new interface Browser, which is implemented by both BookBrowser and BookListBrowser

*/

/**
 * User: pelleb
 * Date: May 5, 2004
 * Time: 5:33:46 PM
 */
public interface Browser {
    boolean next() throws LowlevelLedgerException;
}
