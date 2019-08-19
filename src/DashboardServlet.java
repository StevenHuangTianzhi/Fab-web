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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            ArrayList<String> table_name = new ArrayList<String>();
            JsonArray jsonArray = new JsonArray();
            DatabaseMetaData databaseMetaData = dbcon.getMetaData();
            
            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while(resultSet.next())
            {
                table_name.add(resultSet.getString("TABLE_NAME"));
            }
            
            for (int i = 0; i<table_name.size();i++)
            {
            	String name = table_name.get(i);
            	ResultSet columns = databaseMetaData.getColumns(null,null, name, null);
            	JsonObject jsonObject = new JsonObject();
            	
                jsonObject.addProperty("table_name", name);
        		jsonObject.add("column_name", new JsonArray());
        		jsonObject.add("data_type", new JsonArray());
        		jsonObject.add("column_size", new JsonArray());
        		jsonObject.add("is_nullable", new JsonArray());
            	while(columns.next())
                {
            		
                    String columnName = columns.getString("COLUMN_NAME");
                    String datatype = columns.getString("TYPE_NAME");
                    String columnsize = columns.getString("COLUMN_SIZE");
                    String isNullable = columns.getString("IS_NULLABLE");
                    
                    jsonObject.getAsJsonArray("column_name").add(columnName);
                    jsonObject.getAsJsonArray("data_type").add(datatype);
                    jsonObject.getAsJsonArray("column_size").add(columnsize);
                    jsonObject.getAsJsonArray("is_nullable").add(isNullable);

                }
            	jsonArray.add(jsonObject);
            }
            
            out.write(jsonArray.toString());
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
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	/*
        String cnumber = request.getParameter("Cnumber");
        String fname = request.getParameter("Fname");
        String lname = request.getParameter("Lname");
        String edate = request.getParameter("Edate");
        */
        String form_name = request.getParameter("form_name");
        PrintWriter out = response.getWriter();
        //System.out.println(form_name);
        try
        {
        	Connection dbcon = dataSource.getConnection();
        	//dbcon.setAutoCommit(false);
        	Statement statement = dbcon.createStatement();
        	
	        if(form_name.equals("add_star"))
	        {
	        	String star_name = request.getParameter("star_name");
	        	String birth_year = request.getParameter("birth_year");
	        	if(birth_year.equals(""))
	        	{
	        		birth_year = "-1";
	        	}
	        	if(star_name.equals(""))
	        	{
	        		JsonObject responseJsonObject = new JsonObject();
	        		responseJsonObject.addProperty("fail_type", "star");
	        		responseJsonObject.addProperty("add_status", "fail");
	        		responseJsonObject.addProperty("add_message", "Star name cannot be empty.");
	        		response.getWriter().write(responseJsonObject.toString());
	        	}
	        	else {
	        	PreparedStatement pstatement1 = null;
	        	//String query1 = "CALL add_single_star(" + "'"+ star_name +"'," + birth_year + ")";
	        	String query1 = "CALL add_single_star(?,?)";
	        	//statement.execute("CALL add_single_star(" + "'"+ star_name +"'," + birth_year + ")");
	        	pstatement1 = dbcon.prepareStatement(query1);
	        	pstatement1.setString(1, star_name);
	        	pstatement1.setInt(2, Integer.valueOf(birth_year));
	        	pstatement1.executeUpdate();
	        	JsonObject responseJsonObject = new JsonObject();
	        	responseJsonObject.addProperty("add_status", "success");
	        	responseJsonObject.addProperty("success_type", "star");
	        	responseJsonObject.addProperty("success_message", "Successfully adding the star.");
	        	response.getWriter().write(responseJsonObject.toString());
	        	
	        	
	        	dbcon.close();
	        	}
	        	
	        }
	        
	        else if(form_name.equals("add_movie"))
	        {
	        	String movie_title = request.getParameter("m_title");
	        	String movie_year = request.getParameter("m_year");
	        	String movie_director = request.getParameter("m_director");
	        	String movie_star = request.getParameter("m_star");
	        	String movie_genre = request.getParameter("m_genre");
	        	String movie_rating = request.getParameter("m_rating");
	        	
	        	if(movie_rating.equals(""))
	        	{
	        		movie_rating = "0";
	        	}
	        	
	        	String missing = "";
	        	boolean miss = false;
	        	if(movie_title.equals(""))
	        	{
	        		miss = true;
	        		missing += "movie title,";
	        	}
	        	if(movie_year.equals(""))
	        	{
	        		miss = true;
	        		missing += "movie year,";
	        	}
	        	if(movie_director.equals(""))
	        	{
	        		miss = true;
	        		missing += "movie director,";
	        	}
	        	
	        	if(miss)
	        	{
	        		JsonObject responseJsonObject = new JsonObject();
	        		responseJsonObject.addProperty("fail_type", "movie");
	        		responseJsonObject.addProperty("add_status", "fail");
	        		responseJsonObject.addProperty("add_message", missing+"can not be empty.");
	        		response.getWriter().write(responseJsonObject.toString());
	        		
	        		//dbcon.commit();
	        		dbcon.close();
	        	}
	        	
	        	// add movie to the database.....
	        	else {
	        	PreparedStatement pstatement2 = null;
	        	//String add_movie_query = "CALL add_movie(" + "'" + movie_title + "'" + "," + movie_year + "," + "'" + movie_director + "'," + movie_rating + ")";
	        	String add_movie_query = "CALL add_movie(?,?,?,?)";
	        	pstatement2 = dbcon.prepareStatement(add_movie_query);
	        	pstatement2.setString(1, movie_title);
	        	pstatement2.setString(2, movie_year);
	        	pstatement2.setString(3, movie_director);
	        	pstatement2.setString(4, movie_rating);
	        	ResultSet rs = pstatement2.executeQuery();
	        	rs.next();
	        	String new_movie_id = rs.getString("result");
	        	if(!new_movie_id.equals("-1"))
	        	{
	        		
	        		for(String star: movie_star.split(","))
	        		{
	        			PreparedStatement pstatement3 = null;
	        			//String query3 = "CALL add_star_movie(" + "'" + new_movie_id + "'" + "," + "'" + star + "'"+ ")";
	        			String query3 = "CALL add_star_movie(?,?)";
	        			pstatement3 = dbcon.prepareStatement(query3);
	        			pstatement3.setString(1, new_movie_id);
	        			pstatement3.setString(2, star);
	        			pstatement3.executeUpdate();
	        		}
	        		
	        		for(String genre: movie_genre.split(","))
	        		{
	        			PreparedStatement pstatement4 = null;
	        			//String query4 = "CALL add_genre_movie(" +  "'" + new_movie_id + "'," + "'" + genre + "'" + ")";
	        			String query4 = "CALL add_genre_movie(?,?)";
	        			pstatement4 = dbcon.prepareStatement(query4);
	        			pstatement4.setString(1, new_movie_id);
	        			pstatement4.setString(2, genre);
	        			pstatement4.executeUpdate();
	        		}
	        		
	        		JsonObject responseJsonObject = new JsonObject();
		        	responseJsonObject.addProperty("add_status", "success");
		        	responseJsonObject.addProperty("success_type", "movie");
		        	responseJsonObject.addProperty("success_message", "Successfully adding the movie.");
		        	response.getWriter().write(responseJsonObject.toString());
	        	}
	        	else
	        	{
	        		JsonObject responseJsonObject = new JsonObject();
		        	responseJsonObject.addProperty("add_status", "fail");
		        	responseJsonObject.addProperty("fail_type", "movie");
		        	responseJsonObject.addProperty("add_message", "The movie that you are trying to add already exists.");
		        	response.getWriter().write(responseJsonObject.toString());
	        	}
	        	}
	        	dbcon.close();
	        }
	        }
        
        catch (Exception e)
        {
        	JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
        }
        /*
        try {
        	
        	Connection dbcon = dataSource.getConnection();
        	Statement statement = dbcon.createStatement();
        	
        	String query = "SELECT * from creditcards where id = '" + cnumber + "'" + "and firstName = '" + fname + "'"+ "and lastName = '" + lname + "'"+ "and expiration = '" + edate + "'";
        	
        	ResultSet rs = statement.executeQuery(query);
        	
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
        } else {
            // Login fails
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("checkout_status", "fail");

            responseJsonObject.addProperty("checkout_message", "incorrect payment information");
            response.getWriter().write(responseJsonObject.toString());
        }
    } catch (Exception e)
        {
    		response.setStatus(500);
        }
        */
}
    
}
