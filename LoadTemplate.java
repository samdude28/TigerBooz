import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;

/**
 * @author Nathanael Bishop (of this particular Java servlet)
 * TigerBooz  CSC 4330 Project
 * These Java servlets represent the dynamic portion of the TigerBooz website, a website built to let people
 *   read and share ratings, reviews and prices of liquors. 
 */

public class LoadTemplate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
 	 * This method prints the website template when called.
 	 * @param name the name of the user who is accessing the website
 	 * @param out just gonna borrow this PrintWriter right quick
 	 */
	public static void loadTemplate(String name, PrintWriter out) {
		//the template file which contains the static HTML content (left and top parts of the page)
		String printTemplate = "tigerbooz.html";

		//load the file with the template that contains the head and CSS
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(printTemplate); 
		BufferedReader reader   = new BufferedReader(new InputStreamReader(inputStream));
		
		try {
			//while the file contains data, print it for the user to see
			while((printTemplate=reader.readLine()) != null)
				out.println(printTemplate);

			//finally close the inputStream and reader
			inputStream.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}