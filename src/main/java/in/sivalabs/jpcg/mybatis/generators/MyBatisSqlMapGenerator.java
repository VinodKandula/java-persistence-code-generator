/**
 * 
 */
package in.sivalabs.jpcg.mybatis.generators;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.sivalabs.jpcg.core.ArtifactGenerator;
import in.sivalabs.jpcg.core.DatabaseMetadataProvider;
import in.sivalabs.jpcg.core.configuration.Configuration;
import in.sivalabs.jpcg.core.configuration.TableConfig;
import in.sivalabs.jpcg.core.metadata.ColumnMetadata;
import in.sivalabs.jpcg.core.metadata.DatabaseMetadata;
import in.sivalabs.jpcg.core.metadata.TableMetadata;
import in.sivalabs.jpcg.core.utils.Utilities;

/**
 * @author Siva
 *
 */
public class MyBatisSqlMapGenerator implements ArtifactGenerator 
{

	private Configuration configuration;
	private MyBatisConfiguration mybatisConfiguration;
	private DatabaseMetadata dbMetadata;
	
	public MyBatisSqlMapGenerator(Configuration config) {
		initialize(config);
	}

	private void initialize(Configuration config)
	{
		try {
			this.configuration = config;
			this.mybatisConfiguration = new MyBatisConfiguration(config);
			Connection connection = this.configuration.getConnection();
			this.dbMetadata = DatabaseMetadataProvider.getDatabaseMetadata(connection);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void generate()
	{
		try
		{
			generateSqlMapXmls(configuration);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void generateSqlMapXmls(Configuration configuration) throws Exception {
		Set<String> tablesConfigured = new HashSet<>();
		List<TableConfig> tablesConfig = this.configuration.getTables();
		for (TableConfig tableConfig : tablesConfig)
		{
			tablesConfigured.add(tableConfig.getName());
		}
		
		List<TableMetadata> tables = dbMetadata.getTables();
		for (TableMetadata tableMD : tables) {
			if(tablesConfigured.contains(tableMD.getName().toLowerCase())){
				System.out.println("=========== Generating SqlMaps for Table : "+tableMD.getName()+" ====================");
				String mapperXml = this.generateSqlMapXmls(tableMD);
				//System.out.println(mapperXml);
				this.writeToDisk(tableMD, mapperXml);
			}
		}
	}
	
	private void writeToDisk(TableMetadata tableMD, String mapperXml) {
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		
		String namespace = tableConfig.getSingularClassName()+"Mapper"; 
		String targetDir = configuration.getTargetDir();

		Utilities.writeFile(targetDir+"/"+"mappers/"+namespace+".xml", mapperXml);
	}
	
	String generateSqlMapXmls(TableMetadata tableMD) throws Exception {
		
		String sqlMapperTemplate = Utilities.classpathFileToString("SqlMapper.xml.tmpl");
		Map<String, String> placeHolders = this.buildPlaceHolders(tableMD);
		Set<String> keys = placeHolders.keySet();
		
		String mapperXml = sqlMapperTemplate;
				
		for (String key : keys) {
			mapperXml = mapperXml.replace(key, placeHolders.get(key));
		}
		
		return mapperXml;
	}
	
	private Map<String, String> buildPlaceHolders(TableMetadata tableMD) throws Exception
	{
		Map<String, String>  map = new HashMap<String, String>();
		TableConfig tableConfig = configuration.getTableConfig(tableMD.getName());
		List<ColumnMetadata> pkColumns = tableMD.getPkColumns();
		
		String namespace = mybatisConfiguration.getJavaMappersPackage()+"."+tableConfig.getSingularClassName()+"Mapper"; 
		String where_by_pk = this.generateWhereClause(tableMD, pkColumns);
		
		String columnUpdateList = "";
		String columnList = "";
		String valuesList = "";
				
		List<ColumnMetadata> columns = tableMD.getColumns();

		for (ColumnMetadata column : columns) {
			columnList += ","+column.getName();
			valuesList += ","+"#{"+Utilities.convertUnderscoreNameToPropertyName(column.getName())+"}";
			columnUpdateList += ","+column.getName()+"="+"#{"+Utilities.convertUnderscoreNameToPropertyName(column.getName())+"}";
			
		}
		
		String columnToPropertyMappings = this.generateColumnToPropertyMappings(tableMD, pkColumns);
		
		map.put("#namespace#", namespace);
		
		map.put("#TableName#", tableMD.getName());
		map.put("#TableSingular#", tableConfig.getSingularClassName());
		map.put("#TablePlural#", tableConfig.getPluralClassName());
		map.put("#where_by_pk#", where_by_pk);;
		map.put("#update_columns_list#", columnUpdateList.substring(1));
		map.put("#commaSeparatedColumnList#", columnList.substring(1));
		map.put("#commaSeparatedValueList#", valuesList.substring(1));
		map.put("#columnToPropertyMappings#", columnToPropertyMappings);
		
		
		return map;
	}
	
	private String generateColumnToPropertyMappings(TableMetadata tableMD, List<ColumnMetadata> pkColumns) {
		String str = "";
		
		List<ColumnMetadata> columns = tableMD.getColumns();

		for (ColumnMetadata columnMD : columns)
		{
			String type = pkColumns.contains(columnMD.getName()) ? "id" : "result";
			str += "\t<"+type+" column=\""+columnMD.getName()+"\" "
					+ " property=\""+Utilities.convertUnderscoreNameToPropertyName(columnMD.getName())+"\"  /> \n\t ";
		}
		return str;
	}

	String generateWhereClause(TableMetadata table, List<ColumnMetadata> pkColumns) throws Exception {
		String where = "";
		for (ColumnMetadata column : pkColumns) {
			where += " and "+column.getName()+"="+ "#{"+Utilities.convertUnderscoreNameToPropertyName(column.getName())+"}";
		}
		if(where.length() > 0){
			return "where " + where.substring(" and ".length());
		}
		
		return "";
		
	}
	
	
}
