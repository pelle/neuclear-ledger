package org.neuclear.ledger;

import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.commons.sql.SQLTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 16, 2003
 * Time: 12:58:30 PM
 * To change this template use Options | File Templates.
 */
public final class SQLLedgerTest extends LedgerTest{
    public SQLLedgerTest(final String s) throws LowlevelLedgerException, UnknownLedgerException {
        super(s);
    }

    public final Ledger createLedger() {
        return null;  //To change body of implemented methods use Options | File Templates.
    }


}
