/**
 * 
 */
package com.sivalabs.jpcg.mybatis.generators;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sivalabs.jpcg.core.ArtifactGenerator;
import com.sivalabs.jpcg.core.DatabaseMetadataProvider;
import com.sivalabs.jpcg.core.configuration.Configuration;
import com.sivalabs.jpcg.core.configuration.TableConfig;
import com.sivalabs.jpcg.core.metadata.ColumnMetadata;
import com.sivalabs.jpcg.core.metadata.DatabaseMetadata;
import com.sivalabs.jpcg.core.metadata.TableMetadata;
import com.sivalabs.jpcg.core.utils.Utilities;

/**
 * @author Siva
 *
 */
public class MyBatisJavaArtifactsGenerator implements ArtifactGenerator
{
	private Configuration configuration = null;
	private MyBatisConfiguration mybatisConfiguration = null;
	
	public MyBatisJavaArtifactsGenerator(Configuration configuration) {
		this.configuration = configuration;
		this.mybatisConfiguration = new MyBatisConfiguration(configuration);
	}

	@Override
	public void generate()
	{
		try
		{
			generateJavaArtifacts();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void generateJavaArtifacts() throws Exception
	{
		Connection connection = this.configuration.getConnection();
		DatabaseMetadata dbMetadata = DatabaseMetadataProvider.getDatabaseMetadata(connection);
		
		Boolean generateForAllTables = this.configuration.getGenerateForAllTables();
		
		Set<String> tablesConfigured = new HashSet<>();
		List<TableConfig> tablesConfig = this.configuration.getTables();
		for (TableConfig tableConfig : tablesConfig)
		{
			tablesConfigured.add(tableConfig.getName());
		}
		List<TableMetadata> tables = dbMetadata.getTables();
		for (TableMetadata tableMD : tables) {
			if(generateForAllTables || tablesConfigured.contains(tableMD.getName().toLowerCase())){
				System.out.println("======= Generating Java Artifacts for Table : "+tableMD.getName()+" ========");
				this.generateJavaBean(tableMD);
				this.generateJavaMapper(tableMD);
				this.generateJavaService(tableMD);	
			}
		}		
	}

	private void generateJavaBean(TableMetadata tableMD) {
		String beanSource = this.buildJavaBeanSource(tableMD);
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		String className = tableConfig.getSingularClassName()+".java";
		String pack = configuration.getJavaBasePackage()+".entities";
		this.writeToDisk(className, pack, beanSource);
	}
	
	private String buildJavaBeanSource(TableMetadata tableMD) {
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		String className = tableConfig.getSingularClassName();
		
		String sourceCode = "";
		String pack = mybatisConfiguration.getJavaDomainPackage();
		sourceCode += "package "+pack+";\n";
		sourceCode += "public class "+className+" { ";
		List<ColumnMetadata> columns = tableMD.getColumns();
		String properties = "";
		String methods = "";
		
		for (ColumnMetadata columnMetadata : columns) {
			String propName = Utilities.convertUnderscoreNameToPropertyName(columnMetadata.getName());
			String type = DatabaseMetadataProvider.getJavaType(columnMetadata.getType());
			
			properties += "\t private "+type+" "+propName+";\n";
			//setter
			methods += "\t public void set"+Utilities.capitalize(propName)+"("+type+" "+propName+") {\n";
			methods += "\t\t this."+propName+" = "+ propName+";\n\t } \n";
			
			//getter
			methods += "\t public "+type+" get"+Utilities.capitalize(propName)+"() { \n";
			methods += "\t\t return this."+propName+";\n\t } \n";
			
		}
		
		sourceCode += "\n"+properties;
		sourceCode += "\n"+methods;
		
		sourceCode += "}";
		
		return sourceCode;
	}
	
	private void generateJavaMapper(TableMetadata tableMD) {
		
		String javaMapperTemplate = Utilities.classpathFileToString("Mapper.java.tmpl");
		Map<String, String> placeHolders = this.buildJavaMapperPlaceHolders(tableMD);
		Set<String> keys = placeHolders.keySet();
		
		String mapperJava = javaMapperTemplate;
				
		for (String key : keys) {
			mapperJava = mapperJava.replace(key, placeHolders.get(key));
		}
		
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		String mapperClassName = tableConfig.getSingularClassName()+"Mapper";
		String className = mapperClassName+".java";
		String pack = mybatisConfiguration.getJavaMappersPackage();
		this.writeToDisk(className, pack, mapperJava);		
	}

	private void generateJavaService(TableMetadata tableMD)
	{
		String javaMapperTemplate = Utilities.classpathFileToString("Service.java.tmpl");
		Map<String, String> placeHolders = this.buildJavaMapperPlaceHolders(tableMD);
		Set<String> keys = placeHolders.keySet();
		
		String mapperJava = javaMapperTemplate;
				
		for (String key : keys) {
			mapperJava = mapperJava.replace(key, placeHolders.get(key));
		}
		
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		String className = tableConfig.getSingularClassName()+"Service.java";
		String pack = mybatisConfiguration.getJavaServicesPackage();
		this.writeToDisk(className, pack, mapperJava);
		
	}
	
	private Map<String, String> buildJavaMapperPlaceHolders(TableMetadata tableMD) {
		
		Map<String, String>  map = new HashMap<String, String>();
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		List<ColumnMetadata> pkColumns = tableMD.getPkColumns();
		
		String mapperPackage = mybatisConfiguration.getJavaMappersPackage();
		String servicePackage = mybatisConfiguration.getJavaServicesPackage();
		String ClassFQN = mybatisConfiguration.getJavaDomainPackage()+"."+ tableConfig.getSingularClassName()+""; 
		String where_args = this.generateWhereArguments(pkColumns);
		String pk_args = generatePKArguments(where_args);
		
		map.put("#ClassFQN#", ClassFQN);
		map.put("#mapper_package#", mapperPackage);
		map.put("#service_package#", servicePackage);
		map.put("#instanceVar#", Utilities.camelCase(tableConfig.getSingularClassName()));		
		map.put("#TableName#", tableMD.getName());
		map.put("#TableSingular#", tableConfig.getSingularClassName());
		map.put("#TablePlural#", tableConfig.getPluralClassName());
		map.put("#where_args#", where_args);
		map.put("#pk_args#", pk_args);
		
		return map;
	}
	
	private String generateWhereArguments(List<ColumnMetadata> pkColumns) {
		String whereArguments = "";
		for (ColumnMetadata column : pkColumns) {
			whereArguments += ", "+DatabaseMetadataProvider.getJavaType(column.getType()) +" "
					+ Utilities.convertUnderscoreNameToPropertyName(column.getName());
		}
		
		if(whereArguments.length() > 0){
			return  whereArguments.substring(", ".length());
		}
		
		return "";
	}
	
	private String generatePKArguments(String whereArgs) {
		if(whereArgs == null || whereArgs.trim().length() ==0) return "";
		String[] tokens = whereArgs.split(" ");
		String pkArgs = "";
		for (int i = 0; i < tokens.length; i++)
		{
			if ( (i & 1) != 0 ){
				pkArgs += ", "+tokens[i];
			}
		}
		if(pkArgs.length() > 0){
			return  pkArgs.substring(", ".length());
		}
		
		return "";
	}
	
	private void writeToDisk(String className, String pack, String sourceCode) {
		String targetDir = configuration.getTargetDir();
		pack = pack.replace(".", "/");
		Utilities.writeFile(targetDir+"/"+pack+"/"+className, sourceCode);
	}

}
