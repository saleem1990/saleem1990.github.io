/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;

/**
 *
 * @author owner
 */
@WebServlet(name = "SearchRequest", urlPatterns = {"/SearchRequest"})
public class SearchRequest extends HttpServlet {

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
            out.println("<title>Servlet SearchRequest</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchRequest at " + request.getContextPath() + "</h1>");
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //  processRequest(request, response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String searchkey = request.getParameter("searchkey");
        LoginController ls = new LoginController();
        Connection connectDB = ls.connect("postgres", "sequence");
        String json = getRequestsByKey(connectDB, searchkey);
        
        out.write(json);

    }

    private String getRequestsByKey(java.sql.Connection connectDB, String key) {
        ArrayList<HashMap<String, String>> parentList
                = new ArrayList<HashMap<String, String>>();//Parent List

        // Vector v = new Vector();
        String[] columns = new String[]{"rqid", "rqon", "rqby", "enteredon"};

        String sql = "select rqid,rqtype,requestedby,enteredon from fleet.transportrequestmemo WHERE rqid ilike '%" + key + "%'";
        try {
            PreparedStatement pst = connectDB.prepareStatement(sql);
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {

                HashMap<String, String> child = new HashMap<String, String>();
                for (int i = 0; i < columns.length; i++) {
                    child.put(columns[i].toString(), rset.getObject(i + 1).toString());
                }

                parentList.add(child);
            }

        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
        }
        String json = new Gson().toJson(parentList);//String JSON object

        System.err.println("I am here " + json);

        JSONArray arr = new JSONArray();
        arr.put(json);                         //JSON Array

        return json;

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
