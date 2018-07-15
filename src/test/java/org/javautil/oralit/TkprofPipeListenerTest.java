package org.javautil.oralit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.javautil.oralit.dbstats.oracle.TkprofPipeListener;
import org.javautil.oralit.instrumentation.DbStats;
import org.javautil.oralit.oracle.*;
import org.javautil.oralit.oracle.OracleStats;
import org.javautil.oralit.oracle.OracleStatsPkg;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class TkprofPipeListenerTest {
    /**
     * ORA-06559: wrong datatype requested, string, actual datatype is string
     * <p>
     * Cause: The sender put different datatype on the pipe than that being requested (package dbms_pipe ).
     * The numbers are: 6 - number, 9 - char, 12 - date.
     * <p>
     * purging
     * https://docs.oracle.com/database/121/ARPLS/d_pipe.htm#ARPLS67417
     */
    private DataSource dataSource = DsLookup.getInstance().getDataSource();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private TkprofPipeListener tkProfListener;
    DbStats dbstats;
    Connection conn;

    @Before
    public void setup() throws SQLException {
        startTkprofService();
        conn = dataSource.getConnection();
    }

    public void after() {
        tkProfListener.interrupt();
    }

    private void startTkprofService() throws SQLException {
        Connection conn = dataSource.getConnection();
        assertThat(OracleHelper.isOracleConnection(conn)).isTrue();
        TkprofPipeListener tkProfListener = new TkprofPipeListener(conn);
        tkProfListener.start();
    }

    private int exhaustQuery(Connection conn, String sql) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet rset = s.executeQuery(sql);
        int rowcount = 0;
        while (rset.next()) {
            rowcount++;
        }
        s.close();
        return rowcount;
    }

    @Test
    public void testIt() throws SQLException {
        dbstats = new OracleStatsPkg(conn);
        showTracing();
        dbstats = new OracleStats(conn);
        showTracing();
    }

    public void showTracing() throws SQLException {
        // TODO figure out how to universally have a method
        String method = "instrumentation.integrationTest";
        assertThat(method.length()).isLessThanOrEqualTo(40);
        dbstats.traceOn("method");
        dbstats.setAction("some long queries");

        exhaustQuery(conn, "select t.*, c.* from user_tables t, user_tab_columns c");

        String fileName = dbstats.getTraceFileName();
        // this will only work for dbas with the database on this box
        File traceFile = new File(fileName);
        assertThat(traceFile.exists()).isTrue();
        logger.info("traceFile: " + traceFile);

        // now nothing more should be written to the file
        dbstats.traceOff();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // give a chance to flush
        logger.info("traceOff complete size is: " + traceFile.length());
        long filesize = traceFile.length();
        exhaustQuery(conn, "select 'x' from dual");
        logger.info("after select x  complete size is: " + traceFile.length());
        long newFileSize = traceFile.length();
        //assertEquals(filesize, newFileSize);
        logger.info("file size unchanged " + newFileSize);
        exhaustQuery(conn, "select t.*, c.* from user_tables t, user_tab_columns c");
        logger.info("file after sel t c " + newFileSize);
        // TODO figure out when the file stops growing
        // Now we turn tracing on
        dbstats.traceOn("2 method ");
        String fileName2 = dbstats.getTraceFileName();
        assertThat(traceFile.exists()).isTrue();
        // now its the same trace file, not surprising
        // TODO check if any writing occurs while trace is off
        logger.info("traceFile2: " + fileName2);
        //
        // get the tkprof  output

    }
}
