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
import java.util.*;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "StarsServlet", urlPatterns = "/api/movie-list")
public class StarsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starname = request.getParameter("starname");
        String genre = request.getParameter("genre");
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        if(title == null || title.trim().isEmpty())
		{
			out.write(jsonArray.toString());
			return;
		}
        
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            
            dbcon.setAutoCommit(false);
            PreparedStatement pstatement = null;
            if(title == null || title.trim().isEmpty())
    		{
    			response.getWriter().write(jsonArray.toString());
    			return;
    		}
            String[] keywords = title.trim().split(" ");
            String generateResult = "SELECT * FROM movies, ratings WHERE movies.id = movieid and MATCH(title) AGAINST(";
            //String query = "SELECT * from movies, ratings WHERE movies.id = movieid " + condition;
            String condition  = "'";
    		for(String keyword: keywords)
    		{
    			condition += keyword + "*" + " ";
    		}
    		condition = condition.trim() + "'";
    		generateResult += condition + " IN BOOLEAN MODE)";
    		pstatement = dbcon.prepareStatement(generateResult);
            ResultSet rs = pstatement.executeQuery();


            //Map<String, String> nameMap = new HashMap<String, String>();
            //ArrayList<String> genres = new ArrayList<String>();
            //ArrayList<String> stars = new ArrayList<String>();
            Map<String, JsonObject> objectMap = new HashMap<String, JsonObject>();
            Map<String, JsonArray> nameMap = new HashMap<String, JsonArray>();
            Map<String, JsonArray> genreMap = new HashMap<String, JsonArray>();
            // Iterate through each row of rsz
            while (rs.next()) {
            	
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
            		genreMap.get(movie_id).add(rs3.getString("name"));
            }
            
            for(String key: objectMap.keySet())
            {	
            	JsonArray genres = genreMap.get(key);
            	objectMap.get(key).add("movie_genres", genres);
            }
            
            
            //System.out.println(objectMap.toString());
            System.out.println("Genre:" + genre);
         
            for(JsonObject e: objectMap.values())
            {
            	//System.out.println(e.get("movie_stars").toString().contains(starname));
            	//System.out.println(e.get("movie_genres").toString());
            	if(e.get("movie_stars").toString().toLowerCase().contains(starname) && e.get("movie_genres").toString().contains(genre))
            		jsonArray.add(e);      
            }
            // write JSON string to output
            //System.out.println(jsonArray.toString());
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
            
            rs.close();
            rs2.close();
            rs3.close();
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
