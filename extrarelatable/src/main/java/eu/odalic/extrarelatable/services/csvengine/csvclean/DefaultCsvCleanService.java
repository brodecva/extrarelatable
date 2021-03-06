/**
 * 
 */
package eu.odalic.extrarelatable.services.csvengine.csvclean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link CsvCleanService}. Uses Jersey client to
 * connect to the remote CSV Clean service and request the cleaning. To avoid
 * overwhelming the service with too many requests, the service is limited to
 * one request per two seconds.
 * 
 * @author Václav Brodec
 *
 */
@Service
public final class DefaultCsvCleanService implements CsvCleanService {

	private static final String BODY_PART_NAME = "csv_file";
	private static final String TARGET_PATH = "https://data.wu.ac.at/csvengine/api/v1/clean/";
	private static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv");
	private static final long MINIMUM_QUERY_INTERVAL_MILIS = 2000;

	private Client client;

	private Instant lastQueried = null;

	public DefaultCsvCleanService() {
		this.client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	}

	@Override
	public InputStream clean(final File file) throws IOException {
		throttleRequests();

		final WebTarget target = client.target(TARGET_PATH);
		try (final FormDataMultiPart multipartEntity = new FormDataMultiPart()) {
			final MultiPart multiPartWithFile = multipartEntity
					.bodyPart(new FileDataBodyPart(BODY_PART_NAME, file, CSV_MEDIA_TYPE));

			final Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(multiPartWithFile, multiPartWithFile.getMediaType()));

			if (!isSuccessful(response)) {
				throw new IllegalStateException("The request to clean the file failed: " + response.getStatus() + "["
						+ response.readEntity(String.class) + "]");
			}

			return response.readEntity(InputStream.class);
		}
	}

	private void throttleRequests() {
		if (lastQueried == null) {
			lastQueried = Instant.now();
		} else {
			final Instant now = Instant.now();
			final long differenceMilis = lastQueried.until(now, ChronoUnit.MILLIS);

			try {
				Thread.sleep(Math.max(MINIMUM_QUERY_INTERVAL_MILIS - differenceMilis, 0));
			} catch (final InterruptedException e) {
				throw new IllegalStateException(e);
			}

			lastQueried = Instant.now();
		}
	}

	private static boolean isSuccessful(final Response response) {
		return response.getStatusInfo().getFamily() == Family.SUCCESSFUL;
	}
}
