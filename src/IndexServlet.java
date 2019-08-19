import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import java.util.Date;
import javax.annotation.Resource;
import com.google.gson.JsonArray;

/**
 * This IndexServlet is declared in the web annotation below, 
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    /**
     * handles POST requests to store session information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        Long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles GET requests to add and show the item list information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	try {
    		JsonArray jsonArray = new JsonArray();
    		String query = request.getParameter("query");
    		
    		if(query == null || query.trim().isEmpty())
    		{
    			response.getWriter().write(jsonArray.toString());
    			return;
    		}
    		
    		Connection dbcon = dataSource.getConnection();
    		String[] keywords = query.trim().split(" ");
    		
    		String generateSuggestion = "SELECT id, title FROM movies WHERE MATCH(title) AGAINST(";
    		String condition  = "'";
    		for(String keyword: keywords)
    		{
    			condition += keyword + "*" + " ";
    		}
    		condition = condition.trim() + "'";
    		generateSuggestion += condition + " IN BOOLEAN MODE)";
    		
    		PreparedStatement pstatement = null;
    		pstatement = dbcon.prepareStatement(generateSuggestion);
    		ResultSet rs = pstatement.executeQuery();
    		
    		int count = 0;
    		while(rs.next() && count < 10)
    		{
    			String movie_title = rs.getString("title");
    			String movie_id = rs.getString("id");
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("value", movie_title);
    			
    			JsonObject DatajsonObject = new JsonObject();
    			DatajsonObject.addProperty("id", movie_id);
    			
    			jsonObject.add("data", DatajsonObject);
    			jsonArray.add(jsonObject);
    			count++;
    		}
    		response.getWriter().write(jsonArray.toString());
    		dbcon.close();
    	}
    	catch (Exception e)
    	{
    		System.out.println(e);
    		response.sendError(500, e.getMessage());
    	}
        //String item = request.getParameter("item");
        //String keyword = request.getParameter("keyword");
        //System.out.println("Printed by IndexServlet: " + keyword);
        //System.out.println(item);
        //HttpSession session = request.getSession();
        
        //ArrayList<String> keywordList = new ArrayList<String> ();
        //keywordList.add(request.getParameter("title").replace(" ", "+"));
        //keywordList.add(request.getParameter("year"));
        //keywordList.add(request.getParameter("director").replace(" ", "+"));
        //keywordList.add(request.getParameter("starname").replace(" ", "+"));
        
        
        // get the previous items in a ArrayList
        /*
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
            previousItems.add(keyword);
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems.add(keyword);
            }
        }
		*/
        
        //response.getWriter().write(String.join(",", keywordList));
        //response.sendRedirect("http://localhost:8080/2019w-project2-login-cart-example/movie-list.html");
    	
    	
    }
}
