import com.google.gson.JsonArray;

import com.google.gson.JsonObject;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "ConfirmServlet", urlPatterns = "/api/confirm")
public class ConfirmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		JsonArray jsonArray = new JsonArray();
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
		LocalDateTime now = LocalDateTime.now();  
		String date = dtf.format(now); 
		int id = 0;
		
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);
			String query1 = "select max(id) as c from sales";
			PreparedStatement pstatement = null;
			pstatement = dbcon.prepareStatement(query1);
			ResultSet rs = pstatement.executeQuery();
			while(rs.next())
			{
				int count = Integer.valueOf(rs.getString("c"));
				id = count;
			}
			id++;
			ArrayList<String> keylist = new ArrayList<String> ();
			ArrayList<String> idlist = new ArrayList<String> ();
			ArrayList<Integer> quantitylist = new ArrayList<Integer> ();
			Map<String, Integer> m = (Map<String, Integer>) session.getAttribute("items");
			for( String key: m.keySet())
			{
				keylist.add(key);
				quantitylist.add(m.get(key));
			}

			for (int i = 0; i < keylist.size();i++)
			{
				PreparedStatement pstatement2 = null;
				//String query2 = "select id from movies where title ='" + keylist.get(i)+ "'";
				String query2 = "select id from movies where title = ?";
				pstatement2 = dbcon.prepareStatement(query2);
				pstatement2.setString(1, keylist.get(i));
				ResultSet rs2 = pstatement2.executeQuery();
				rs2.next();
				String movieid = rs2.getString("id");
				idlist.add(movieid);
			}

			PreparedStatement pstatement3 = null;
			//String query3 = "select id from customers where email ='" + username + "'";
			String query3 = "select id from customers where email = ?";
			pstatement3 = dbcon.prepareStatement(query3);
			pstatement3.setString(1, username);
			ResultSet rs3 = pstatement3.executeQuery();
			rs3.next();
			String userid = rs3.getString("id");
			
			
			for (int i = 0; i < idlist.size();i++)
			{
				PreparedStatement pstatement4 = null;
				//String query4 = "INSERT INTO sales VALUES ('" + id + "'" + "," + "'" + userid + "'" + "," + "'" + idlist.get(i) + "'" + "," + "'" + date + "'" + ")";
				String query4 = "INSERT INTO sales VALUES (?,?,?,?)";
				pstatement4 = dbcon.prepareStatement(query4);
				pstatement4.setInt(1, id);
				pstatement4.setString(2, userid);
				pstatement4.setString(3, idlist.get(i));
				pstatement4.setString(4, date);
				pstatement4.executeUpdate();
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("orderid", id);
				jsonObject.addProperty("title", keylist.get(i));
				jsonObject.addProperty("quantity", String.valueOf(quantitylist.get(i)));
				id++;
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
            // set response status to 200 (OK)	
			response.setStatus(200);
			dbcon.close();
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
