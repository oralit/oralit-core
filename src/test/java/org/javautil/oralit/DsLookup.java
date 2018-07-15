package org.javautil.oralit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DsLookup {

    private static final String DEFAULT_CFG = "db.properties";
    private static final Logger logger = LoggerFactory.getLogger(DsLookup.class);
    private static DsLookup instance;

    private DataSource dataSource;

    private DsLookup() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(DEFAULT_CFG)) {
            final Properties config = new Properties();
            config.load(is);

            final HikariConfig hikariConfig = new HikariConfig(config);
            this.dataSource = new HikariDataSource(hikariConfig);
        } catch (IOException e) {
            logger.error("Cannot load db.properties because: {}", e);
        }
    }

    public static DsLookup getInstance() {
        if (instance == null) {
            instance = new DsLookup();

        }
        return instance;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
