/**
 * 
 */
package in.sivalabs.jpcg.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.sivalabs.jpcg.core.metadata.ColumnMetadata;
import in.sivalabs.jpcg.core.metadata.DatabaseMetadata;
import in.sivalabs.jpcg.core.metadata.TableMetadata;

/**
 * @author Siva
 *
 */
public class DatabaseMetadataProvider 
{
	public static DatabaseMetadata getDatabaseMetadata(Connection conn) 
	{
		try {
			DatabaseMetadata db = new DatabaseMetadata();
			db.setName(conn.getCatalog());
			
			List<TableMetadata> tables = new ArrayList<>();
			List<String> tableNames = getTableNames(conn);
			for (String tbl : tableNames) {
				TableMetadata table = new TableMetadata();
				table.setName(tbl);
				
				List<ColumnMetadata> columns = getColumnsMetadata(conn, tbl);
				table.setColumns(columns);
				
				List<ColumnMetadata> pkColumns = getPrimaryKeyColumnsForTable(conn, tbl);
				table.setPkColumns(pkColumns);
				
				tables.add(table);
			}
			db.setTables(tables);
			return db;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<ColumnMetadata> getPrimaryKeyColumnsForTable(Connection conn, String tableName)
			throws SQLException {
		List<ColumnMetadata> pkColumnSet = new ArrayList<>();
		try {
			ResultSet rsColumns = conn.getMetaData().getPrimaryKeys(null, null, tableName);
			while (rsColumns.next()) {
				
				
				
			    String columnName = rsColumns.getString("COLUMN_NAME");
			    ColumnMetadata column =  getColumnMetadata(conn, tableName, columnName);
			   			    
			    pkColumnSet.add(column);
			    
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return pkColumnSet;
	}
	
	public static ColumnMetadata getColumnMetadata(Connection conn, String tableName, String columnName)  throws SQLException
	{

	    ResultSet rsColumns = null;
	    DatabaseMetaData meta = conn.getMetaData();
	    rsColumns = meta.getColumns(null, "%", tableName, columnName);
	    
	    ColumnMetadata column = new ColumnMetadata();
	    if(rsColumns.next()) {
		    String columnType = rsColumns.getString("TYPE_NAME");
		    String columnNameStr = rsColumns.getString("COLUMN_NAME");
		    int size = rsColumns.getInt("COLUMN_SIZE");
		    int nullable = rsColumns.getInt("NULLABLE");
		    int position = rsColumns.getInt("ORDINAL_POSITION");
	
		    column.setName(columnNameStr);
		    column.setType(columnType);
		    column.setNullable(nullable == DatabaseMetaData.columnNullable);
		    column.setPosition(position);
		    column.setSize(size);
	    }

	    return column;
	  }
	
	public static List<ColumnMetadata> getColumnsMetadata(Connection conn, String tableName)  throws SQLException
	{
		List<ColumnMetadata> columns = new ArrayList<>();
		
		ResultSet rsColumns = null;
	    DatabaseMetaData meta = conn.getMetaData();
	    rsColumns = meta.getColumns(null, null, tableName, null);
	    
	    while (rsColumns.next()) 
	    {
	    	ColumnMetadata column = new ColumnMetadata();
		    String columnType = rsColumns.getString("TYPE_NAME");
		    String columnName = rsColumns.getString("COLUMN_NAME");
		    int size = rsColumns.getInt("COLUMN_SIZE");
		    int nullable = rsColumns.getInt("NULLABLE");
		    int position = rsColumns.getInt("ORDINAL_POSITION");
	
		    column.setName(columnName);
		    column.setType(columnType);
		    column.setNullable(nullable == DatabaseMetaData.columnNullable);
		    column.setPosition(position);
		    column.setSize(size);
		    columns.add(column);
	      
	    }
	    return columns;
	}

	public static List<String> getTableNames(Connection conn) throws SQLException
	{
		List<String> names = new ArrayList<>();
		ResultSet rs = null;
		DatabaseMetaData meta = conn.getMetaData();
		rs = meta.getTables(null, null, null, new String[] { "TABLE" });

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			names.add(tableName);
		}
		return names;
	}
	
	public static String getJavaType(String jdbcType)
	{
		return getJdbcToJavaTypeMap().containsKey(jdbcType) ? getJdbcToJavaTypeMap().get(jdbcType) : "String";
	}
	
	public static Map<String, String> getJdbcToJavaTypeMap()
	{
		Map<String, String> jdbcToJavaMap = new HashMap<>();
		jdbcToJavaMap.put("CHAR","String");
		jdbcToJavaMap.put("VARCHAR","String");
		jdbcToJavaMap.put("LONGVARCHAR","String");
		jdbcToJavaMap.put("NUMERIC","java.math.BigDecimal");
		jdbcToJavaMap.put("DECIMAL","java.math.BigDecimal");
		jdbcToJavaMap.put("BIT","boolean");
		jdbcToJavaMap.put("TINYINT","byte");
		jdbcToJavaMap.put("SMALLINT","short");
		jdbcToJavaMap.put("INTEGER","int");
		jdbcToJavaMap.put("INT","Long");
		jdbcToJavaMap.put("BIGINT","Long");
		jdbcToJavaMap.put("REAL","float");
		jdbcToJavaMap.put("FLOAT","double");
		jdbcToJavaMap.put("DOUBLE","double");
		jdbcToJavaMap.put("BINARY","byte[]");
		jdbcToJavaMap.put("VARBINARY","byte[]");
		jdbcToJavaMap.put("LONGVARBINARY","byte[]");
		jdbcToJavaMap.put("DATE","java.util.Date");
		jdbcToJavaMap.put("DATETIME","java.util.Date");
		jdbcToJavaMap.put("TIME","java.sql.Time");
		jdbcToJavaMap.put("TIMESTAMP","java.sql.Timestamp");

		return jdbcToJavaMap;
	}

}
