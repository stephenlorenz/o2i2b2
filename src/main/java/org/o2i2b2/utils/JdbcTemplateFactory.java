package org.o2i2b2.utils;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateFactory {

	private JdbcTemplate jdbcTemplate;
	
	private String databaseType;
	private String host;
	private String port;
	private String database;
	private String username;
	private String password;
	
	public JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(getDriverClassName());
			dataSource.setUrl(getUrl());
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setPoolPreparedStatements(true);
			dataSource.setMaxActive(10);
			dataSource.setMaxIdle(2);
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		return jdbcTemplate;
	}
	
	
	public JdbcTemplateFactory(String databaseType, String host, String port,
			String database, String username, String password) {
		super();
		this.databaseType = databaseType;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public JdbcTemplate getJdbcTemplate(String databaseType, String host, String port,
			String database, String username, String password) {
		this.databaseType = databaseType;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		return getJdbcTemplate();
	}

	public boolean testConnection() {
		try {
			return getJdbcTemplate().getDataSource().getConnection() != null ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String getDriverClassName() {
		if (databaseType != null) {
			if (databaseType.equalsIgnoreCase("oracle")) {
				return "oracle.jdbc.OracleDriver";
			} else if (databaseType.equalsIgnoreCase("Microsoft SQL Server")) {
				return "net.sourceforge.jtds.jdbc.Driver";
			}
		}
		return null;
	}
	
	private String getUrl() {
		if (host != null && port != null && database != null) {
			if (databaseType.equalsIgnoreCase("oracle")) {
				return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, database);
			} else if (databaseType.equalsIgnoreCase("Microsoft SQL Server")) {
				return String.format("jdbc:jtds:sqlserver://%s:%s/%s", host, port, database);
			}
		}
		return null;
	}
	
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
