package org.neuclear.ledger;

import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.commons.sql.DefaultXAConnectionSource;
import org.neuclear.commons.sql.statements.SimpleStatementFactory;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.time.TimeTools;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.io.IOException;

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

$Id: PopulateLedger.java,v 1.1 2004/01/02 23:18:34 pelle Exp $
$Log: PopulateLedger.java,v $
Revision 1.1  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

*/

/**
 * User: pelleb
 * Date: Jan 2, 2004
 * Time: 3:49:52 PM
 */
public class PopulateLedger {
    public final static String LEDGERID="neu://test/bux";
    public static void main(String args[]){
        try {
            Ledger ledger=new SQLLedger(new SimpleStatementFactory(new DefaultXAConnectionSource()),LEDGERID);
            Book bob=ledger.createNewBook("neu://bob@test");
            Book alice=ledger.createNewBook("neu://alice@test");
            for (int i=0;i<20;i++){
                System.out.println("Performing transfer number: "+i);
                bob.transfer(alice,100+i,"Loan "+i,TimeTools.now());
                alice.transfer(bob,100+2*i,"Repayment"+i,TimeTools.now());
            }
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        } catch (UnknownLedgerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NeuClearException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (BookExistsException e) {
            e.printStackTrace();
        } catch (UnBalancedTransactionException e) {
            e.printStackTrace();
        } catch (InvalidTransactionException e) {
            e.printStackTrace();
        }
    }
}
