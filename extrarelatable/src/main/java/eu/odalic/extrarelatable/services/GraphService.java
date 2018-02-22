package eu.odalic.extrarelatable.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import eu.odalic.extrarelatable.model.annotation.Annotation;
import eu.odalic.extrarelatable.model.annotation.AnnotationResult;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.csv.Format;

public interface GraphService {
	void create(final String name);
	
	void learn(String graphName, InputStream input, Format format, Metadata metadata) throws IOException;

	Map<Integer, Annotation> annotate(String graphName, InputStream input, Format format, Metadata metadata) throws IOException;
}
