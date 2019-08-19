import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
   
    
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cnumber = request.getParameter("Cnumber");
        String fname = request.getParameter("Fname");
        String lname = request.getParameter("Lname");
        String edate = request.getParameter("Edate");

        /**
         * This example only allows username/password to be anteater/123456
         * In real world projects, you should talk to the database to verify username/password
         */
        try {
        	
        	Connection dbcon = dataSource.getConnection();
        	dbcon.setAutoCommit(false);
        	PreparedStatement pstatement = null;
        	Statement statement = dbcon.createStatement();
        	
        	//String query = "SELECT * from creditcards where id = '" + cnumber + "'" + "and firstName = '" + fname + "'"+ "and lastName = '" + lname + "'"+ "and expiration = '" + edate + "'";
        	String query = "SELECT * from creditcards where id = ? and firstName = ? and lastName = ? and expiration = ?";
        	pstatement = dbcon.prepareStatement(query);
        	pstatement.setString(1, cnumber);
        	pstatement.setString(2, fname);
        	pstatement.setString(3, lname);
        	pstatement.setString(4, edate);
        	ResultSet rs = pstatement.executeQuery();
        	
        	boolean exist = false;
        	//boolean verified = false;
        	
        	if (rs.next())
        	{	
        		exist = true;
        		//verified  = rs.getString("password").equals(password);
        	}
        	
        if (exist) {
            // Login succeeds
            // Set this user into current session
            String sessionId = ((HttpServletRequest) request).getSession().getId();
            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
//            request.getSession().setAttribute("user", new User(username));

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("checkout_status", "success");
            responseJsonObject.addProperty("checkout_message", "success");

            response.getWriter().write(responseJsonObject.toString());
            dbcon.close();
        } else {
            // Login fails
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("checkout_status", "fail");

            responseJsonObject.addProperty("checkout_message", "incorrect payment information");
            response.getWriter().write(responseJsonObject.toString());
            dbcon.close();
        }
    } catch (Exception e)
        {
    		response.setStatus(500);
        }
}
    
}
