package org.neuclear.ledger.servlets;

import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.sql.JNDIConnectionSource;
import org.neuclear.commons.sql.statements.SimpleStatementFactory;
import org.neuclear.commons.sql.statements.StatementFactory;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NSTools;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.Principal;

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

$Id: LedgerServlet.java,v 1.6 2004/03/26 23:36:34 pelle Exp $
$Log: LedgerServlet.java,v $
Revision 1.6  2004/03/26 23:36:34  pelle
The simple browse(book) now works on hibernate, I have implemented the other two, which currently don not constrain the query correctly.

Revision 1.5  2004/03/25 22:04:46  pelle
The first shell for the HibernateBookBrowser

Revision 1.4  2004/03/21 00:48:36  pelle
The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.

Revision 1.3  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

Revision 1.2  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added BookBrowser pattern to ledger, simplifying the statement writing process.

Revision 1.1  2003/12/29 22:40:15  pelle
Added LedgerServlet and friends

*/

/**
 * User: pelleb
 * Date: Dec 26, 2003
 * Time: 5:54:05 PM
 */
public class LedgerServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        datasource = ServletTools.getInitParam("datasource", config);
        serviceid = ServletTools.getInitParam("serviceid", config);
        try {
            fact = new SimpleStatementFactory(new JNDIConnectionSource(datasource));
//            ledger= new SQLLedger(
//                    fact,
//                        serviceid
//                );
//            if (!ledger.bookExists("neu://alice@test"))
//                PopulateLedger.main(null);
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        ServletTools.printHeader(out, request, "Account Browser");
        String url = ServletTools.getAbsoluteURL(request, request.getServletPath());
        try {
            Principal user = request.getUserPrincipal();
            String book = request.getPathInfo();
            if (Utility.isEmpty(book))
                book = serviceid;
            else
                book = "neu:/" + book;
            BookBrowser stmt = ledger.browse(book);
            out.println("<table><tr><th>Transaction ID</th><th>Time</th><th>Counterparty</th><th>Comment</th><th>Amount</th></tr>");
            while (stmt.next()) {
                final double amount = stmt.getAmount();
                out.print("<tr");
                if (amount < 0)
                    out.print(" class=\"negative\"");
                out.print("><td style=\"size:small\">");
                out.print(stmt.getId());
                out.print("</td><td>");
                out.print(TimeTools.formatTimeStampShort(stmt.getValuetime()));
                out.print("</td><td><a href=\"");
                out.print(url);
                if (NSTools.isValidName(stmt.getCounterparty()))
                    out.print(NSTools.name2path(stmt.getCounterparty()));
                else
                    out.print("/" + stmt.getCounterparty());
                out.println("\">");
                out.print(stmt.getCounterparty());
                out.print("</a></td><td>");
                out.print(stmt.getComment());
                out.print("</td><td>");
                out.print(amount);
                out.print("</td></tr>");

            }
            out.println("</table>");
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        }
    }

    private DataSource ds;
    private String datasource;
    private String serviceid;
    private static final BigDecimal ZERO = new BigDecimal(0);
    private LedgerBrowser ledger;
    private StatementFactory fact;
}
