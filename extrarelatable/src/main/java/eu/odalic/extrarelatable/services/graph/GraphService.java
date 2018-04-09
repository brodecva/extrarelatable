package eu.odalic.extrarelatable.services.graph;

import java.io.IOException;
import java.io.InputStream;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.graph.SearchResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;
import eu.odalic.extrarelatable.model.table.csv.Format;

public interface GraphService {
	void create(String name);
	boolean exists(String name);
	void delete(String name);
	
	void learn(String graphName, ParsedTable table) throws IOException;
	void learn(String graphName, InputStream input, Format format, Metadata metadata) throws IOException;

	AnnotationResult annotate(String graphName, ParsedTable table) throws IOException;
	AnnotationResult annotate(String graphName, InputStream input, Format format, Metadata metadata) throws IOException;
	
	SearchResult search(String graphName, String pattern, Integer flags, int limit);
}
