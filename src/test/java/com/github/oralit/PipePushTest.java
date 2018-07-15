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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class PipePushTest  {
	String listener;
	String pushText = null;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Before
	public void before() throws IOException, URISyntaxException {
		String resourceName = "/plsql/push_tkprof_request_to_pipe.sql";
		URI resourceURI = getClass().getResource(resourceName).toURI();
		logger.debug("resourceUri:" + resourceURI.toString());
		pushText = new String(Files.readAllBytes(Paths.get(getClass().getResource(resourceName).toURI())));
		assertThat(pushText).isNotNull();
		logger.debug("push Text: " + pushText);
		
	}
	
	@Test
	public void nullTest() {
		
	}
}
