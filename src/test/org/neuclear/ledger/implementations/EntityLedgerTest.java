package org.neuclear.ledger.implementations;

import junit.framework.TestCase;
import org.neuclear.commons.time.TimeTools;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;

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

$Id: EntityLedgerTest.java,v 1.1 2003/11/20 23:41:12 pelle Exp $
$Log: EntityLedgerTest.java,v $
Revision 1.1  2003/11/20 23:41:12  pelle
Getting all the tests to work in id
Removing usage of BC in CryptoTools as it was causing issues.
First version of EntityLedger that will use OFB's EntityEngine. This will allow us to support a vast amount databases without
writing SQL. (Yipee)

*/

/**
 * User: pelleb
 * Date: Nov 20, 2003
 * Time: 2:20:13 PM
 */
public class EntityLedgerTest extends TestCase {

    public void testEntityEngine() throws GenericEntityException {
        //Instantiate the delegator.
        GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
        assertNotNull("Error creating delegator object", delegator);

        GenericValue ledgerValue = delegator.makeValue("Ledger",
                UtilMisc.toMap("id", "neu://test/bux",
                        "title", "Test Ledger",
                        "date_created", TimeTools.now()));
        GenericValue book = delegator.create(ledgerValue);
        assertNotNull("Couldn't create Book entity", book);

        // Find book by primary key
        GenericValue foundBook = delegator.findByPrimaryKey("Ledger",
                UtilMisc.toMap("id", "neu://test"));
        assertNotNull("Couldn't find Ledger", foundBook);
    }
}
