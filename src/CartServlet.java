import com.google.gson.JsonArray;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	String title = request.getParameter("title");
    	System.out.println(title);
    	String num = request.getParameter("num");
    	try {
    	int quantity = Integer.valueOf(num);
    	if(quantity < 0)
    	{}
    	else if(quantity == 0)
    	{
    		Map<String, Integer> m = (Map<String, Integer>) session.getAttribute("items");
    		m.remove(title);
    	}
    	else
    	{
    		Map<String, Integer> m = (Map<String, Integer>) session.getAttribute("items");
    		m.put(title, quantity);
    	}
    	}
    	catch(Exception e)
    	{
    	}
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		try {
		Map<String, Integer> m = (HashMap <String, Integer>) session.getAttribute("items");
        JsonArray jsarr = new JsonArray ();
		for(String key: m.keySet())
		{
			JsonObject jso = new JsonObject();
			jso.addProperty("title", key);
			jso.addProperty("quantity", m.get(key));
			jsarr.add(jso);
		}
		
		
		out.write(jsarr.toString());
            // set response status to 200 (OK)
        response.setStatus(200);
            
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
