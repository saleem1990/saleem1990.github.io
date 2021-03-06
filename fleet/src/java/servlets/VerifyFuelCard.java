/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import com.mtrh.fleet.dao.ConnectionProperties;
import com.mtrh.fleet.dao.DBObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author owner
 */
@WebServlet(name = "VerifyFuelCard", urlPatterns = {"/VerifyFuelCard"})
public class VerifyFuelCard extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet VerifyUser</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyUser at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private String verifyID(String cardno, Connection connectDB) {
        String returnvalue = "0";
        String sql = "SELECT count(*) FROM fleet.vehicle_register WHERE upper(cardno) = ?";

        try {
            PreparedStatement pst = connectDB.prepareStatement(sql);
            pst.setObject(1, cardno.trim().toUpperCase());
            ResultSet rset = pst.executeQuery();

            while (rset.next()) {

                returnvalue = String.valueOf(rset.getInt(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return returnvalue;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");

        String cardno = request.getParameter("cardno");

        LoginController ls = new LoginController();
        ls.connect("postgres", "sequence");
        Connection conn = ls.connect("postgres", "sequence");
        String isExists = verifyID(cardno, conn);

        out.write(isExists);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private String getVehiclesByCardNo(java.sql.Connection connectDB, String cardno) {
        ArrayList<HashMap<String, String>> parentList
                = new ArrayList<HashMap<String, String>>();

        String[] columns = new String[]{"regno"};

        String sql = "select distinct regno from fleet.vehicle_register where cardno= ?";

        try {
            PreparedStatement pst = connectDB.prepareStatement(sql);

            pst.setObject(1, cardno);
            ResultSet rset = pst.executeQuery();

            int j = 0;
            while (rset.next()) {

                HashMap<String, String> child = new HashMap<String, String>();
                for (int i = 0; i < columns.length; i++) {
                    child.put("make1", rset.getObject(1).toString().toUpperCase());
                }
                parentList.add(child);
                j++;
            }

        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
        }

        String json = new Gson().toJson(parentList);//String JSON object

        System.err.println("I am here " + json);

        return json;

    }

    private String getCardamt(String cardno, Connection connectDB) {

        com.mtrh.fleet.dao.CardTransactions card = new com.mtrh.fleet.dao.CardTransactions();

        return card.getCardAmount(cardno, connectDB);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        String isExists = "0";

        if (session != null) {
            //java.sql.Connection conn = ConnectionProperties.getConnect2DB();
            java.sql.Connection conn = (java.sql.Connection) session.getAttribute("connection");

            String cardno = request.getParameter("cardno");
            String transtype = request.getParameter("transtype");

            LoginController ls = new LoginController();
            ls.connect("postgres", "sequence");

            if (transtype.equalsIgnoreCase("CARD VEHICLES")) {
                String json = getVehiclesByCardNo(conn, cardno);

                out.write(json);
            } else {
                String cardamt = getCardamt(cardno, conn);

                out.write(cardamt);
                System.err.println(cardamt);
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
