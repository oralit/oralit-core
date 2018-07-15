package org.javautil.oralit.dbstats.oracle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.javautil.oralit.oracle.AbstractOracleStats;
import org.javautil.oralit.oracle.OracleStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TkprofPipeListener 
extends Thread 
{
	private static final Logger logger = LoggerFactory.getLogger(TkprofPipeListener.class.getName());
	int callRc;
	String traceFileName = null;
	String args = null;
	String returnPipe = null;
	
	private AbstractOracleStats oracleStats;
	
	boolean waiting = true;
	boolean ok = true;

    // TODO externalize
	
	// TODO put in package an allow use and explain why privileges through package but not for user
	// allows multiple users
	
	private String listen = 
			"declare\n" + 
			"    pipe_rc number := 0;\n" + 
			"    trace_file_name varchar2(255);\n" + 
			"    return_pipe_name varchar2(80);\n" + 
			"begin\n" + 
			"     pipe_rc := dbms_pipe.receive_message('tkprof',60);\n" + 
			"     IF ( pipe_rc = 0 ) THEN\n" + 
			"          dbms_pipe.unpack_message(trace_file_name) ;\n" + 
			"          dbms_pipe.unpack_message(return_pipe_name) ;\n" + 
			"          dbms_output.put_line('trace_file_name: ' || trace_file_name);\n" + 
			"          dbms_output.put_line('return_pipe_name: ' || return_pipe_name);\n" + 
			"      ELSE\n" + 
			"          dbms_output.put_line('rc was ' || pipe_rc);\n" + 
			"      END IF ;\n"
			+ "    :pipe_rc := pipe_rc;\n"
			+ "    :trace_file_name := trace_file_name;\n"
			+ "    :return_pipe_name := return_pipe_name;\n" + 
			" end;\n" + 
			"";

	private static final String reply = 
	" begin \n " +                                   //
	"     dbms_pipe.pack_message(:pipe_payload);\n" +            //
	"     :pipe_rc := dbms_pipe.send_message('tkprof');\n" +              //
	" end; \n ";                                     //
	private static final String requestPipeName = "tkprof";
	private CallableStatement getStmt = null;
	private CallableStatement replyStmt = null;
	private Connection connection;



	public TkprofPipeListener(final Connection conn)  throws SQLException {
		this.connection = conn;
		getStmt = connection.prepareCall(listen);
		getStmt.registerOutParameter("pipe_rc", java.sql.Types.NUMERIC);
		getStmt.registerOutParameter("trace_file_name", java.sql.Types.VARCHAR);
		getStmt.registerOutParameter("return_pipe_name", java.sql.Types.VARCHAR);
		
		replyStmt = connection.prepareCall(reply);
		replyStmt.registerOutParameter("pipe_rc",java.sql.Types.NUMERIC);
		oracleStats = new OracleStats(conn);
	}
	
	void getRequest() throws SQLException {
		while (waiting && ok) {
			oracleStats.setModule("tkprof service", "waiting on pipe '" + requestPipeName + "'");
			getStmt.executeUpdate();
		
			traceFileName = getStmt.getString("trace_file_name");
			returnPipe = getStmt.getString("return_pipe_name");
			callRc = getStmt.getInt("pipe_rc");
	
			switch (callRc) {
			case 0:
				waiting = false;
				logger.info("read on pipe returned 0");
				break;
			case 1: // timeOut, no request within 60, seconds
				logger.info("no request received in 60 seconds on pipe '" + requestPipeName + "'");
				break;
			default:
				throw new IllegalStateException("unknown return value from read pipe " + callRc);
			}
		}
	}
	public static List<String> analyzeTrace(String fileName) throws IOException {
		return analyzeTrace(new File(fileName));
	}
	
	public static List<String> analyzeTrace(File traceFile) throws IOException {
		// TODO create temporary prf and delete when returned.
		   List<String> lines = null;
		String outfile = "/tmp/trace.prf";
		String tkProf = "/common/oracle/product/12.2.0/dbhome_1/bin/tkprof";
		ProcessBuilder pb = new ProcessBuilder(tkProf, traceFile.getCanonicalPath(), "/tmp/trace.prf");
		 Process p = pb.start();
		  Charset charset = Charset.forName("ISO-8859-1");
		  Path traceFilePath = Paths.get(outfile);
		    try {
		     lines = Files.readAllLines(traceFilePath, charset);

		      for (String line : lines) {
		        System.out.println(line);
	
		      }
		    } catch (IOException e) {
		      System.out.println(e);
		    }
		logger.info("leaving analyze trace");
		return lines;
	}
	List<String> analyze() throws IOException {
		oracleStats.setAction("running TKProf");
		logger.info("about to analyze " + traceFileName);
		List<String> lines  = analyzeTrace(traceFileName);
		return lines;
	}
	
	void reply(List<String> lines) throws SQLException {
		logger.info("replying with " + lines);
        if (lines != null && lines.size() > 0) {
			replyStmt.setString("pipe_payload", lines.get(0));
			replyStmt.executeUpdate();
			
        }
        logger.info("reply sent");
	}

	void processRequest() throws SQLException, IOException {
		oracleStats.setModule(getClass().getSimpleName(), "");
		getRequest();
		List<String> lines = analyze();
		reply(lines);
	}

	/**
	 * Listens for requests on pipeName specified in constructor.
	 * 
	 * Expects that messages written to the specified pipe are formatted by the
	 * javautil provided Oracle plsql package service_rqst.
	 * 
	 * Loops on listening for 60 seconds. The 60 second timeout is not configurable
	 * as its only purpose is to prevent this from blocking on a ready making the
	 * session non killable from Oracle.
	 *
	 * @return ServiceRequestPipe
	 * @exception SQLException
	 * @throws IOException
	 * 
	 * TODO this sits in the SGA if a lot passes through the SGA it takes up 
	 * a lot of space, should expose as a REST service perhaps authenticate 
	 * through the dbms_pipe
	 */
	public void run()  {
		logger.info("running");
        int errorCount = 0;
		while (errorCount <= 100) {
			try {
				processRequest();
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				errorCount++;
				e.printStackTrace();
				logger.error("hmm", e);
			}
		}
		
	}
}
