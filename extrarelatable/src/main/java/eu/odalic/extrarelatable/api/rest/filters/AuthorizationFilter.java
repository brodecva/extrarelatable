/**
 *
 */
package eu.odalic.extrarelatable.api.rest.filters;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import eu.odalic.extrarelatable.api.rest.Secured;
import eu.odalic.extrarelatable.api.rest.responses.Message;
import eu.odalic.extrarelatable.api.rest.util.Role;

/**
 * Role authorization filter.
 *
 * @author Václav Brodec
 *
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
@Component
public class AuthorizationFilter implements ContainerRequestFilter {

  private static Set<Role> extractRoles(final AnnotatedElement annotatedElement) {
    Preconditions.checkNotNull(annotatedElement, "The annotatedElement cannot be null!");

    final Secured secured = annotatedElement.getAnnotation(Secured.class);
    if (secured == null) {
      return ImmutableSet.of();
    }

    return ImmutableSet.copyOf(secured.value());
  }

  /*@Autowired
  private UserService userService;*/

  @Context
  private UriInfo uriInfo;

  @Context
  private ResourceInfo resourceInfo;

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final String userId = requestContext.getSecurityContext().getUserPrincipal().getName();

    final Method resourceMethod = this.resourceInfo.getResourceMethod();
    Preconditions.checkArgument(resourceMethod != null, "The resource method information is not available. Cannot authorize!");

    final Set<Role> methodRoles = extractRoles(resourceMethod);

    try {
      if (methodRoles.isEmpty()) {
        final Class<?> resourceClass = this.resourceInfo.getResourceClass();
        Preconditions.checkArgument(resourceClass != null, "The resource class information is not available. Cannot authorize!");

        final Set<Role> classRoles = extractRoles(resourceClass);
        Preconditions.checkArgument(!classRoles.isEmpty(), "There are no roles with access permission!");

        checkPermissions(userId, classRoles);
        return;
      }

      checkPermissions(userId, methodRoles);
    } catch (final Exception e) {
      requestContext
          .abortWith(Message.of("Authorization failed. Insufficient rights!", e.getMessage())
              .toResponse(Status.FORBIDDEN, this.uriInfo));
      return;
    }
  }

  private void checkPermissions(final String userId, final Set<? extends Role> allowedRoles)
      throws Exception {
    /*final User user = this.userService.getUser(userId);

    checkArgument(allowedRoles.contains(user.getRole()), "User's role does not allow access!");*/
  }
}
