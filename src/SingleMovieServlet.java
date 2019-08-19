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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String title = request.getParameter("title");
		
		HttpSession session = request.getSession();
		if(session.getAttribute("items") == null)
		{
			Map<String, Integer> x = new HashMap<>();
			session.setAttribute("items", x);
		}
		Map<String, Integer> m = (Map<String, Integer>) session.getAttribute("items");
		
		if(!m.containsKey(title))
			m.put(title, 0);
		m.put(title, m.get(title) + 1);
		
		System.out.println(session.getAttribute("items").toString());
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String Gotid = request.getParameter("id");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);
			PreparedStatement pstatement1 = null;
			// Construct a query with parameter represented by "?"
			String query = "SELECT * from movies, ratings WHERE movies.id = movieid ORDER BY rating DESC";
			pstatement1 = dbcon.prepareStatement(query);
			ResultSet rs = pstatement1.executeQuery();

            JsonArray jsonArray = new JsonArray();
            //Map<String, String> nameMap = new HashMap<String, String>();
            //ArrayList<String> genres = new ArrayList<String>();
            //ArrayList<String> stars = new ArrayList<String>();
            Map<String, JsonObject> objectMap = new HashMap<String, JsonObject>();
            Map<String, JsonArray> nameMap = new HashMap<String, JsonArray>();
            Map<String, JsonArray> genreMap = new HashMap<String, JsonArray>();
            // Iterate through each row of rsz
            while (rs.next()) {
            	
            	//System.out.println("In rs");
            	String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                
                
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating); 
                objectMap.put(movie_id, jsonObject);
                nameMap.put(movie_id, new JsonArray());
                genreMap.put(movie_id, new JsonArray());
       
            }

            PreparedStatement pstatement2 = null;
            String query2 = "SELECT * from stars_in_movies, stars WHERE stars.id = stars_in_movies.starId";
            pstatement2 = dbcon.prepareStatement(query2);
            ResultSet rs2 = pstatement2.executeQuery();
            
            while (rs2.next()) {
            	String movie_id = rs2.getString("movieId");
            	if(objectMap.keySet().contains(movie_id))
            		nameMap.get(movie_id).add(rs2.getString("name"));
            }
            
            for(String key: objectMap.keySet())
            {	
            	JsonArray starNames = nameMap.get(key);
            	objectMap.get(key).add("movie_stars", starNames);
            }
      
            PreparedStatement pstatement3 = null;
            String query3 = "SELECT * from genres, genres_in_movies where genres.id = genres_in_movies.genreId";
            pstatement3 = dbcon.prepareStatement(query3);
            ResultSet rs3 = pstatement3.executeQuery();
            
            
            while (rs3.next()) {
            	String movie_id = rs3.getString("movieId");
            	if(objectMap.keySet().contains(movie_id))
            	{
            		//genreMap.put(movie_id, genreMap.getOrDefault(movie_id, "").concat(rs3.getString("name") + ", "));
            		genreMap.get(movie_id).add(rs3.getString("name"));
            	}
            }
            
            for(String key: objectMap.keySet())
            {	
            	JsonArray genres = genreMap.get(key);
            	//System.out.println(genres);
            	objectMap.get(key).add("movie_genres", genres);
            }
                  
            /*
            for(String e: objectMap.keySet())
            {
            	if(e.equals(Gotid))
            	{
            		System.out.println("Found the one!!!!!!!!");
            		jsonArray.add(objectMap.get(e));
            	}
            }
            */
            
            jsonArray.add(objectMap.get(Gotid));
   
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
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
