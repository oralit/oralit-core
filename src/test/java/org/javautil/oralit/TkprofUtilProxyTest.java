package org.javautil.oralit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.oralit.dbstats.services.oracle.TkprofUtilProxy;
import org.javautil.oralit.instrumentation.DbStats;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TkprofUtilProxyTest 
{
/**
 * ORA-06559: wrong datatype requested, string, actual datatype is string 

Cause: The sender put different datatype on the pipe than that being requested (package dbms_pipe ). 
The numbers are: 6 - number, 9 - char, 12 - date. 

purging
https://docs.oracle.com/database/121/ARPLS/d_pipe.htm#ARPLS67417
 */
	private DataSource dataSource = DsLookup.getInstance().getDataSource();
	private Logger logger = LoggerFactory.getLogger(getClass());

	DbStats dbstats;
	Connection conn;
	@Before
	public void setup() throws SQLException {
		conn = dataSource.getConnection();
	}
	
	// TODO put in test resources
	String getTraceFileName() {
		return "/common/oracle/product/12.2.0/dbhome_1/rdbms/log/dev12c_ora_10294.trc";
	}
	
	@Test
	public void testIt() throws SQLException, IOException, URISyntaxException {
		Connection conn = dataSource.getConnection();
		TkprofUtilProxy proxy = new TkprofUtilProxy(conn);
		String result = proxy.getAnalyzedTrace(getTraceFileName());
		logger.info(result);
	}
	
	

	
}
