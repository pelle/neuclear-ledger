package org.neuclear.ledger.servlets;

import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.simple.SimpleLedgerController;

import javax.servlet.ServletConfig;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to create Signers from signers configuration parameters. It keeps a cache of Signers with similar parameters. Thus
 * if you have several Servlets with the same keystore parameters they will use the same instance of Signer<p>
 * The Configuration parameters are as follows:
 * <table border="2"><tr><th>parameter name</th><th>parameter value</th></tr>
 * <tr><td>keystore</td><td>The location of the JCE KeyStore. Defaults to the file .keystore in the users home directory
 * If you specify <tt>test</tt> the built in Test keystore will be used.</td></tr>
 * <tr><td>serviceid</td><td>The main service ID of the service. Ie. neu://superbux.com/ecurrency. This is only required (and used)
 * if you set <tt>keeppassphrase</tt> (see below)</td></tr>
 * <tr><td>passphraseagent</td><td>The type of passphraseagent to use. Valid options are <tt>signers</tt>,
 * <tt>gui</tt>(default) and <tt>console</tt></td></tr>
 * <tr><td>keeppassphrase</td><td>This asks for the service passphrase once at startup and remembers it through the lifetime of the signers</td></tr>
 * </table>
 * <p/>
 * To use the factory. Do as follows within your servlets init() method:
 * <code>Signer signer=ServletSignerFactory.getInstance().createSigner(config);</code>
 *
 * @see org.neuclear.commons.crypto.passphraseagents.PassPhraseAgent
 * @see org.neuclear.commons.crypto.signers.Signer
 */
public final class ServletLedgerFactory {

    private ServletLedgerFactory() {
        map = Collections.synchronizedMap(new HashMap());
    }

    public synchronized LedgerController createLedger(ServletConfig config) throws LowlevelLedgerException {
        final String type = ServletTools.getInitParam("ledger", config);
        final String serviceid = ServletTools.getInitParam("serviceid", config);
        final String hash = getConfigHash(type, serviceid);
        if (map.containsKey(hash))
            return (LedgerController) map.get(hash);

        System.out.println("using ledger: " + type);
        final LedgerController ledger;
        try {
            ledger = createLedger(type, serviceid);
        } catch (Exception e) {
            config.getServletContext().log("ServletLedgerFactory: " + e.getLocalizedMessage());
            throw new LowlevelLedgerException(e);
        }
        map.put(hash, ledger);
        return ledger;
    }

    private LedgerController createLedger(String type, String serviceid) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (type.equals("hibernate"))
            return createLedger(Class.forName("org.neuclear.ledger.hibernate.HibernateLedgerController"), serviceid);
        if (type.equals("prevalent"))
            return createLedger(Class.forName("org.neuclear.ledger.prevalent.PrevalentLedgerController"), serviceid);
        if (type.equals("simple"))
            return new SimpleLedgerController(serviceid);
        return createLedger(Class.forName(type), serviceid);
    }

    private LedgerController createLedger(Class aClass, String serviceid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (LedgerController) aClass.getConstructor(new Class[]{String.class}).newInstance(new String[]{serviceid});
    }


    private static final String getConfigHash(final String type, final String serviceid) {
        return new String(CryptoTools.digest((type + serviceid).getBytes()));
    }

    public synchronized static ServletLedgerFactory getInstance() {
        if (instance == null)
            instance = new ServletLedgerFactory();
        return instance;
    }

    private static ServletLedgerFactory instance;
    final private Map map;
}
