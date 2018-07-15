package org.javautil.oralit.dbstats.services.oracle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.javautil.oralit.text.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extremely low overhead minimal implementation that requires the database server
 * on the local computer and the user to be a DBA.  Not uncommon in development.
 * @author jjs
 *
 */
public class TkprofUtilLocal implements TkprofUtil {
	
	private static Logger logger = LoggerFactory.getLogger(TkprofUtilLocal.class);

	
	public static List<String> analyzeTrace(String fileName) throws IOException {
		return analyzeTrace(new File(fileName));
	}
	
	/* (non-Javadoc)
	 * @see org.javautil.dbstats.services.oracle.TkprofUtil#getAnalyzedTrace(java.lang.String)
	 */
	@Override
	public String getAnalyzedTrace(final String traceFileName) throws IOException {
		File tempOut  = File.createTempFile("/tmp/tkprofs", ".text");
		logger.info("creating " + tempOut.getCanonicalPath());
		getOutputFileName(traceFileName,tempOut.getCanonicalPath());
		return FileUtil.getAsString(tempOut.getCanonicalPath());
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
