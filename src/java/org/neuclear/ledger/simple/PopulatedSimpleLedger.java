package org.neuclear.ledger.simple;

import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;

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

$Id: PopulatedSimpleLedger.java,v 1.3 2004/04/27 15:23:54 pelle Exp $
$Log: PopulatedSimpleLedger.java,v $
Revision 1.3  2004/04/27 15:23:54  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

Revision 1.2  2004/04/23 19:09:15  pelle
Lots of cleanups and improvements to the userinterface and look of the bux application.

Revision 1.1  2004/03/29 23:43:30  pelle
The servlets now work and display the ledger contents.

*/

/**
 * User: pelleb
 * Date: Mar 29, 2004
 * Time: 7:36:18 PM
 */
public class PopulatedSimpleLedger extends SimpleLedgerController {
    public PopulatedSimpleLedger(String name) throws InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        super(name);

        transfer("mint", "bob", 10000, "Initial Funding");
        transfer("mint", "alice", 10000, "Initial Funding");

        for (int i = 0; i < 50; i++) {
            transfer("bob", "carol", 50 + i, "test" + i);
        }

        for (int i = 0; i < 50; i++) {
            transfer("alice", "bob", 50 + i, "test" + i);
        }

    }
}
