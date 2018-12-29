/**
 * 
 */
package eu.odalic.extrarelatable.services.odalic;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.services.odalic.responses.ResultReply;
import eu.odalic.extrarelatable.services.odalic.values.ComputationInputValue;
import eu.odalic.extrarelatable.services.odalic.values.ComputationValue;
import eu.odalic.extrarelatable.services.odalic.values.ResultValue;

/**
 * Service that connects to the configured Odalic instance and retrieves the
 * context provided by its annotation run.
 * 
 * @author Václav Brodec
 *
 */
@Service
public final class OdalicContextCollectionService implements ContextCollectionService {

	private static final String COMPUTATIONS_SUBPATH = "computations";

	private static final String PATH_SEGMENT_DELIMITER = "/";

	private static final String USERS_SUBPATH = "users/";

	private static final long MINIMUM_QUERY_INTERVAL_MILIS = 2000;

	private final URI targetPath;
	private final int rowsLimit;
	private final ComputationInputConverter computationInputConverter;

	private Client client;
	private Instant lastQueried = null;

	/**
	 * Initializes the context-collecting service.
	 * 
	 * @param computationInputConverter converter of the ERT format to the one recognized by Odalic
	 * @param basePath URI string of the Odalic instance (e.g. "http://localhost:8080/odalic/")
	 * @param userId user ID used to query the Odalic instance
	 * @param rowsLimit limit on number of rows sent to Odalic
	 */
	@Autowired
	public OdalicContextCollectionService(final ComputationInputConverter computationInputConverter,
			final @Value("${eu.odalic.extrarelatable.odalic.basePath:http://localhost:8080/odalic/}") String basePath,
			final @Value("${eu.odalic.extrarelatable.odalic.userId:odalic@email.cz}") String userId,
			final @Value("${eu.odalic.extrarelatable.odalic.rowsLimit:200}") int rowsLimit) {
		checkNotNull(computationInputConverter);
		checkNotNull(basePath);
		checkNotNull(userId);
		checkArgument(rowsLimit >= 2, "The limit of input rows has to be at least two!");

		this.computationInputConverter = computationInputConverter;
		this.targetPath = URI.create(basePath).resolve(USERS_SUBPATH + userId + PATH_SEGMENT_DELIMITER)
				.resolve(COMPUTATIONS_SUBPATH);
		this.rowsLimit = rowsLimit;
		this.client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
	}

	@Override
	public ResultValue process(final ParsedTable table, final Set<? extends String> usedBases, final String primaryBase,
			final Random random) {
		checkNotNull(table);
		checkNotNull(usedBases);
		checkNotNull(primaryBase);
		checkArgument(!usedBases.isEmpty(), "At least one base has to be provided!");
		checkArgument(usedBases.contains(primaryBase), "The used bases must contain the primary base!");

		final ComputationValue computation = prepareInput(table, usedBases, primaryBase, random);

		throttleRemoteQueries();

		final Response response = query(computation);

		if (!isSuccessful(response)) {
			throw new IllegalStateException("The request to process the file failed: " + response.getStatus() + "["
					+ response.readEntity(String.class) + "]");
		}

		return read(response);
	}

	private ResultValue read(final Response response) {
		return response.readEntity(ResultReply.class).getPayload();
	}

	private Response query(final ComputationValue computation) {
		final WebTarget target = client.target(this.targetPath);
		final Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(computation, MediaType.APPLICATION_JSON_TYPE));
		return response;
	}

	private ComputationValue prepareInput(final ParsedTable table, final Set<? extends String> usedBases,
			final String primaryBase, final Random random) {
		final ComputationInputValue computationInput = this.computationInputConverter.convert(table, this.rowsLimit,
				random);
		final ComputationValue computation = new ComputationValue(computationInput, usedBases, primaryBase, false);
		return computation;
	}

	private void throttleRemoteQueries() {
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
