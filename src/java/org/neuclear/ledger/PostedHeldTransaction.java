package org.neuclear.ledger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: pelleb
 * Date: Jul 29, 2003
 * Time: 3:17:16 PM
 */
public final class PostedHeldTransaction extends PostedTransaction implements HeldTransaction {
    public PostedHeldTransaction(final UnPostedHeldTransaction orig, Date time) throws InvalidTransactionException, UnBalancedTransactionException {
        super(orig, time);
        this.expiryTime = orig.getExpiryTime();
    }

    public final Date getExpiryTime() {
        return expiryTime;
    }

    public List getAdjustedItems(final double amount) throws ExceededHeldAmountException, UnBalancedTransactionException {
        final double origAmount = getAmount();
        if (amount > origAmount)
            throw new ExceededHeldAmountException(this, amount);
        final List ol = getItemList();

        final List nl = new ArrayList(ol.size());

        final double ratio = amount / origAmount;
        double balance = 0;
        for (int i = 0; i < ol.size(); i++) {
            TransactionItem item = (TransactionItem) ol.get(i);
            final double itemamount = item.getAmount() * ratio;
            nl.add(new TransactionItem(item.getBook(), itemamount));
            balance += itemamount;
        }
        if (balance != 0)
            throw new UnBalancedTransactionException(null, this, balance);
        return nl;
    }

    final private Date expiryTime;

}
