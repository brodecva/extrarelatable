package eu.odalic.extrarelatable.services.persistence;

import java.util.Map;

import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;

/**
 * Service that allows to persists the current state of the
 * {@link BackgroundKnowledgeGraph}.
 * 
 * @author Václav Brodec
 *
 */
public interface GraphsPersitingService {
	/**
	 * Saves the graph.
	 * 
	 * @param name name of the graph
	 * @param graph the graph
	 */
	void persist(String name, BackgroundKnowledgeGraph graph);

	/**
	 * Saves the graphs.
	 * 
	 * @param graphs map of graph names to graphs
	 */
	void persist(Map<? extends String, ? extends BackgroundKnowledgeGraph> graphs);

	/**
	 * Deletes the persisted version of the graph.
	 * 
	 * @param name graph name
	 */
	void delete(String name);

	/**
	 * Retrieves the persisted versions of graphs along with their names.
	 * 
	 * @return map of graph names to graphs as stored
	 */
	Map<String, BackgroundKnowledgeGraph> load();
}
