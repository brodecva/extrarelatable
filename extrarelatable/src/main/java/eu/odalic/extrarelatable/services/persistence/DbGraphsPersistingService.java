package eu.odalic.extrarelatable.services.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;

/**
 * File based {@link GraphsPersitingService}. Uses {@link DB} under the hood.
 *
 * @author Václav Brodec
 *
 */
@Service
public final class DbGraphsPersistingService implements GraphsPersitingService {

	private final DB db;

	private final Map<String, BackgroundKnowledgeGraph> persistedGraphs;

	/**
	 * @param db
	 *            DB-managing instance
	 * @param persistedGraphs
	 *            DB-persisted map of names and graphs
	 */
	DbGraphsPersistingService(final DB db, final HTreeMap<String, BackgroundKnowledgeGraph> persistedGraphs) {
		checkNotNull(db);
		checkNotNull(persistedGraphs);

		this.db = db;
		this.persistedGraphs = persistedGraphs;
	}

	/**
	 * Initializes the DB from the provided path.
	 * 
	 * @param filePath
	 *            path to the DB file
	 */
	@SuppressWarnings("unchecked")
	public DbGraphsPersistingService(final Path filePath) {
		checkNotNull(filePath, "The DB file path cannot be null!");

		this.db = DBMaker.fileDB(filePath.toFile()).closeOnJvmShutdown().transactionEnable().make();
		this.persistedGraphs = this.db.hashMap("persistedGraphs", Serializer.STRING, Serializer.JAVA).createOrOpen();
	}

	/**
	 * Initializes the DB from the provided path string.
	 * 
	 * @param filePath
	 *            path string to the DB file
	 */
	@Autowired
	public DbGraphsPersistingService(@Value("${eu.odalic.extrarelatable.db.filePath}") final String filePath) {
		this(Paths.get(filePath));
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		this.db.close();
	}

	@Override
	public void persist(String name, BackgroundKnowledgeGraph graph) {
		this.persistedGraphs.put(name, new BackgroundKnowledgeGraph(graph));
		this.db.commit();
		this.db.getStore().compact();
	}

	@Override
	public void persist(Map<? extends String, ? extends BackgroundKnowledgeGraph> graphs) {
		this.persistedGraphs.clear();
		this.persistedGraphs.putAll(graphs.entrySet().stream().collect(ImmutableMap
				.toImmutableMap(entry -> entry.getKey(), entry -> new BackgroundKnowledgeGraph(entry.getValue()))));
		this.db.commit();
		this.db.getStore().compact();
	}

	@Override
	public Map<String, BackgroundKnowledgeGraph> load() {
		return this.persistedGraphs.entrySet().stream().collect(ImmutableMap.toImmutableMap(entry -> entry.getKey(),
				entry -> new BackgroundKnowledgeGraph(entry.getValue())));
	}

	@Override
	public void delete(String name) {
		this.persistedGraphs.remove(name);
		this.db.commit();
		this.db.getStore().compact();
	}
}
