/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fleet;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
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
import javax.servlet.http.HttpSession;
import org.json.JSONArray;

/**
 *
 * @author owner
 */
@WebServlet(name = "VehiclesServlet", urlPatterns = {"/VehiclesServlet"})
public class VehiclesServlet extends HttpServlet {

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
            out.println("<title>Servlet VehiclesServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VehiclesServlet at " + request.getContextPath() + "</h1>");
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
        //processRequest(request, response);
        HttpSession session = request.getSession();
        if (session != null) {
            java.sql.Connection conn = (java.sql.Connection) session.getAttribute("connection");//ConnectionProperties.getConnect2DB();

            String fleetno = request.getParameter("fleetno");

            response.setContentType("application/json");

            String json = getVehicleDetailsByFleetID(conn, fleetno).replace("[", "").replace("]", "");

            System.err.println("I am ending this parameterization with " + json);

            response.getWriter().print(json);

        }
    }

    private String getVehicleDetailsByFleetID(java.sql.Connection connectDB, String fleetno) {
        ArrayList<HashMap<String, String>> parentList
                = new ArrayList<HashMap<String, String>>();//Parent List

        // Vector v = new Vector();
        String[] columns = new String[]{"regno", "regdate", "make", "model", "classification", "yom", "enginecc", "fuel", "chassisno", "engineno", "status", "driver", "section", "authority"};

        String sql = "select regno,regdate::text,make,model,classification,yom,engine_cc,fuel_used,chassis_no, engine_no, vehicle_status, driver,section,authority from fleet.vehicle_register where fleetno = ? OR regno = ?";
        try {
            PreparedStatement pst = connectDB.prepareStatement(sql);
            pst.setObject(1, fleetno);
            pst.setObject(2, fleetno);
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

        //   System.err.println("I am here " + json);
        JSONArray arr = new JSONArray();
        arr.put(json);                         //JSON Array

        return json;

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
        processRequest(request, response);
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
