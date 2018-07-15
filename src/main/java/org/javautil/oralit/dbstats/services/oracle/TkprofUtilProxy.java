package org.javautil.oralit.dbstats.services.oracle;



import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.javautil.oralit.oracle.OracleHelper;
import org.javautil.oralit.text.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extremely low overhead minimal implementation that requires the database server
 * on the local computer and the user to be a DBA.  Not uncommon in development.
 * @author jjs
 *
 */
public class TkprofUtilProxy implements TkprofUtil {
	
	private static Logger logger = LoggerFactory.getLogger(TkprofUtilProxy.class);

	
	public static List<String> analyzeTrace(String fileName) throws IOException {
		return analyzeTrace(new File(fileName));
	}

	private String pushText;
	
	private CallableStatement pushStatement;

	private Connection conn;
	
	public TkprofUtilProxy(Connection conn) throws IOException, URISyntaxException {
	
		if (!OracleHelper.isOracleConnection(conn)) {
			throw new IllegalArgumentException("is not Oracle Connection: " + conn);
		};
		init();
		this.conn = conn;
	}
	
	public void init() throws IOException, URISyntaxException {
		String resourceName = "/plsql/push_tkprof_request_to_pipe.sql";
		URI resourceURI = getClass().getResource(resourceName).toURI();
		logger.debug("resourceUri:" + resourceURI.toString());
		pushText = new String(Files.readAllBytes(Paths.get(getClass().getResource(resourceName).toURI())));
		logger.debug("push Text: " + pushText);
		
	}
	
	public String pushRequest(String traceFileName) throws SQLException {
		if (pushStatement == null) {
			logger.info("preparing");
			pushStatement = conn.prepareCall(pushText);
			logger.info("setting out parameter");
			//pushStatement.registerOutParameter("trace_info",java.sql.Types.VARCHAR);
			pushStatement.registerOutParameter(2,java.sql.Types.VARCHAR);
			pushStatement.registerOutParameter(3, java.sql.Types.NUMERIC);
			logger.info("prepared push");
		}
		logger.info("about to call");
	//	pushStatement.setString("trace_info", traceFileName);
		pushStatement.setString(1, traceFileName);
		pushStatement.execute();
		//String response = pushStatement.getString("trace_file_name");
		String response = pushStatement.getString(2);
		Integer pipeReturnCode  = pushStatement.getInt(3);
		if (pipeReturnCode != 0) {
			response = "probably a timeout pipe response " + pipeReturnCode;
		}
		return response;
	}
	
	/* (non-Javadoc)
	 * @see org.javautil.dbstats.services.oracle.TkprofUtil#getAnalyzedTrace(java.lang.String)
	 */
	@Override
	public String getAnalyzedTrace(final String traceFileName) throws IOException {
		String response;
		try {
			response = pushRequest(traceFileName);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return response;
//		File tempOut  = File.createTempFile("/tmp/tkprofs", ".text");
//		logger.info("creating " + tempOut.getCanonicalPath());
//		getOutputFileName(traceFileName,tempOut.getCanonicalPath());
//		return FileUtil.getAsString(tempOut.getCanonicalPath());
	}
	
	public static String  getOutputFileName(final String traceFileName, final String outfileName) throws IOException {
	
		String tkProf = "/common/oracle/product/12.2.0/dbhome_1/bin/tkprof";  // TODO should be configurable
		ProcessBuilder pb = new ProcessBuilder(tkProf, traceFileName, outfileName);
		Process p = pb.start();
		return outfileName;
	}


	
	public static List<String> analyzeTrace(File traceFile) throws IOException {
		 String outputFile = "/tmp/trace.prf";
		 getOutputFileName(traceFile.getCanonicalPath(),outputFile);
		 return(FileUtil.readAllLines(outputFile));
	
	}

}
