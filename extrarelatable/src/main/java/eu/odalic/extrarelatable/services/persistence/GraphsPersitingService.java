package eu.odalic.extrarelatable.services.persistence;

import java.util.Map;

import eu.odalic.extrarelatable.model.graph.BackgroundKnowledgeGraph;

public interface GraphsPersitingService {
  void persist(String name, BackgroundKnowledgeGraph graph);
  void persist(Map<? extends String, ? extends BackgroundKnowledgeGraph> graphs);
  
  void delete(String name);
  
  Map<String, BackgroundKnowledgeGraph> load();
}
