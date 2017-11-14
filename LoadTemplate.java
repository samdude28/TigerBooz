import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

/**
 * Servlet implementation class LoadTemplate
 */
public class LoadTemplate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * helper method to make doGet more readable. Load the Head and CSS template, 
	 *    print the users name in the title, the body template and JS sorting 
	 *    script, finally print the left side bar
	 * @param name the users name (passed from the previous page
	 */
	public static void loadTemplate(String name, PrintWriter out) {
		//load the file with the template that contains the head and CSS
		String printTemplate = "headtemplate.html";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(printTemplate); 
//FileHelper.class.getClassLoader().getResourceAsStream(printTemplate)
//getClass().getClassLoader().getResourceAsStream(printTemplate);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		//while the file contains data, print it for the user to see
		try {
			while((printTemplate=reader.readLine()) != null)
				out.println(printTemplate);
			inputStream.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Print the title with user name
		out.println("<title>welcomes you "+name+"</title>");
		
		//load the template file that contains the body formatting (the sort script)
		printTemplate="bodytemplate.html";
		inputStream = classLoader.getResourceAsStream(printTemplate);
		reader = new BufferedReader(new InputStreamReader(inputStream));
		//while the file contains data, print it for the user to see
		try {
			while((printTemplate=reader.readLine()) != null)
				out.println(printTemplate);
			inputStream.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//big table contains all of the user viewable content
		out.println("<table><tr><td class='bigtable'>");

		//small table for the left side bar
		out.println("<table>");
		
		//left side bar link to the users home
		out.println("<tr><td><form action='/4330/Home' method='post'>" 
				  + "<input type='hidden' name='name' value="+name+">"  
				  + "<input type='image' src='http://52.26.169.0/pictures/TigerBooz.jpg' width=200 alt='Submit'>" 
				  + "</form><br><br><br><br></td></tr>");

		//left side bar 
		out.println("<tr><td><form action='/4330/DisplayLiquor' method='post'>"
				  + "<input type='hidden' name='liquorType' value='rum'>" 
				  + "<input type='image' src='http://52.26.169.0/pictures/rum.jpg' width=200 alt='Submit'>"
				  + "</form><br><br></td></tr>");
		
		
		//wrap up the left side bar and start the user content that goes on the right
		out.println("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		out.println("<td class='bigtable'>");
	}

}
