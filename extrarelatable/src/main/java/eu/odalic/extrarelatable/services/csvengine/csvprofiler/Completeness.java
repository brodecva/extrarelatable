package eu.odalic.extrarelatable.services.csvengine.csvprofiler;

import java.util.List;

/**
 * POJ to which the completeness part of the JSON output of the CSV Profiler is
 * mapped.
 * 
 * @author Václav Brodec
 *
 */
public class Completeness {
	private List<Double> columns;
	private Double data;
	private Double header;
	private Double rows;
	private Double table;

	public Completeness() {
	}

	public List<Double> getColumns() {
		return columns;
	}

	public void setColumns(final List<Double> columns) {
		this.columns = columns;
	}

	public Double getData() {
		return data;
	}

	public void setData(Double data) {
		this.data = data;
	}

	public Double getHeader() {
		return header;
	}

	public void setHeader(Double header) {
		this.header = header;
	}

	public Double getRows() {
		return rows;
	}

	public void setRows(Double rows) {
		this.rows = rows;
	}

	public Double getTable() {
		return table;
	}

	public void setTable(Double table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return "Completeness [columns=" + columns + ", data=" + data + ", header=" + header + ", rows=" + rows
				+ ", table=" + table + "]";
	}
}
