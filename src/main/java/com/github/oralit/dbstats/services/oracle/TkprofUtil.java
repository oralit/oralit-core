package com.github.oralit.dbstats.services.oracle;

import java.io.IOException;

public interface TkprofUtil {

	String getAnalyzedTrace(String traceFileName) throws IOException;

}