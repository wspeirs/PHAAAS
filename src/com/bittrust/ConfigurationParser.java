/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.config.ServerConfig;
import com.bittrust.config.ServiceConfig;

/**
 * @class ConfigurationParser
 */
public class ConfigurationParser {
	
	private Digester digester = new Digester();
	
	public ConfigurationParser() {
		digester.setValidating(false);
		
		// setup the digester for the server
		digester.addObjectCreate("server", "com.bittrust.config.ServerConfig");
		digester.addSetProperties("server");
		
		// setup the services
		digester.addObjectCreate("server/service", "com.bittrust.config.ServiceConfig");
		digester.addSetProperties("server/service");
		digester.addSetNext("server/service", "addServiceConfig", "com.bittrust.config.ServiceConfig");
		
		// setup authentication
		addBasicModuleConfig("authentication", "setAuthenticationConfig");
		
		// setup authorization
		addBasicModuleConfig("authorization", "setAuthorizationConfig");

		// setup auditing
		addBasicModuleConfig("auditing", "setAuditingConfig");
		
		// setup sessioning
		addBasicModuleConfig("session", "setSessionConfig");
	}
	
	private void addBasicModuleConfig(String moduleName, String methodName) {
		digester.addObjectCreate("server/service/" + moduleName, "com.bittrust.config.BasicModuleConfig");
		digester.addSetProperties("server/service/" + moduleName);
		digester.addSetNext("server/service/" + moduleName, methodName);
		digester.addCallMethod("server/service/" + moduleName + "/param", "addParam", 2);
		digester.addCallParam("server/service/" + moduleName + "/param", 0, "name");
		digester.addCallParam("server/service/" + moduleName + "/param", 1);
	}
	
	public ServerConfig parse(File configFile) {
		ServerConfig sc = null;
		
		try {
			if(!configFile.exists()) {
				System.err.println("Cannot find file: " + configFile.getAbsolutePath());
				return sc;
			}
		
			// parse the config file constructing the server config object
			sc = (ServerConfig)digester.parse(configFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return sc;
	}
	
	public static void main(String[] args) {
		
		

	}
}
