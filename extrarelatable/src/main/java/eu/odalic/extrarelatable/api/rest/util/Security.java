package eu.odalic.extrarelatable.api.rest.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;

/**
 * REST API security utility methods.
 *
 * @author Václav Brodec
 *
 */
public final class Security {



  /**
   * <p>
   * Verifies that the authenticated user is either {@link Role#ADMINISTRATOR} or it can access
   * resources under the provided user ID.
   * </p>
   *
   * <p>
   * Raises standard {@link WebApplicationException}s when respective authentication and
   * authorization requirements are not met.
   * </p>
   *
   * @param securityContext a {@link SecurityContext} instance
   * @param userId user ID
   */
  public static void checkAuthorization(final SecurityContext securityContext,
      final String userId) {
    checkNotNull(userId, "The user ID cannot be null!");

    final Principal userPrincipal = securityContext.getUserPrincipal();
    if (userPrincipal == null) {
      throw new BadRequestException("No authenticated user!");
    }

    if (securityContext.isUserInRole(Role.ADMINISTRATOR.toString())) {
      return;
    }

    if (userId.equals(userPrincipal.getName())) {
      return;
    }

    throw new ForbiddenException(
        "The authenticated user is not authorized to access the resource!");
  }

  private Security() {}
}
