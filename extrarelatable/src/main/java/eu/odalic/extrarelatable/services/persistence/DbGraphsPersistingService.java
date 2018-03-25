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
 * File based {@link GraphsPersitingService}.
 *
 * @author Václav Brodec
 *
 */
@Service
public final class DbGraphsPersistingService implements GraphsPersitingService {

	private final DB db;
	
	private final Map<String, BackgroundKnowledgeGraph> persistedGraphs;

	DbGraphsPersistingService(final DB db, final HTreeMap<String, BackgroundKnowledgeGraph> persistedGraphs) {
		checkNotNull(db);
		checkNotNull(persistedGraphs);

		this.db = db;
		this.persistedGraphs = persistedGraphs;
	}

	@SuppressWarnings("unchecked")
	public DbGraphsPersistingService(final Path filePath) {
		checkNotNull(filePath, "The DB file path cannot be null!");

		this.db = DBMaker.fileDB(filePath.toFile()).closeOnJvmShutdown().transactionEnable().make();
		this.persistedGraphs = this.db.hashMap("persistedGraphs", Serializer.STRING, Serializer.JAVA).createOrOpen();
	}

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
	}

	@Override
	public void persist(Map<? extends String, ? extends BackgroundKnowledgeGraph> graphs) {
		this.persistedGraphs.clear();
		this.persistedGraphs.putAll(graphs.entrySet().stream().collect(ImmutableMap.toImmutableMap(entry -> entry.getKey(), entry -> new BackgroundKnowledgeGraph(entry.getValue()))));
		this.db.commit();
	}

	@Override
	public Map<String, BackgroundKnowledgeGraph> load() {
		return this.persistedGraphs.entrySet().stream().collect(ImmutableMap.toImmutableMap(entry -> entry.getKey(), entry -> new BackgroundKnowledgeGraph(entry.getValue())));
	}

	@Override
	public void delete(String name) {
		this.persistedGraphs.remove(name);
		this.db.commit();
	}
}
