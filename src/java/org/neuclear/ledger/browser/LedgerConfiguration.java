package org.neuclear.ledger.browser;

import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.sql.statements.StatementFactory;
import org.neuclear.commons.sql.statements.SimpleStatementFactory;
import org.neuclear.commons.sql.ConnectionSource;
import org.neuclear.commons.sql.DefaultXAConnectionSource;
import org.neuclear.commons.sql.TestCaseXAConnectionSource;
import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.ledger.Ledger;
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

$Id: LedgerConfiguration.java,v 1.1 2004/01/02 23:18:34 pelle Exp $
$Log: LedgerConfiguration.java,v $
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
        pico.registerComponentImplementation(ConnectionSource.class,TestCaseXAConnectionSource.class);
        pico.registerComponentImplementation(StatementFactory.class,SimpleStatementFactory.class);
        pico.registerComponentImplementation(Ledger.class,SQLLedger.class,new Parameter[] {new ConstantParameter("neu://test/bux")});

    }
}
