package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 2:46:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionExistsException extends InvalidTransactionException {
    public TransactionExistsException(Ledger ledger, String id) {
        super(ledger, "Transaction: " + id + " already exists");
    }
}
