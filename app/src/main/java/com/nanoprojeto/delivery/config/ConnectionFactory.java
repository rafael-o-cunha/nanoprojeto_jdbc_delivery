package com.nanoprojeto.delivery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
	private static Connection conn = null;
	
	public static Connection getConnection(EnvFile ev) {
		if(conn == null) {
			try {
				String host = null;
                String port = null;
                String database = null;
                String user = null;
                String password = null;
				
				if(ev.equals(EnvFile.PROPERTIES)) {
					Properties props = LoadProperty.LoadProperties();
					host = props.getProperty("POSTGRES_HOST");
	                port = props.getProperty("POSTGRES_PORT");
	                database = props.getProperty("POSTGRES_DB");
	                user = props.getProperty("POSTGRES_USER");
	                password = props.getProperty("POSTGRES_PASSWORD");
				}
				else if(ev.equals(EnvFile.ENV)) {
					host = System.getenv("POSTGRES_HOST");
	                port = System.getenv("POSTGRES_PORT");
	                database = System.getenv("POSTGRES_DB");
	                user = System.getenv("POSTGRES_USER");
	                password = System.getenv("POSTGRES_PASSWORD");
				}
                /*System.out.println(
                		"host: " + host + " \n " +
                		"port: " + port + " \n " +
                		"database: " + database + " \n " +
                		"user: " + user + " \n " +
                		"password: " + password + " \n "
                		);*/
                
                String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
                conn = DriverManager.getConnection(url, user, password);
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
				conn = null;
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}


	public static Connection createNewConnection(EnvFile ev) {
		try {
			String host = null;
			String port = null;
			String database = null;
			String user = null;
			String password = null;

			if (ev.equals(EnvFile.PROPERTIES)) {

				Properties props = LoadProperty.LoadProperties();

				host = props.getProperty("POSTGRES_HOST");
				port = props.getProperty("POSTGRES_PORT");
				database = props.getProperty("POSTGRES_DB");
				user = props.getProperty("POSTGRES_USER");
				password = props.getProperty("POSTGRES_PASSWORD");

			}
			else if (ev.equals(EnvFile.ENV)) {

				host = System.getenv("POSTGRES_HOST");
				port = System.getenv("POSTGRES_PORT");
				database = System.getenv("POSTGRES_DB");
				user = System.getenv("POSTGRES_USER");
				password = System.getenv("POSTGRES_PASSWORD");
			}

			String url =
					"jdbc:postgresql://" +
					host + ":" +
					port + "/" +
					database;

			return DriverManager.getConnection(url, user, password);

		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
}
