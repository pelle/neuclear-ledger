package org.neuclear.ledger.servlets;

import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.commons.Utility;
import org.neuclear.commons.sql.JNDIConnectionSource;
import org.neuclear.id.NSTools;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.ledger.implementations.SQLLedger;

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

$Id: LedgerServlet.java,v 1.1 2003/12/29 22:40:15 pelle Exp $
$Log: LedgerServlet.java,v $
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
            ledger.createLedger(serviceid);

        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        ServletTools.printHeader(out,request,"Account Browser");
        String url=request.getServletPath();
        try {
            Connection con=consrc.getConnection();
            PreparedStatement stmt=con.prepareStatement("select t.id,t.valuetime, r.bookid,t.comment,s.amount from entry s,entry r, transaction t where s.transactionid=t.id and r.transactionid=t.id and r.id<>s.id\n" +
                    "and (s.bookid = ? ) and t.ledgerid=?");
            Principal user=request.getUserPrincipal();
            String book=request.getPathInfo();
            if (Utility.isEmpty(book))
                book=serviceid;
            else
                book="neu:/"+book;
            stmt.setString(1,book);
            stmt.setString(2,book);
            stmt.setString(3,serviceid);
            ResultSet rs=stmt.executeQuery();
            out.println("<table><tr><th>Transaction ID</th><th>Time</th><th>Counterparty</th><th>Comment</th><th>Amount</th></tr>");
            while(rs.next()){
                final BigDecimal amount = rs.getBigDecimal(5);
                out.print("<tr");
                if (amount.compareTo(ZERO)<0)
                    out.print(" class=\"negative\"");
                out.print("><td>");
                out.print(rs.getString(1));
                out.print("</td><td>");
                out.print(TimeTools.formatTimeStamp(rs.getTimestamp(2)));
                out.print("</td><td><a href=\"");
                out.print(url);
                out.print(NSTools.name2path(rs.getString(3)));
                out.println("\">");
                out.print(rs.getString(3));
                out.print("</a></td><td>");
                out.print(rs.getString(4));
                out.print("</td><td>");
                out.print(amount);
                out.print("</td></tr>");

            }
            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace(out);
        } catch (InvalidNamedObjectException e) {
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
