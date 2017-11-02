import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nathanael Bishop 
 * Simple login prototype intended for Apache & Tomcat integration 
 * 
 * SQL:
 *			CREATE DATABASE 4330;
 *			USE 4330;
 *			CREATE TABLE logins(name VARCHAR(30), password VARCHAR(30), email VARCHAR(30));		
 */

public class Signup extends HttpServlet {
	
	//Don't really need to mess with these
	private static final long serialVersionUID = 1L;
    public Signup() {        super();    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	/**
	 * Using information from a html form post, attempt to add that user to the
	 *   mysql database
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type, print writer, and declare the document html type
		response.setContentType("text/html;charset=UTF-8");
		final PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		
		//print out the first bit of html
		out.println(docType+"<html>\n<head><title>User registration</title></head>\n"+
		        "<body>\n <h1 align=\"center\">");
		
		//setting up variables passed from the html	form	
		String name  = request.getParameter("name");
		String pass  = request.getParameter("password");
		String email = request.getParameter("email");
		
		// JDBC driver name and database URL
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		String DB_NAME = "logins";
		String DB_URL  = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";
		Connection conn=null;
		Statement stmt=null;

		//try to write the data and close the connection
		try {
			//open a connection
			Class.forName(JDBC_DRIVER);
			conn=(Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");

			//create the statement to write the data to the database
			//**** TO DO: add check for existing username first  ****
			stmt=(Statement) conn.createStatement();
			String sql="INSERT INTO student(name, password, email)"+ 
					   "VALUES ('"+name+"','"+pass+"','"+email+"');";

			//send that statement to the db and commit
			stmt.executeUpdate(sql);
			conn.commit();
			
		 	//now that we have all the data sent, print welcome message
			out.println("<h1><br>Welcome "+name+"</h1><ul>"+
				        "<b>You're registered with email</b>: "+email+"\n");
		} catch (ClassNotFoundException | SQLException e) {
			out.println("<h1>Database Error</h1>");
		} finally {
			//finally, attempt close the connection
			try {
				if(stmt!=null)
					conn.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//close out the html tags
		out.println("</body></html>");
	}
}