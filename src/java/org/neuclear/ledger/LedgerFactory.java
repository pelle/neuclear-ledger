package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 18, 2003
 * Time: 11:38:33 AM
 * $Id: LedgerFactory.java,v 1.2 2003/10/25 00:39:05 pelle Exp $
 * $Log: LedgerFactory.java,v $
 * Revision 1.2  2003/10/25 00:39:05  pelle
 * Fixed SmtpSender it now sends the messages.
 * Refactored CommandLineSigner. Now it simply signs files read from command line. However new class IdentityCreator
 * is subclassed and creates new Identities. You can subclass CommandLineSigner to create your own variants.
 * Several problems with configuration. Trying to solve at the moment. Updated PicoContainer to beta-2
 *
 * Revision 1.1.1.1  2003/09/20 23:16:17  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.6  2003/08/15 22:39:22  pelle
 * Introducing new neuclear-commons project.
 * The commons project will have all the non application specific common stuff such as database connectivity, configuration etc.
 * Did various refactorings to support this.
 *
 * Revision 1.5  2003/08/14 19:12:28  pelle
 * Configuration is now done in an xml file neuclear-conf.xml
 *
 * Revision 1.4  2003/08/08 23:05:11  pelle
 * Updated to use PicoContainer.
 * This will be made more elegant as we go along.
 *
 * Revision 1.3  2003/08/06 19:16:32  pelle
 * Updated various missing items.
 *
 * Revision 1.2  2003/07/28 21:29:14  pelle
 * Changed a few things in the LedgerFactory.
 * Still not quite there yet.
 *
 * Revision 1.1  2003/01/18 17:18:34  pelle
 * Added LedgerFactory for creating new Ledger Instances
 *
 */

import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.ledger.implementations.SQLLedger;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Currently Simple factory class for creating Ledgers. This needs to be a lot smarter than it is.<p>
 * Basic usage is:<pre>
 * LedgerFactory.getInstance().getLedger("XYZ Widgets Ltd.");
 * </pre>
 */
public class LedgerFactory {
    protected LedgerFactory() throws LowlevelLedgerException {
        ledgerMap = new HashMap();
    }

    /**
     * This attempts to find a ledger in the map. If not it creates a new one and returns it.
     * 
     * @param name Unique Ledger Name
     * @return Ledger
     */
    public final Ledger getLedger(String name) throws LedgerCreationException {
        try {
            return (Ledger) Configuration.getContainer(Ledger.class).getComponentInstance(Ledger.class);
        } catch (ConfigurationException e) {
            throw new LedgerCreationException(e);
        }
    }

    /**
     * Gets a Singleton instance of the LedgerFactory.
     * 
     * @return the Singleton Instance
     */
    public synchronized static LedgerFactory getInstance() throws LowlevelLedgerException {
        if (instance == null) {
            instance = new LedgerFactory();
        }
        return instance;
    }


/*    static synchronized public void registerImplementation(String name, Class impl) {
        if (implementationMap==null) {
            implementationMap=new HashMap();
            defaultImplementation=name;
        }
        implementationMap.put(name,impl);
    }
    private static Map implementationMap;
    private static String defaultImplementation;*/
    private static LedgerFactory instance;
    private static Map ledgerMap;
    private DefaultPicoContainer pico;
    private static Class DEFAULT_IMPLEMENTATION = SQLLedger.class;
}
