package eu.odalic.extrarelatable.experiments.configuration.odalic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.odalic.extrarelatable.algorithms.table.csv.CsvTableParser;
import eu.odalic.extrarelatable.util.UuidGenerator;

@Configuration
public class PropertyBeanConfiguration {

	@Autowired
	private ApplicationContext context;

	@Bean
	public CsvTableParser CsvTableParser(
			@Value("${eu.odalic.extrarelatable.csvTableParser:automatic}") String qualifier) {
		return (CsvTableParser) context.getBean(qualifier);
	}

	@Bean
	public UuidGenerator UuidGenerator(
			@Value("${eu.odalic.extrarelatable.uuidGenerator:default}") String qualifier) {
		return (UuidGenerator) context.getBean(qualifier);
	}
}