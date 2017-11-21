import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.Cookie;

public class ForgotPassword extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setup the printwriter and document type 
		response.setContentType("text/html;charset=UTF-8");
		final PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";

		//print out the first bit of html
		out.println(docType+"<html>\n<head><title>Forgot Password</title></head>\n"
		        		   +"<body>\n <h1 align=\"center\">");

		//Define the database parameters for this servlet
		String DB_TABLE    = "user";
		String DB_NAME     = "tigerbooz";
		String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;

		String emailInput  = request.getParameter("email");
		String dobInput  = request.getParameter("dob");
		
		//try to connect to db and search for the user
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for all entries with that email and date of birth
			Statement stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE email='"+emailInput+"' AND dob='"+dobInput+"';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//look through result set for the user's name and password
			if(rs.next()){
				out.println("<h2>"+rs.getString("name")+", your password is "+rs.getString("password")+"</h2>\n");
				out.println("Redirecting you to TigerBooz home\n");
		 		//create the login token cookie
		 		Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(rs.getInt("id")));
		 		loginCookie.setMaxAge(60 * 60);

		 		//add the cookie to the response returned to the client
			 	response.addCookie(loginCookie);
			 	response.setHeader("Refresh", "5; URL=Home");
			}
			else {
				out.println("<h3>email and date of birth not found</h3>");
				out.println("Redirecting you to TigerBooz login page\n");
			 	response.setHeader("Refresh", "5; URL=http://52.26.169.0/");
			}

		
			
			//close all the connections to the db
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println("closed connections in forgotpassword");			

			out.println("</body></html>");
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}
	
    public ForgotPassword() {
    	super();    
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}