package eu.odalic.extrarelatable.services.csvengine.csvprofiler;

import java.util.List;
import java.util.Map;

import eu.odalic.extrarelatable.model.bag.Type;

public class CsvProfile {
	private int columns;
	private Completeness completeness;
	private String delimiter;
	private List<Integer> distinct;
	private String encoding;
	private List<String> header;
	private Map<Integer, List<Double>> outliers;
	private String quotechar;
	private int rows;
	private List<Type> types;
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public Completeness getCompleteness() {
		return completeness;
	}
	public void setCompleteness(Completeness completeness) {
		this.completeness = completeness;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public List<Integer> getDistinct() {
		return distinct;
	}
	public void setDistinct(List<Integer> distinct) {
		this.distinct = distinct;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public List<String> getHeader() {
		return header;
	}
	public void setHeader(List<String> header) {
		this.header = header;
	}
	
	//@XmlAnyElement
	//@JsonDeserialize(contentUsing = OutliersValueListDeserializer.class)
	public Map<Integer, List<Double>> getOutliers() {
		return outliers;
	}
	public void setOutliers(Map<Integer, List<Double>> outliers) {
		this.outliers = outliers;
	}
	public String getQuotechar() {
		return quotechar;
	}
	public void setQuotechar(String quotechar) {
		this.quotechar = quotechar;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public List<Type> getTypes() {
		return types;
	}
	public void setTypes(List<Type> types) {
		this.types = types;
	}
	@Override
	public String toString() {
		return "CsvProfile [columns=" + columns + ", completeness=" + completeness + ", delimiter=" + delimiter
				+ ", distinct=" + distinct + ", encoding=" + encoding + ", header=" + header + ", outliers=" + outliers
				+ ", quotechar=" + quotechar + ", rows=" + rows + ", types=" + types + "]";
	}
}
