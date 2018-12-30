package eu.odalic.extrarelatable.api.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.webcohesion.enunciate.metadata.Ignore;

/**
 * Welcome page.
 *
 * @author Václav Brodec
 */
@Path("/")
@Ignore
public final class WelcomeResource {

	private static final String WELCOME_PAGE_CONTENT = "<html>" + "<title>ExtraRelaTable REST API</title>" + "<body>"
			+ "<h1>ExtraRelaTable REST API is working!</h1>" + "<p>For more information about ExtraRelaTable visit "
			+ "<a href=\"https://github.com/brodecva/extrarelatable\">the project GitHub page.</a>" + "</p>" + "</body>"
			+ "</html>";

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String welcome() {
		return WELCOME_PAGE_CONTENT;
	}
}
