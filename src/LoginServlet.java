import com.google.gson.JsonObject;
import java.io.*;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.jasypt.util.password.StrongPasswordEncryptor;
/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
   
    
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();
        
        /**
         * This example only allows username/password to be anteater/123456
         * In real world projects, you should talk to the database to verify username/password
         */
        
        String userAgent = request.getHeader("User-Agent");
        System.out.println("Got connection from " + userAgent);
        
        if(userAgent != null && !userAgent.contains("Android")) {
	        try {
	            RecaptchaVerifyUtils.verify(request.getParameter("g-recaptcha-response"));
	        } catch (Exception e) {
	        	JsonObject jsob = new JsonObject();
	        	jsob.addProperty("status", "fail");
	        	jsob.addProperty("message", "Please pass reCHAPTHA before loggin in");
	        	out.write(jsob.toString());
	            out.close();
	            return;
	        }
        }
        
        try {
        	
        	Connection dbcon = dataSource.getConnection();
        	dbcon.setAutoCommit(false);
        	Statement statement = dbcon.createStatement();
        	PreparedStatement pstatement1 = null;
        	//String query = "SELECT * from customers where email = '" + username + "'";
        	String query = "SELECT * from employees where email = ?";
        	pstatement1 = dbcon.prepareStatement(query);
        	pstatement1.setString(1,username);
        	ResultSet rs = pstatement1.executeQuery();
        	
        	boolean exist = false;
        	boolean verified = false;
        	
        	
        	if (rs.next())
        	{	
        		exist = true;
        		String encryptedPassword = rs.getString("password");
        		verified  = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        	}
        	
        if (verified) {
            // Login succeeds
            // Set this user into current session
            String sessionId = ((HttpServletRequest) request).getSession().getId();
            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
            request.getSession().setAttribute("user", new User(username));
            
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            responseJsonObject.addProperty("type", "employee");

            response.getWriter().write(responseJsonObject.toString());
            dbcon.close();
        } else {
            
        	PreparedStatement pstatement2 = null;
        	//String query2 = "SELECT * from customers where email = '" + username + "'";
        	String query2 = "SELECT * from customers where email = ?";
        	pstatement2 = dbcon.prepareStatement(query2);
        	pstatement2.setString(1, username);
        	ResultSet rs2 = pstatement2.executeQuery();
        	
        	boolean c_exist = false;
        	boolean c_verified = false;
        	
        	if (rs2.next())
        	{	
        		c_exist = true;
        		String encryptedPassword = rs2.getString("password");
        		c_verified  = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        		//System.out.println(password);
        		//System.out.println(c_verified);
        	}

        	if ((username.equals("anteater") && password.equals("123456")) || c_verified) {
                // Login succeeds
                // Set this user into current session
        		//System.out.println("login in success");
        		System.out.println("It's an anteater!");
                String sessionId = ((HttpServletRequest) request).getSession().getId();
                Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
                request.getSession().setAttribute("user", new User(username));
                
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                responseJsonObject.addProperty("type", "customer");
               
                response.getWriter().write(responseJsonObject.toString());
                dbcon.close();
            }
        	else {
            
        		JsonObject responseJsonObject = new JsonObject();
            
        		//responseJsonObject.addProperty("status", "fail");
            
        		if (!username.equals("anteater") && !exist && !c_exist) {
        			responseJsonObject.addProperty("status", "fail1");
        			responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            
        		} else {
        			responseJsonObject.addProperty("status", "fail2");
        			responseJsonObject.addProperty("message", "incorrect password");
            
        		}
            
        		response.getWriter().write(responseJsonObject.toString());
        		dbcon.close();
        	
        	}
        }
    } catch (Exception e)
        {
    		response.setStatus(500);
        }
}
    
}
