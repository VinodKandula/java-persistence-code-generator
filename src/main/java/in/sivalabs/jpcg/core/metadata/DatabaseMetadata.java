/**
 * 
 */
package in.sivalabs.jpcg.core.metadata;

import java.util.List;

/**
 * @author Siva
 *
 */
public class DatabaseMetadata {
	private String name;
	private List<TableMetadata> tables;
	
	
	@Override
	public String toString() {
		return "Database [name=" + name + ", tables=" + tables + "]";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<TableMetadata> getTables() {
		return tables;
	}
	public void setTables(List<TableMetadata> tables) {
		this.tables = tables;
	}
	
	
}
