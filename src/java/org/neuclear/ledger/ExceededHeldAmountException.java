package org.neuclear.ledger;

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

$Id: ExceededHeldAmountException.java,v 1.1 2004/03/31 23:11:10 pelle Exp $
$Log: ExceededHeldAmountException.java,v $
Revision 1.1  2004/03/31 23:11:10  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

*/

/**
 * User: pelleb
 * Date: Mar 31, 2004
 * Time: 9:33:02 PM
 */
public class ExceededHeldAmountException extends InvalidTransactionException {
    public ExceededHeldAmountException(final PostedHeldTransaction tran, final double amount) {
        super(null, "Invalid Amount for Held Transaction: " + amount + " is higher than held amount: " + tran.getAmount());
    }
}
