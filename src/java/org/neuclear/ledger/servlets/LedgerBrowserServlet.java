package org.neuclear.ledger.servlets;

import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletMessages;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.ledger.Book;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;

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

$Id: LedgerBrowserServlet.java,v 1.13 2004/07/23 18:55:26 pelle Exp $
$Log: LedgerBrowserServlet.java,v $
Revision 1.13  2004/07/23 18:55:26  pelle
Added an improved complete method which allows you to specify a book to change to another book when completing a transaction.
This is used by the NeuClear Pay Complete Exchange Order process which changes the benificiary book from the agent to the final recipient.

Revision 1.12  2004/06/19 21:18:05  pelle
Fixes to spanish version

Revision 1.11  2004/06/15 21:25:25  pelle
Added PortfolioBrowserServlet
LedgerBrowserServlet now can take an optional ledger id in its path info.

Revision 1.10  2004/06/08 17:45:47  pelle
Minor fixes and cleanups

Revision 1.9  2004/06/06 21:26:29  pelle
Localized LedgerBrowserServlet to Spanish

Revision 1.8  2004/05/22 20:41:19  pelle
Fixed LedgerBrowserServlet to handle the new ledgerid.
Split amount column to payments and received

Revision 1.7  2004/04/25 07:27:10  pelle
Cosmetic changes to signing servlet and neuclear pay web app.
Cosmetic changes to html generated by ServiceBuilder.
Fixed some stuff in Sender and SmtpSender
Added account registration.
Bumped version numbers in project.xml files to final release versions.

Revision 1.6  2004/04/23 19:09:15  pelle
Lots of cleanups and improvements to the userinterface and look of the bux application.

Revision 1.5  2004/04/22 23:59:21  pelle
Added various statistics to Ledger as well as AssetController
Improved look and feel in the web app.

Revision 1.4  2004/04/21 23:24:17  pelle
Integrated Browser with the asset controller
Updated look and feel
Added ServletLedgerFactory
Added ServletAssetControllerFactory
Created issue.jsp file
Fixed many smaller issues

Revision 1.3  2004/04/19 18:57:26  pelle
Updated Ledger to support more advanced book information.
You can now create a book or fetch a book by doing getBook(String id) on the ledger.
You can register a book or upddate an existing one using registerBook()
SimpleLedger now works and passes all tests.
HibernateLedger has been implemented, but there are a few things that dont work yet.

Revision 1.2  2004/03/31 23:11:09  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

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
public class LedgerBrowserServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        serviceid = ServletTools.getInitParam("serviceid", config);
        defaultledger = ServletTools.getInitParam("ledgerid", config);
        title = ServletTools.getInitParam("title", config);
        try {
//            ConfigurableContainer pico=(ConfigurableContainer) getServletContext().getAttribute("pico");
//            ledger = (LedgerBrowser) pico.getComponentInstance(Ledger.class) ;
            ledger = (LedgerBrowser) ServletLedgerFactory.getInstance().createLedger(config);
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
        try {
            String bookid = user.getName();
            Book book = ((LedgerController) ledger).getBook(bookid);

            ServletTools.printHeader(out, request, title, messages.getString("accountmovements") + " " + Utility.denullString(book.getNickname()));
            String url = ServletTools.getAbsoluteURL(request, request.getServletPath());
            System.out.println("Browsing: " + book.getId());

            String fromStr = request.getParameter("from");
            String toStr = request.getParameter("to");

            Date from = parseDate(fromStr);
            Date to = parseDate(toStr);
            String ledgerid = request.getPathInfo();
            if (ledgerid != null && ledgerid.length() > 32)
                ledgerid = ledgerid.substring(1, 32);
            else
                ledgerid = defaultledger;
            System.out.println("Using ledger: " + ledgerid);
            BookBrowser stmt = browse(ledgerid, bookid, from, to);
            out.println("<table><tr><th>" + messages.getString("xid") + "</th><th>" + messages.getString("time") + "</th><th>" + messages.getString("who") + "</th><th>" + messages.getString("comments") + "</th><th>" + messages.getString("payments") + "</th><th>" + messages.getString("received") + "</th></tr>");
            int linecount = 0;
            while (stmt.next()) {
                final double amount = stmt.getAmount();
                out.print("<tr class=\"");
                if ((linecount++) % 2 == 1)
                    out.print("odd");
                else
                    out.print("even");
                if (amount < 0)
                    out.print("negative");
                out.print("\" onMouseOver=\"if(this.className=='even') this.class='evenhover' else this.class='oddhover'\" " +
                        "onMouseOut=\"if(this.class='evenhover') this.class='even' else this.class='odd'\">" +
                        "<td style=\"size:small\" title=\"");
                out.print(stmt.getRequestId());
                out.print("\">");
                out.print((stmt.getRequestId().length() > 10) ? stmt.getRequestId().substring(0, 10) : stmt.getRequestId());
                out.print("</td><td>");
                out.print(dateformat.format(stmt.getValuetime()));
                out.print("</td><td>");
                Book counterparty = stmt.getCounterparty();
//                out.println("\">");
                if (counterparty.getSource() != null) {
                    out.print("<a href=\"");
                    out.print(counterparty.getSource());
                    out.print("\">");
                }
                out.print(Utility.denullString(counterparty.getNickname(), counterparty.getId()));
                if (counterparty.getSource() != null)
                    out.print("</a>");
                out.print("</td><td>");
                out.print(stmt.getComment());
                out.print("</td><td class=\"negative\">");
                if (amount < 0)
                    out.print(numbers.format(-amount));
                out.print("</td><td class=\"positive\">");
                if (amount >= 0)
                    out.print(numbers.format(amount));
                out.print("</td></tr>");

            }
            out.println("</table><a href=\"");
            out.println(ServletTools.getAbsoluteURL(request, "/"));
            out.println("\">" + messages.getString("mainmenu") + "</a>");
            out.println("</body></html>");
        } catch (LowlevelLedgerException e) {
            e.printStackTrace(out);
            e.printStackTrace();
        } catch (UnknownBookException e) {
            e.printStackTrace(out);
            e.printStackTrace();
        }
    }


    private BookBrowser browse(String ledgerid, String book, Date from, Date to) throws LowlevelLedgerException {
        if (from != null) {
            if (to != null)
                return ledger.browseRange(ledgerid, book, from, to);
            return ledger.browseFrom(ledgerid, book, from);
        }
        return ledger.browse(ledgerid, book);
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

    private String serviceid;
    private LedgerBrowser ledger;
    private String defaultledger;
    private String title;
}
