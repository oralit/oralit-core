package com.github.oralit.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.oralit.jdbc.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.OracleConnection;
/**
 * References https://docs.oracle.com/cd/B28359_01/server.111/b28320/dynviews_3027.htm#REFRN30232
 * @author jjs
 * https://docs.oracle.com/cd/B28359_01/server.111/b28310/diag006.htm#ADMIN12484
 *
 */
public class OracleHelper {

	private final static Logger log = LoggerFactory.getLogger(OracleHelper.class);

	public static boolean isOracleConnection(Connection connection) {
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		try {
			return connection.isWrapperFor(OracleConnection.class);
		} catch (SQLException e) {
			log.error("Error when checking isOracleConnection: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void commitNoWait(Connection connection) {
		final String commitBatchNoWaitText = "commit work write batch nowait";
		try {

			PreparedStatement ps = connection.prepareStatement(commitBatchNoWaitText);
			ps.executeUpdate();

		} catch (SQLException e) {
			log.error("Cannot commit because: {}", e);
			throw new RuntimeException(e);
		}
	}

	

	
	

	
	

	
	public static Map<String,Object> getV$Session(Connection connection) throws SQLException {
		String sql  = 
				"select \n" + 
				"	s.SADDR,\n" + 
				"	s.SID,\n" + 
				"	s.SERIAL#,\n" + 
				"	s.AUDSID,\n" + 
				"	s.PADDR,\n" + 
				"	s.USER#,\n" + 
				"	s.USERNAME,\n" + 
				"	s.COMMAND,\n" + 
				"	s.OWNERID,\n" + 
				"	s.TADDR,\n" + 
				"	s.LOCKWAIT,\n" + 
				"	s.STATUS,\n" + 
				"	s.SERVER,\n" + 
				"	s.SCHEMA#,\n" + 
				"	s.SCHEMANAME,\n" + 
				"	s.OSUSER,\n" + 
				"	s.PROCESS,\n" + 
				"	s.MACHINE,\n" + 
				"	s.PORT,\n" + 
				"	s.TERMINAL,\n" + 
				"	s.PROGRAM,\n" + 
				"	s.TYPE,\n" + 
				"	s.SQL_ADDRESS,\n" + 
				"	s.SQL_HASH_VALUE,\n" + 
				"	s.SQL_ID,\n" + 
				"	s.SQL_CHILD_NUMBER,\n" + 
				"	s.SQL_EXEC_START,\n" + 
				"	s.SQL_EXEC_ID,\n" + 
				"	s.PREV_SQL_ADDR,\n" + 
				"	s.PREV_HASH_VALUE,\n" + 
				"	s.PREV_SQL_ID,\n" + 
				"	s.PREV_CHILD_NUMBER,\n" + 
				"	s.PREV_EXEC_START,\n" + 
				"	s.PREV_EXEC_ID,\n" + 
				"	s.PLSQL_ENTRY_OBJECT_ID,\n" + 
				"	s.PLSQL_ENTRY_SUBPROGRAM_ID,\n" + 
				"	s.PLSQL_OBJECT_ID,\n" + 
				"	s.PLSQL_SUBPROGRAM_ID,\n" + 
				"	s.MODULE,\n" + 
				"	s.MODULE_HASH,\n" + 
				"	s.ACTION,\n" + 
				"	s.ACTION_HASH,\n" + 
				"	s.CLIENT_INFO,\n" + 
				"	s.FIXED_TABLE_SEQUENCE,\n" + 
				"	s.ROW_WAIT_OBJ#,\n" + 
				"	s.ROW_WAIT_FILE#,\n" + 
				"	s.ROW_WAIT_BLOCK#,\n" + 
				"	s.ROW_WAIT_ROW#,\n" + 
				"	s.TOP_LEVEL_CALL#,\n" + 
				"	s.LOGON_TIME,\n" + 
				"	s.LAST_CALL_ET,\n" + 
				"	s.PDML_ENABLED,\n" + 
				"	s.FAILOVER_TYPE,\n" + 
				"	s.FAILOVER_METHOD,\n" + 
				"	s.FAILED_OVER,\n" + 
				"	s.RESOURCE_CONSUMER_GROUP,\n" + 
				"	s.PDML_STATUS,\n" + 
				"	s.PDDL_STATUS,\n" + 
				"	s.PQ_STATUS,\n" + 
				"	s.CURRENT_QUEUE_DURATION,\n" + 
				"	s.CLIENT_IDENTIFIER,\n" + 
				"	s.BLOCKING_SESSION_STATUS,\n" + 
				"	s.BLOCKING_INSTANCE,\n" + 
				"	s.BLOCKING_SESSION,\n" + 
				"	s.FINAL_BLOCKING_SESSION_STATUS,\n" + 
				"	s.FINAL_BLOCKING_INSTANCE,\n" + 
				"	s.FINAL_BLOCKING_SESSION,\n" + 
				"	s.SEQ#,\n" + 
				"	s.EVENT#,\n" + 
				"	s.EVENT,\n" + 
				"	s.P1TEXT,\n" + 
				"	s.P1,\n" + 
				"	s.P1RAW,\n" + 
				"	s.P2TEXT,\n" + 
				"	s.P2,\n" + 
				"	s.P2RAW,\n" + 
				"	s.P3TEXT,\n" + 
				"	s.P3,\n" + 
				"	s.P3RAW,\n" + 
				"	s.WAIT_CLASS_ID,\n" + 
				"	s.WAIT_CLASS#,\n" + 
				"	s.WAIT_CLASS,\n" + 
				"	s.WAIT_TIME,\n" + 
				"	s.SECONDS_IN_WAIT,\n" + 
				"	s.STATE,\n" + 
				"	s.WAIT_TIME_MICRO,\n" + 
				"	s.TIME_REMAINING_MICRO,\n" + 
				"	s.TIME_SINCE_LAST_WAIT_MICRO,\n" + 
				"	s.SERVICE_NAME,\n" + 
				"	s.SQL_TRACE,\n" + 
				"	s.SQL_TRACE_WAITS,\n" + 
				"	s.SQL_TRACE_BINDS,\n" + 
				"	s.SQL_TRACE_PLAN_STATS,\n" + 
				"	s.SESSION_EDITION_ID,\n" + 
				"	s.CREATOR_ADDR,\n" + 
				"	s.CREATOR_SERIAL#,\n" + 
				"	s.ECID,\n" + 
				"	s.SQL_TRANSLATION_PROFILE_ID,\n" + 
				"	s.PGA_TUNABLE_MEM,\n" + 
				"	s.SHARD_DDL_STATUS,\n" + 
				"	s.CON_ID,\n" + 
				"	s.EXTERNAL_NAME,\n" + 
				"	s.PLSQL_DEBUGGER_CONNECTED,\n" + 
				"	p.ADDR,\n" + 
				"	p.PID,\n" + 
				"	p.SOSID,\n" + 
				"	p.SPID,\n" + 
				"	p.STID,\n" + 
				"	p.EXECUTION_TYPE,\n" + 
				"	p.PNAME,\n" + 
				"	p.USERNAME p_username,\n" + 
				"	p.SERIAL# p_serial#,\n" + 
				"	p.TERMINAL p_terminal,\n" + 
				"	p.PROGRAM p_program,\n" + 
				"	p.TRACEID,\n" + 
				"	p.TRACEFILE,\n" + 
				"	p.BACKGROUND,\n" + 
				"	p.LATCHWAIT,\n" + 
				"	p.LATCHSPIN,\n" + 
				"	p.PGA_USED_MEM,\n" + 
				"	p.PGA_ALLOC_MEM,\n" + 
				"	p.PGA_FREEABLE_MEM,\n" + 
				"	p.PGA_MAX_MEM,\n" + 
				"	p.NUMA_DEFAULT,\n" + 
				"	p.NUMA_CURR,\n" + 
				"	p.CON_ID\n" + 
				"from    v$session s, \n" + 
				"	v$process p \n" + 
				"where   s.audsid=userenv('sessionid') and \n" + 
				"	p.addr = s.paddr";
		
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rset = ps.executeQuery();
		List<LinkedHashMap<String,Object>> rows = ResultSetHelper.toListOfMaps(rset);
		if (rows.size() != 1) {
			throw new IllegalStateException("rows not 1 is " + rows.size());
		}
		return rows.get(0);
		
	}
	
	
	
	public static void setClientInfo(Connection connection, final String info) {
		if (isOracleConnection(connection)) {

			String text = info;
			if (text.length() > 32) {
				text = info.substring(0, 31);
			}

			final String sql = "{call dbms_application_info.set_client_info(:txt)}";
			try (CallableStatement sqlClientInfo = connection.prepareCall(sql)) {
				sqlClientInfo.setString(1, text);
				sqlClientInfo.executeUpdate();
			} catch (final SQLException sqe) {
				throw new RuntimeException(sqe);
			}
		} else {
			log.info("not oracle connection, nothing done");
		}
	}

	

	

	
	
	
	
}
