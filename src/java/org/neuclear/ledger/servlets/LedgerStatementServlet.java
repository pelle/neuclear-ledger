package org.neuclear.ledger.servlets;

import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

$Id: LedgerStatementServlet.java,v 1.3 2004/04/27 15:23:40 pelle Exp $
$Log: LedgerStatementServlet.java,v $
Revision 1.3  2004/04/27 15:23:40  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

Revision 1.2  2004/04/21 23:24:18  pelle
Integrated Browser with the asset controller
Updated look and feel
Added ServletLedgerFactory
Added ServletAssetControllerFactory
Created issue.jsp file
Fixed many smaller issues

Revision 1.1  2004/03/29 23:43:30  pelle
The servlets now work and display the ledger contents.

Revision 1.7  2004/03/29 20:05:16  pelle
LedgerServlet works now at least for a straight non date restricted browse.

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
public class LedgerStatementServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        serviceid = ServletTools.getInitParam("serviceid", config);
        try {
            ledger = ServletLedgerFactory.getInstance().createLedger(config);

        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Principal user = request.getUserPrincipal();
        ServletTools.printHeader(out, request, "Account Statement for " + Utility.denullString(user.getName()));
        String url = ServletTools.getAbsoluteURL(request, request.getServletPath());
        try {
            String book = user.getName();

            out.println("<h1>Statement</h1>");
            out.print("<h3>");
            out.print(book);
            out.println("</h3><hr>");
            out.println("Balance: ");
            out.println(ledger.getBalance(book));
            out.println("<br/>Available: ");
            out.println(ledger.getAvailableBalance(book));
            out.print("<hr><a href=\"../browse/");
            out.print(book);
            out.print("\">View Transactions</a>");


        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        }
    }


    private String serviceid;
    private LedgerController ledger;
}
