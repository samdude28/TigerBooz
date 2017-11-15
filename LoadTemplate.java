import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

/**
 * helper class to make doGet more readable. Load the Head and CSS template, 
 *    print the users name in the title, the body template and JS sorting 
 *    script, finally print the left side bar and the big table that holds
 *    all of the main content on the right together
 */
public class LoadTemplate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static void loadTemplate(String name, PrintWriter out) {
		//load the file with the template that contains the head and CSS
		String printTemplate = "tigerbooz.html";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(printTemplate); 
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
		
		
		
		//wrap up the left side bar and start the user content that goes on the right
		//out.println("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		//out.println("<td class='bigtable'>");
	}

}
