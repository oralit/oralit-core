package com.github.oralit.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.github.oralit.text.SimpleDateFormatFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractOracleStats {

	protected Connection connection;
	protected static final SimpleDateFormat dateFormat = SimpleDateFormatFactory.getDateTimeForFileName();
	private Logger log = LoggerFactory.getLogger(getClass());
	public AbstractOracleStats(Connection conn) {
		super();
		this.connection = conn;
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		if (!OracleHelper.isOracleConnection(connection)) {
			throw new IllegalArgumentException(
					"Is not an OracleConnection " + connection.getClass().getCanonicalName());
		}
	}

	public void setAction(final String action) {
	
		String text = "{call dbms_application_info.set_action(?)}";
		try (CallableStatement sqlAction = connection.prepareCall(text)) {
			sqlAction.setString(1, action);
			sqlAction.execute();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	
	}

	/**
	 * Registering the application allows system administrators and performance
	 * tuning specialists to track performance by module. System administrators can
	 * also use this information to track resource use by module. When an
	 * application registers with the database, its name and actions are recorded in
	 * the V$SESSION and V$SQLAREA views.
	 * 
	 * These can be viewed in V$SESSION
	 * 
	 * 
	 * @param connection
	 * @param module
	 * @param action
	 */
	public void setModule(final String module, final String action) {
	
		String text = module;
		if (text.length() > 48) {
			final int end = text.length() - 1;
			final int begin = end - 48;
			text = module.substring(begin, end);
			log.warn("module trimmed to: '" + text + "' from '" + module);
		}
		final String sqlString = "{call dbms_application_info.set_module(?,?)}";
		try (CallableStatement sqlModule = connection.prepareCall(sqlString)) {
			sqlModule.setString(1, text);
			sqlModule.setString(2, action);
			sqlModule.execute();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setClientInfo(final String info) {
	
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
	}

	public void setClientIdentifier(final String info) {
	
		String text = info;
		if (text.length() > 32) {
			text = info.substring(0, 31);
		}
		final String sql = "{call dbms_session.set_identifier(:txt)}";
		try (CallableStatement sqlClientInfo = connection.prepareCall(sql)) {
			sqlClientInfo.setString(1, text);
			sqlClientInfo.executeUpdate();
		} catch (final SQLException sqe) {
			throw new RuntimeException(sqe);
		}
	}

	public String getTraceFileName() {
		String query = "SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File'";
		try (ResultSet rset = connection.createStatement().executeQuery(query)) {
			rset.next();
			return rset.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	
	}

}