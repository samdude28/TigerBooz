import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nathanael Bishop 
 * TigerBooz login servlet 
 * 		
 * The starting page for TigerBooz is http://52.26.169.0
 */

public class Login extends HttpServlet {

	//Don't really need to mess with these
	private static final long serialVersionUID = 1L;
    public Login() {        super();    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

    /**
	 * Using information from a html form post, attempt to load a user's info
	 *   from the mysql server. If found display it, otherwise inform user.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type, print writer, and declare the document html type
		response.setContentType("text/html;charset=UTF-8");
		final PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		
		//create the first bit of html to be displayed
		out.println(docType + "<html><head><title>User Login</title></head><body>\n");
		
		// JDBC driver name, database URL, and init connection/statement
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		String DB_TABLE    = "user";
		String DB_NAME     = "tigerbooz";
		String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;
		Connection conn = null;
		Statement  stmt = null;

		//get the name & password from the HTML form and setup a success/fail var
		String nameInput=request.getParameter("name");
		String passInput=request.getParameter("password");
		Boolean foundName=false;
		
		//declare the SQL variables
		String name, password;
		
		//try to connect to db and search for the user
		try {
			//Open a connection
			Class.forName(JDBC_DRIVER);
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE;
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//look through result set for the user's name and password
			while(rs.next()){
				//Retrieve by column name (from the SQL server)
				name     = rs.getString("name");
				password = rs.getString("password");
				
				//if we find a match, print it
			 	if(name.equals(nameInput) && password.equals(passInput)) { 
			 		foundName=true;
			 		out.println("<h1><br>Welcome "+name+"</h1>");
			 	}
			}
			//if no name match, inform the user and give a way back
			if(!foundName)
				out.println("<h1>Incorrect name or password!</h1><a href='index.html'>Go Back</a>");
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		//finally close out the html tags
		out.println("</body></html>");
	}
}