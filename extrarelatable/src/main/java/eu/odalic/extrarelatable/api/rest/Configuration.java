/**
 *
 */
package eu.odalic.extrarelatable.api.rest;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import eu.odalic.extrarelatable.api.rest.filters.AuthenticationFilter;
import eu.odalic.extrarelatable.api.rest.filters.AuthorizationFilter;
import eu.odalic.extrarelatable.api.rest.filters.CorsResponseFilter;
import eu.odalic.extrarelatable.api.rest.filters.LoggingResponseFilter;
import eu.odalic.extrarelatable.api.rest.resources.GraphResource;
import eu.odalic.extrarelatable.api.rest.resources.WelcomeResource;
import eu.odalic.extrarelatable.api.rest.responses.ThrowableMapper;

/**
 * Configures the provided resources, filters and features.
 *
 * @author Václav Brodec
 *
 * @see org.glassfish.jersey.server.ResourceConfig
 */
public final class Configuration extends ResourceConfig {

  public Configuration() throws JAXBException {
    /*
     * Jersey JSON exception mapping bug workaround.
     *
     * https://java.net/jira/browse/JERSEY-2722
     *
     */
    register(JacksonJaxbJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);

    // Resources registration
    register(WelcomeResource.class);
    register(GraphResource.class);

    // Filters registration
    register(RequestContextFilter.class);
    register(LoggingResponseFilter.class);
    register(CorsResponseFilter.class);
    register(AuthenticationFilter.class);
    register(AuthorizationFilter.class);

    // Features registration
    register(JacksonFeature.class);
    register(MultiPartFeature.class);

    // Exception mappers registration
    register(ThrowableMapper.class);

    // Prevent the container to interfere with the error entities.
    property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
  }
}
