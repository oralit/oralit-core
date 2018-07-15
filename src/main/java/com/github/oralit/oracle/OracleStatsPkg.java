package com.github.oralit.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.oralit.instrumentation.DbStats;
import com.github.oralit.text.SimpleDateFormatFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Implementation of DbStats for Oracle that uses the oracle_stats package
 * so the user does not have to be granted alter session privileges.	
 *
 */
public class OracleStatsPkg extends AbstractOracleStats implements DbStats {

	private final static Logger log = LoggerFactory.getLogger(OracleStatsPkg.class);
	private Connection connection;
	private SimpleDateFormat dateFormat = SimpleDateFormatFactory.getDateTimeForFileName();

	public OracleStatsPkg(Connection connection) {
		super(connection);
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		if (!OracleHelper.isOracleConnection(connection)) {
			throw new IllegalArgumentException(
					"Is not an OracleConnection " + connection.getClass().getCanonicalName());
		}
		this.connection = connection;
	}

	// TODO need trace suspend and trace resume
	/**
	 * Turns on detailed oracle tracing. stmt.execute("alter session set
	 * timed_statistics = true"); stmt.execute("alter session set max_dump_file_size
	 * = unlimited"); stmt.execute("alter session set sql_trace = true");
	 * stmt.execute("alter session set events '10046 trace name context forever,
	 * level 12'"); setTraceFileIdentifier(connection, fileId);
	 *
	 * @param fileId
	 *            the name of the trace file to use
	 * @return
	 * @throws SQLException
	 */
	public boolean traceOn(final String fileId) {
		boolean success = true;

		try (final Statement stmt = connection.createStatement()) {
			stmt.execute("alter session set timed_statistics = true");
			stmt.execute("alter session set max_dump_file_size = unlimited");
			stmt.execute("alter session set sql_trace = true");
			stmt.execute("alter session set events '10046 trace name context forever, level 12'");
			setTraceFileIdentifier(fileId + "_" + dateFormat.format(new Date()));
		} catch (final SQLException e) {
			if (e.getErrorCode() == 1031) {
				success = false;
				log.warn("no alter session permission 1031" + e.getMessage());
			} else if (e.getErrorCode() == -1031) {
				success = false;
				log.warn("no alter session -1031 " + e.getMessage());
			} else {
				log.error("Error when trace-on: {}", e);
				throw new RuntimeException(e);
			}
		}

		return success;
	}

	public boolean traceOff() {
		boolean success = true;

		try (final Statement stmt = connection.createStatement()) {
			//stmt.execute("alter session set timed_statistics = false");
			stmt.execute("alter session set sql_trace = false");
		} catch (final SQLException e) {
			if (e.getErrorCode() == 1031) {
				success = false;
				log.warn("no alter session permission 1031" + e.getMessage());
			} else if (e.getErrorCode() == -1031) {
				success = false;
				log.warn("no alter session -1031 " + e.getMessage());
			} else {
				log.error("Error when trace-off: {}", e);
				throw new RuntimeException(e);
			}
		}

		return success;
	}

	public static SessionInfo getSessionInfo(Connection connection) {
		SessionInfo sessionInfo = null;

		final String text = "select s.sid, s.serial#, p.spid, p.pid from v$session s, v$process p "
				+ " where s.audsid=userenv('sessionid') and p.addr = s.paddr";

		try (final Statement stmt = connection.createStatement()) {
			final ResultSet rset = stmt.executeQuery(text);
			rset.next();

			int sid = rset.getInt("sid");
			int serial = rset.getInt("serial#");
			int spid = rset.getInt("spid");
			int pid = rset.getInt("pid");

			sessionInfo = new SessionInfo(sid, serial, spid, pid);
			log.debug("sid " + sid + " serial " + serial + " spid " + spid + " pid " + pid);

		} catch (final SQLException e) {
			log.error("Error when getSessionInfo() because: {}", e.getMessage());
		}

		return sessionInfo;

	}

	
	

	public void setTraceFileIdentifier(final String id) {

		final String text = "alter session set tracefile_identifier = '" + id + "'";
		try (final PreparedStatement stmt = connection.prepareStatement(text)) {
			stmt.executeUpdate();
		} catch (final SQLException s) {
			log.error("Error when setTraceFileIdentifier with id: {}, because: {}", id, s.getMessage());
		}

	}

//	public String getTraceFileName() {
//		String query = "SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File'";
//		try (ResultSet rset = connection.createStatement().executeQuery(query)) {
//			rset.next();
//			return rset.getString(1);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//
//	}

	
}
