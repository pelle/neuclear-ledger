package org.neuclear.ledger.servlets;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletMessages;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.Identity;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.browser.LedgerBrowser;
import org.neuclear.ledger.browser.PortfolioBrowser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * User: pelleb
 * Date: Dec 26, 2003
 * Time: 5:54:05 PM
 */
public class PortfolioBrowserServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        title = ServletTools.getInitParam("title", config);
        try {
//            ConfigurableContainer pico=(ConfigurableContainer) getServletContext().getAttribute("pico");
//            ledger = (LedgerBrowser) pico.getComponentInstance(Ledger.class) ;
            ledger = (LedgerController) ServletLedgerFactory.getInstance().createLedger(config);
            if (ledger instanceof LedgerBrowser) {
                browser = (LedgerBrowser) ledger;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
//        response.setCharacterEncoding("UTF-8");
        System.setProperty("file.encoding", "UTF-8");
        ResourceBundle messages = ServletMessages.getMessages("ledgermessages", request);
        NumberFormat numbers = NumberFormat.getNumberInstance(request.getLocale());
        numbers.setMaximumFractionDigits(2);
        numbers.setMinimumFractionDigits(2);
        DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, request.getLocale());
        PrintWriter out = response.getWriter();
        Principal user = request.getUserPrincipal();
        ServletTools.printHeader(out, request, title, messages.getString("accountportfolio") + " " + Utility.denullString(user.getName()));
        String url = ServletTools.getAbsoluteURL(request, request.getServletPath());
        try {
            String bookid = user.getName();
            System.out.println("Browsing: " + bookid);

            PortfolioBrowser stmt = browse(bookid);
            out.println("<table><tr><th>" + messages.getString("asset") + "</th><th>" + messages.getString("count") + "</th><th>" + messages.getString("balance") + "</th></tr>");
            int linecount = 0;
            while (stmt.next()) {
                final double balance = stmt.getBalance();
                Identity id = (Identity) Resolver.resolveFromCache(stmt.getLedger());
                out.print("<tr class=\"");
                if ((linecount++) % 2 == 1)
                    out.print("odd");
                else
                    out.print("even");
                if (balance < 0)
                    out.print("negative");
                out.print("\" onMouseOver=\"if(this.className=='even') this.class='evenhover' else this.class='oddhover'\" " +
                        "onMouseOut=\"if(this.class='evenhover') this.class='even' else this.class='odd'\">" +
                        "<td style=\"size:small\" title=\"");
                out.print(stmt.getLedger());
                out.print("\"><a href=\"browse/");
                out.print(stmt.getLedger());
                out.print("\">");
                if (id != null)
                    out.print(id.getNickname());
                else
                    out.print(stmt.getLedger());
                out.print("</a></td><td>");
                out.print(stmt.getCount());
                out.print("</td><td>");
                out.print(numbers.format(balance));
                out.print("</td></tr>");

            }
            out.println("</table><a href=\"");
            out.println(ServletTools.getAbsoluteURL(request, "/"));
            out.println("\">" + messages.getString("mainmenu") + "</a>");
            out.println("</body></html>");
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        } catch (UnknownBookException e) {
            e.printStackTrace();
        }
    }


    private PortfolioBrowser browse(String book) throws LowlevelLedgerException, UnknownBookException {
        return browser.browsePortfolio(ledger.getBook(book));
    }

    private Date parseDate(String fromStr) {
        if (Utility.isEmpty(fromStr))
            return null;
        try {
            return TimeTools.parseTimeStamp(fromStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private LedgerController ledger;
    private LedgerBrowser browser;
    private String title;
}
