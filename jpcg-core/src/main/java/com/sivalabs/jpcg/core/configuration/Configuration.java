/**
 * 
 */
package com.sivalabs.jpcg.core.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.sivalabs.jpcg.core.utils.Utilities;

/**
 * @author Siva
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
	
	private JdbcConfig jdbcConfig;
	
	private Boolean generateForAllTables;
	@XmlElementWrapper(name = "tablesConfig")
    @XmlElement(name = "table")
	private List<TableConfig> tables;
	
	private String targetDir;
	private String javaBasePackage;
	
	public JdbcConfig getJdbcConfig() {
		return jdbcConfig;
	}

	public void setJdbcConfig(JdbcConfig jdbcConfig) {
		this.jdbcConfig = jdbcConfig;
	}

	public Boolean getGenerateForAllTables()
	{
		return generateForAllTables == null ? true: generateForAllTables;
	}

	public void setGenerateForAllTables(Boolean generateForAllTables)
	{
		this.generateForAllTables = generateForAllTables;
	}

	public List<TableConfig> getTables() {
		return tables;
	}

	public void setTables(List<TableConfig> tables) {
		this.tables = tables;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	public String getJavaBasePackage() {
		if(javaBasePackage == null || javaBasePackage.trim().length() == 0){
			return "com.mycompany.proj";
		}
		return javaBasePackage;
	}

	public void setJavaBasePackage(String javaBasePackage) {
		this.javaBasePackage = javaBasePackage;
	}

	public Connection getConnection() {
		try {
			String driver =  jdbcConfig.getDriver() ;
			String url = jdbcConfig.getUrl();
			String username = jdbcConfig.getUsername();
			String password = jdbcConfig.getPassword();

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public TableConfig getTableConfig(String name) {
		for (TableConfig table : tables) {
			if(name.equalsIgnoreCase(table.getName())){
				return table;
			}
		}
		TableConfig tableConfig = new TableConfig();
		tableConfig.setName(name);
		tableConfig.setPluralClassName(Utilities.convertUnderscoreNameToClassName(name)+"s");
		tableConfig.setSingularClassName(Utilities.convertUnderscoreNameToClassName(name));
		return tableConfig;
	}
	
	public String getTableSingularName(String name) {
		for (TableConfig table : tables) {
			if(name.equalsIgnoreCase(table.getName())){
				return table.getSingularClassName();
			}
		}
		return name;
	}	
	
	public String getTablePluralName(String name) {
		for (TableConfig table : tables) {
			if(name.equalsIgnoreCase(table.getName())){
				return table.getPluralClassName();
			}
		}
		return name;
	}	
	
}
