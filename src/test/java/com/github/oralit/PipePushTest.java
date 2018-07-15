package com.github.oralit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.sql.DataSource;

import org.javautil.dbstats.oracle.TkprofPipeListener;
import org.javautil.dbstats.services.oracle.TkprofUtil;
import org.javautil.dbstats.services.oracle.TkprofUtilLocal;
import org.javautil.dbstats.services.oracle.TkprofUtilProxy;
import org.javautil.oracle.HardwiredDataSource;
import org.javautil.oracle.OracleHelper;
import org.javautil.oracle.OracleStats;
import org.javautil.oracle.OracleStatsPkg;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipePushTest 
{
	String listener;
	String pushText = null;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Before
	public void before() throws IOException, URISyntaxException {
		String resourceName = "/plsql/push_tkprof_request_to_pipe.sql";
		URI resourceURI = getClass().getResource(resourceName).toURI();
		logger.debug("resourceUri:" + resourceURI.toString());
		pushText = new String(Files.readAllBytes(Paths.get(getClass().getResource(resourceName).toURI())));
		Assert.assertNotNull(pushText);
		logger.debug("push Text: " + pushText);
		
	}
	
	@Test
	public void nullTest() {
		
	}
}
