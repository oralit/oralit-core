package org.javautil.oralit.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResultSetHelper {
	public String[] getColumnNames(ResultSetMetaData meta) throws SQLException {
		int columnCount = meta.getColumnCount();
		String[] columnNames = new String[columnCount];
		for (int i = 1 ; i <= columnCount; i++) {
			columnNames[i] = meta.getColumnName(i);
		}
		return columnNames;
	}
	
	public static LinkedHashMap<String,Object> getRowAsMap(ResultSet rset) throws SQLException {
        final  ResultSetMetaData metaData = rset.getMetaData();
        final LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                final String columnName = metaData.getColumnName(i);
                final Object columnValue = rset.getObject(i);
                row.put(columnName, columnValue);
        }
        return row;
}


	public static List<LinkedHashMap<String,Object>> toListOfMaps(ResultSet rset) throws SQLException {
		 List<LinkedHashMap<String,Object>> list = new ArrayList<LinkedHashMap<String,Object>>();
		 while (rset.next()) {
			 list.add(getRowAsMap(rset));
		 }
		 return list;
	}
}
