/**
 * 
 */
package com.sivalabs.jpcg.core.metadata;

import java.util.List;

/**
 * @author Siva
 *
 */
public class TableMetadata {
	private String name;
	private List<ColumnMetadata> columns;
	private List<ColumnMetadata> pkColumns;
	
	
	@Override
	public String toString() {
		return "TableMetadata [name=" + name + ", columns=" + columns + ", pkColumns=" + pkColumns + "]";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ColumnMetadata> getColumns() {
		return columns;
	}
	public void setColumns(List<ColumnMetadata> columns) {
		this.columns = columns;
	}
	public List<ColumnMetadata> getPkColumns() {
		return pkColumns;
	}
	public void setPkColumns(List<ColumnMetadata> pkColumns) {
		this.pkColumns = pkColumns;
	}
	
}
