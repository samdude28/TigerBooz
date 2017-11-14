import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;


  Servlet implementation class Liquor
 
public class Liquor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Define the database parameters for this servlet
	private final static String DB_TABLE    = liquor;
	private final static String DB_NAME     = tigerbooz;
	private final static String DB_URL      = jdbcmysqllocalhost3306+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	
	

    public static String getLiquorNameByID(int liquorID) {
		String liquorName=;
		
    	try {
	    	Open a connection to the database
			Class.forName(com.mysql.jdbc.Driver);
			conn = (Connection) DriverManager.getConnection(DB_URL,root,ilovepizza);
	
			Create and execute a query for this
			String sql   = SELECT name FROM +DB_TABLE+ WHERE id=+liquorID+;;
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			if there were results, set the name for that record 
			if(rs.next())
				liquorName = rs.getString(name);
			
			close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println(connection closed in Liquor.getLiquorNameByID);	 		
		}
		catch (ClassNotFoundException  SQLException e) {
			e.printStackTrace();
		}
		
		return liquorName;
    }
}
