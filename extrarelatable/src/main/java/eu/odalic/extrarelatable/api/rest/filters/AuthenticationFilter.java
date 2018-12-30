/**
 *
 */
package eu.odalic.extrarelatable.api.rest.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.api.rest.Secured;
import eu.odalic.extrarelatable.api.rest.responses.Message;

/**
 * <p>
 * Authentication filter.
 * </p>
 * 
 * <p>
 * Adapted from Odalic with permission.
 * </p>
 *
 * @author Václav Brodec
 *
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@Component
public final class AuthenticationFilter implements ContainerRequestFilter {

	private static final String INVALID_REQUEST_CHALLENGE_FORMAT = "Bearer realm=\"ExtraRelaTable\", error=\"invalid_request\", error_description=\"%s\"";
	/*
	 * private static final String INVALID_TOKEN_CHALLENGE_FORMAT =
	 * "Bearer realm=\"Odalic\", error=\"invalid_token\", error_description=\"%s\"";
	 */
	private static final String AUTHENTICATION_SCHEME = "Bearer";
	private static final String AUTHENTICATION_SCHEME_DELIMITER = " ";
	private static final Set<String> SECURE_PROTOCOLS_NAMES = ImmutableSet.of("https");

	/*
	 * @Autowired private UserService userService;
	 */

	@Context
	private UriInfo uriInfo;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null) {
			requestContext.abortWith(Message.of("Authorization header must be provided!")
					.toResponseBuilder(Response.Status.UNAUTHORIZED, this.uriInfo)
					.header(HttpHeaders.WWW_AUTHENTICATE,
							String.format(INVALID_REQUEST_CHALLENGE_FORMAT, "Authorization header must be provided!"))
					.build());
			return;
		}

		if (!authorizationHeader.startsWith(AUTHENTICATION_SCHEME + AUTHENTICATION_SCHEME_DELIMITER)) {
			requestContext
					.abortWith(Message.of("Authorization header must specify the supported authentication scheme!")
							.toResponseBuilder(Response.Status.UNAUTHORIZED, this.uriInfo)
							.header(HttpHeaders.WWW_AUTHENTICATE,
									String.format(INVALID_REQUEST_CHALLENGE_FORMAT,
											"Authorization header must specify the supported authentication scheme!"))
							.build());
			return;
		}

		requestContext.setSecurityContext(new SecurityContext() {

			@Override
			public String getAuthenticationScheme() {
				return AUTHENTICATION_SCHEME;
			}

			@Override
			public Principal getUserPrincipal() {
				return () -> "user";
			}

			@Override
			public boolean isSecure() {
				try {
					return SECURE_PROTOCOLS_NAMES
							.contains(AuthenticationFilter.this.uriInfo.getRequestUri().toURL().getProtocol());
				} catch (final Exception e) {
					return false;
				}
			}

			@Override
			public boolean isUserInRole(final String role) {
				return true;
			}
		});
	}
}
