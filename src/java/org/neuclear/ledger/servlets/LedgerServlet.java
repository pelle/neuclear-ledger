package org.neuclear.ledger.servlets;

import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.commons.Utility;
import org.neuclear.commons.sql.JNDIConnectionSource;
import org.neuclear.id.NSTools;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.ledger.browser.Statement;
import org.neuclear.ledger.browser.StatementEntry;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.LowlevelLedgerException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.Principal;
import java.math.BigDecimal;

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

$Id: LedgerServlet.java,v 1.2 2003/12/31 00:39:04 pelle Exp $
$Log: LedgerServlet.java,v $
Revision 1.2  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added Statement pattern to ledger, simplifying the statement writing process.

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
        datasource = ServletTools.getInitParam("datasource",config);
        serviceid = ServletTools.getInitParam("serviceid",config);
        try {
            consrc = new JNDIConnectionSource(datasource);
            ledger= new SQLLedger(
                    consrc,
                        serviceid
                );
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        ServletTools.printHeader(out,request,"Account Browser");
        String url=ServletTools.getAbsoluteURL(request,request.getServletPath());
        try {
            Principal user=request.getUserPrincipal();
            String book=request.getPathInfo();
            if (Utility.isEmpty(book))
                book=serviceid;
            else
                book="neu:/"+book;
            Statement stmt=ledger.browse(ledger.getBook(book));
            out.println("<table><tr><th>Transaction ID</th><th>Time</th><th>Counterparty</th><th>Comment</th><th>Amount</th></tr>");
            while(stmt.next()){
                final BigDecimal amount = stmt.getAmount();
                out.print("<tr");
                if (amount.compareTo(ZERO)<0)
                    out.print(" class=\"negative\"");
                out.print("><td style=\"size:small\">");
                out.print(stmt.getXid());
                out.print("</td><td>");
                out.print(TimeTools.formatTimeStampShort(stmt.getValuetime()));
                out.print("</td><td><a href=\"");
                out.print(url);
                if (NSTools.isValidName(stmt.getCounterparty()))
                    out.print(NSTools.name2path(stmt.getCounterparty()));
                else
                    out.print("/"+stmt.getCounterparty());
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
        } catch (UnknownBookException e) {
            e.printStackTrace();
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        }
    }
    private DataSource ds;
    private String datasource;
    private String serviceid;
    private static final BigDecimal ZERO=new BigDecimal(0);
    private SQLLedger ledger;
    private JNDIConnectionSource consrc;
}
