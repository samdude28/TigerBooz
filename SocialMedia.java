import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SocialMedia
 */
public class SocialMedia extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * Given the liquor's unique ID, create a table in HTML that contains the facebook and twitter logos
	 *   that when clicked send the user to that companies appropriate social media page
	 * @param liquorID the unique ID that identifies the liquor in the TigerBooz database
	 * @return a String that contains a self contained HTML table with the social media buttons
	 */
	public static String getSocialMediaButtons(int liquorID) {
		//retrieve the liquor's name and category and initialize the html table to be returned
		String liquorName         = Liquor.getLiquorNameByID(liquorID); 
		String liquorCategory     = Liquor.getLiquorCategoryByID(liquorID);
		String socialMediaButtons = "<table>\n<tr><td>";
		
		//create the clickable facebook and the twitter icons 
		socialMediaButtons += "<a href='https://www.google.com/search?btnI=3564&q="+liquorName+"%20"+liquorCategory+"%20facebook'>"
				           +  "<img src='http://52.26.169.0/pictures/facebook.png' width='60' height='60'></a></td>\n<td>";
		socialMediaButtons += "<a href='https://www.google.com/search?btnI=3564&q="+liquorName+"%20"+liquorCategory+"%20twitter'>"
						   +  "<img src='http://52.26.169.0/pictures/twitter.png' width='60' height='60'></a></td></tr></table>\n";

		//return the html table
		return socialMediaButtons;
	}
	
    public SocialMedia() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
