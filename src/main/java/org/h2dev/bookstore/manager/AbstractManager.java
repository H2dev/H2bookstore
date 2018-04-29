package org.h2dev.bookstore.manager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.j256.ormlite.support.ConnectionSource;

public abstract class AbstractManager {

	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "spring-beans.xml" });
	ConnectionSource cs = (ConnectionSource) context.getBean("connectionSource");

	protected static final Logger logger = Logger.getLogger("H2bookstore");

	public void setLoggerLevel(Level level) {
		logger.setLevel((Level) level);
		System.out.println();
	}

}
