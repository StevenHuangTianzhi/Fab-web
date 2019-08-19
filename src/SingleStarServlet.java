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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
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
		
		
		PrintWriter out = response.getWriter();

		JsonObject jasonObject = new JsonObject();
		HttpSession session = request.getSession();
		String url = (String) session.getAttribute("url");
		jasonObject.addProperty("url", url);
		;
		out.write(jasonObject.toString());
        // set response status to 200 (OK)
        response.setStatus(200);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			dbcon.setAutoCommit(false);
			PreparedStatement pstatement = null;
			// Construct a query with parameter represented by "?"
			//String query = "SELECT * from stars_in_movies, stars, movies WHERE stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId";
			String query = "SELECT * from stars_in_movies, stars, movies WHERE stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId and name = ?";
			pstatement = dbcon.prepareStatement(query);
			pstatement.setString(1, id);

			ResultSet rs = pstatement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            //Map<String, String> nameMap = new HashMap<String, String>();
            //ArrayList<String> genres = new ArrayList<String>();
            //ArrayList<String> stars = new ArrayList<String>();
            //Map<String, JsonObject> objectMap = new HashMap<String, JsonObject>();
            //Map<String, JsonArray> nameMap = new HashMap<String, JsonArray>();
            //Map<String, String> genreMap = new HashMap<String, String>();
            // Iterate through each row of rsz

            JsonObject jsonObject = new JsonObject();
/*
            String star_name = rs.getString("name");
            String star_id = rs.getString("starId");
            String birth_year = rs.getString("birthYear");
            jsonObject.addProperty("star_name", star_name);
            jsonObject.addProperty("birth_year", birth_year);
            jsonObject.addProperty("star_id", star_id);
            JsonArray idArray = new JsonArray();
            JsonArray titleArray = new JsonArray();
            jsonObject.add("movie_ids", new JsonArray());
            jsonObject.add("movie_titles", new JsonArray());
            */
            int count = 0;
            JsonArray idArray = new JsonArray();
            JsonArray titleArray = new JsonArray();
            jsonObject.add("movie_ids", idArray);
            jsonObject.add("movie_titles", titleArray);
            while (rs.next()) {
            	if(count == 0)
            	{
                    String star_name = rs.getString("name");
                    String star_id = rs.getString("starId");
                    String birth_year = rs.getString("birthYear");
                    jsonObject.addProperty("star_name", star_name);
                    jsonObject.addProperty("birth_year", birth_year);
                    jsonObject.addProperty("star_id", star_id);
            	}

                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                idArray.add(movie_id);
                titleArray.add(movie_title);
                count++;
                // Create a JsonObject based on the data w
                
            jsonArray.add(jsonObject);    
       
            }

 /*           
            String query2 = "SELECT * from stars_in_movies, stars WHERE stars.id = stars_in_movies.starId";
            ResultSet rs2 = statement.executeQuery(query2);
            
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
      
            String query3 = "SELECT * from genres, genres_in_movies where genres.id = genres_in_movies.genreId";
            ResultSet rs3 = statement.executeQuery(query3);
            
            
            while (rs3.next()) {
            	String movie_id = rs3.getString("movieId");
            	if(objectMap.keySet().contains(movie_id))
            		genreMap.put(movie_id, genreMap.getOrDefault(movie_id, "").concat(rs3.getString("name") + ", "));
            }
            
            for(String key: objectMap.keySet())
            {	
            	String genres = genreMap.get(key);
            	System.out.println(genres);
            	objectMap.get(key).addProperty("movie_genres", genres);
            }
                  
            
            for(JsonObject e: objectMap.values())
            {
            	jsonArray.add(e);
            }
			*/
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
