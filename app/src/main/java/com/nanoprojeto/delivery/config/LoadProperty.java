package com.nanoprojeto.delivery.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadProperty {
	
	public static Properties LoadProperties() {
		try(FileInputStream fd = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fd);
			return props;
		}
		catch(IOException e) {
			throw new DbException(e.getMessage());
		}
	}
}
