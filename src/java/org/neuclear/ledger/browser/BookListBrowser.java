package org.neuclear.ledger.browser;

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

import org.neuclear.ledger.LowlevelLedgerException;

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:26:52 PM
 */
public abstract class BookListBrowser {
    public BookListBrowser(String ledger) {
        this.ledger = ledger;
    }

    public abstract boolean next() throws LowlevelLedgerException;


    protected final void setRow(String book, String source, String nickname, int count, double balance) {
        this.book = book;
        this.source = source;
        this.nickname = nickname;
        this.count = count;
        this.balance = balance;
    }

    public String getBook() {
        return book;
    }

    public String getLedger() {
        return ledger;
    }

    public String getSource() {
        return source;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCount() {
        return count;
    }

    public double getBalance() {
        return balance;
    }

    private final String ledger;
    private String book;
    private String source;
    private String nickname;
    private int count;
    private double balance;
}
