package org.linuxkernel.proof.digger.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	static Properties props = new Properties();
	static {
		InputStream in = Config.class.getResourceAsStream("/config.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final String DEFAULT_DIC = props.getProperty("default.dic");
	public static final String AMBIGUITY_DIC = props.getProperty("ambiguity.dic");
	public static final String COUSTOM_DIC = props.getProperty("custom.dic.dir");
	public static final String ACCEPT = props.getProperty("accept");
	public static final String ENCODING = props.getProperty("encoding");
	public static final String LANGUAGE = props.getProperty("language");
	public static final String CONNECTION = props.getProperty("connection");
	public static final String HOST = props.getProperty("host");
	public static final String USER_AGENT = props.getProperty("user_agent");
}
