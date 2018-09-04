package eu.odalic.extrarelatable.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.odalic.extrarelatable.algorithms.graph.aggregation.ResultAggregator;
import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.model.annotation.MeasuredNode;
import eu.odalic.extrarelatable.model.graph.PropertyTreesMergingStrategy;
import eu.odalic.extrarelatable.util.UuidGenerator;

@Configuration
public class PropertyBeanConfiguration {

	@Autowired
	private ApplicationContext context;

	@Bean
	public PropertyTreesMergingStrategy PropertyTreesMergingStrategy(
			@Value("${eu.odalic.extrarelatable.propertyTreesMergingStrategy:propertyUriLabelTextFallback}") String qualifier) {
		return (PropertyTreesMergingStrategy) context.getBean(qualifier);
	}
	
	@Bean
	public CsvTableParser CsvTableParser(
			@Value("${eu.odalic.extrarelatable.csvTableParser:automatic}") String qualifier) {
		return (CsvTableParser) context.getBean(qualifier);
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public ResultAggregator<MeasuredNode> PropertiesResultAggregator(
			@Value("${eu.odalic.extrarelatable.propertiesResultAggregator:averageDistance}") String qualifier) {
		return (ResultAggregator<MeasuredNode>) context.getBean(qualifier);
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public ResultAggregator<MeasuredNode> LabelsResultAggregator(
			@Value("${eu.odalic.extrarelatable.labelsResultAggregator:averageDistance}") String qualifier) {
		return (ResultAggregator<MeasuredNode>) context.getBean(qualifier);
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public ResultAggregator<MeasuredNode> PairsResultAggregator(
			@Value("${eu.odalic.extrarelatable.pairsResultAggregator:averageDistance}") String qualifier) {
		return (ResultAggregator<MeasuredNode>) context.getBean(qualifier);
	}
	
	@Bean
	public UuidGenerator UuidGenerator(
			@Value("${eu.odalic.extrarelatable.uuidGenerator:default}") String qualifier) {
		return (UuidGenerator) context.getBean(qualifier);
	}
}