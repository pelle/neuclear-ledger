package org.neuclear.ledger.simple;

import org.neuclear.ledger.Book;
import org.neuclear.ledger.PostedTransaction;

import java.util.*;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * User: pelleb
 * Date: Apr 19, 2004
 * Time: 10:59:44 AM
 */
public class SimpleBook extends Book {

    public SimpleBook(SimpleBook orig, String nickname, String type, Date updated, String source, String registrationid) {
        super(orig.getId(), nickname, type, source, orig.getRegistered(), updated, registrationid);
        transactions = orig.transactions;
        balances = orig.balances;
    }

    public SimpleBook(String id, String nickname, String type, String source, Date registered, Date updated, String registrationid) {
        super(id, nickname, type, source, registered, updated, registrationid);
        transactions = new LinkedList();
        balances = new HashMap();
    }

    public SimpleBook(String id, Date registered) {
        super(id, registered);
        transactions = new LinkedList();
        balances = new HashMap();
    }

    void add(PostedTransaction tran) {
        transactions.add(tran);
    }

    public synchronized void updateBalance(final String ledger, final double amount) {
        Double balance = (Double) balances.get(ledger);
        if (balance == null)
            balances.put(ledger, new Double(amount));
        else
            balances.put(ledger, new Double(amount + balance.doubleValue()));
    }

    public double getBalance(final String ledger) {
        if (balances.containsKey(ledger))
            return ((Double) balances.get(ledger)).doubleValue();
        return 0.0;
    }

    public Iterator iterator() {
        return transactions.iterator();
    }

    public Iterator ledgerIterator() {
        return balances.keySet().iterator();
    }

    private final List transactions;
    private final Map balances;
//    private double held=0;
}
