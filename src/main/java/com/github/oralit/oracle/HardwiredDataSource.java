package com.github.oralit.oracle;



import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HardwiredDataSource {

		public static HikariDataSource getDataSource() {

			HikariConfig config = new HikariConfig();
			HikariDataSource ds;
			config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/sales_reporting_pdb");
			config.setUsername("sa");
			config.setPassword("tutorial");
			return new HikariDataSource(config);

		}
}
