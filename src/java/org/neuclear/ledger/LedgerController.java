package org.neuclear.ledger;

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

$Id: LedgerController.java,v 1.2 2004/03/31 23:11:10 pelle Exp $
$Log: LedgerController.java,v $
Revision 1.2  2004/03/31 23:11:10  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

Revision 1.1  2004/03/29 23:43:29  pelle
The servlets now work and display the ledger contents.

*/

/**
 * User: pelleb
 * Date: Mar 29, 2004
 * Time: 9:35:30 PM
 */
public abstract class LedgerController {
    public abstract void createLedger(String id);

    public abstract boolean ledgerExists(String id);

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public abstract PostedTransaction performTransaction(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * Similar to a transaction but guarantees that there wont be any negative balances left after the transaction.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public abstract PostedTransaction performVerifiedTransfer(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This transaction guarantees to not leave a negative balance in any account.
     *
     * @param trans Transaction to perform
     */
    public abstract PostedHeldTransaction performHeldTransfer(UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * Cancels a Held Transaction.
     *
     * @param hold
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     * @throws org.neuclear.ledger.UnknownTransactionException
     *
     */
    public abstract void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException;

    /**
     * Completes a held transaction. Which means:
     * cancelling the hold and performing the transfer with the given updated amount and comment.
     *
     * @param hold    HeldTransaction to complete
     * @param amount  The updatd amount. It must be <= than the amount of the hold
     * @param comment
     * @return
     * @throws InvalidTransactionException
     * @throws LowlevelLedgerException
     * @throws TransactionExpiredException
     */
    public abstract PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException, UnknownTransactionException;

    /**
     * Searches for a Transaction based on its Transaction ID
     *
     * @param id A valid ID
     * @return The Transaction object
     */
    public abstract Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException, InvalidTransactionException, UnknownBookException;

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and credit= 'neu://BOB'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://BOB'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public abstract double getBalance(String book) throws LowlevelLedgerException;

    /**
     * Calculate the available balance at a given time. This DOES take into account any held transactions.
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and (end_date is null or end_date>= sysdate) and credit= 'neu://BOB'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://BOB'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public abstract double getAvailableBalance(String book) throws LowlevelLedgerException;

    /**
     * Searches for a Held Transaction based on its Transaction ID
     *
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public abstract PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException;

    public abstract double getTestBalance() throws LowlevelLedgerException;

    public abstract void close() throws LowlevelLedgerException;


}
