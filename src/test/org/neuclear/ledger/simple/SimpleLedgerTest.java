package org.neuclear.ledger.simple;

import org.neuclear.commons.NeuClearException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownLedgerException;
import org.neuclear.ledger.tests.AbstractLedgerTest;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 16, 2003
 * Time: 12:58:30 PM
 * To change this template use Options | File Templates.
 */
public final class SimpleLedgerTest extends AbstractLedgerTest {
    public SimpleLedgerTest(final String s) throws LowlevelLedgerException, UnknownLedgerException, SQLException, NamingException, IOException, NeuClearException {
        super(s);
    }

    public final LedgerController createLedger() {
        return new SimpleLedgerController("test");
    }


}
