package org.neuclear.ledger.browser;

import org.neuclear.commons.configuration.Configuration;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.simple.PopulatedSimpleLedger;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;

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

$Id: LedgerConfiguration.java,v 1.5 2004/04/27 15:23:39 pelle Exp $
$Log: LedgerConfiguration.java,v $
Revision 1.5  2004/04/27 15:23:39  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

Revision 1.4  2004/03/29 23:43:30  pelle
The servlets now work and display the ledger contents.

Revision 1.3  2004/03/23 22:01:42  pelle
Bumped version numbers for commons and xmlsig througout.
Updated repositories and webservers to use old.neuclear.org
Various other fixes in project.xml and project.properties on misc projects.

Revision 1.2  2004/03/21 00:48:35  pelle
The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.

Revision 1.1  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

*/

/**
 * User: pelleb
 * Date: Jan 2, 2004
 * Time: 10:19:48 PM
 */
public class LedgerConfiguration implements Configuration {
    public void configure(org.picocontainer.MutablePicoContainer pico) {
//        pico.registerComponentImplementation(ConnectionSource.class, TestCaseXAConnectionSource.class);
//        pico.registerComponentImplementation(StatementFactory.class, SimpleStatementFactory.class);
//        pico.registerComponentImplementation(Ledger.class,SQLLedger.class,new Parameter[] {new ConstantParameter("neu://test/bux")});
        pico.registerComponentImplementation(LedgerController.class, PopulatedSimpleLedger.class, new Parameter[]{new ConstantParameter("test")});

    }
}
