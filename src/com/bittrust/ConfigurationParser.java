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
		digester.addObjectCreate("server/service/authentication", "com.bittrust.config.BasicModuleConfig");
		digester.addSetProperties("server/service/authentication");
		digester.addSetNext("server/service/authentication", "setAuthenticationConfig");
		digester.addCallMethod("server/service/authentication/param", "addParam", 2);
		digester.addCallParam("server/service/authentication/param", 0, "name");
		digester.addCallParam("server/service/authentication/param", 1);
		
		// setup authorization
		digester.addObjectCreate("server/service/authorization", "com.bittrust.config.BasicModuleConfig");
		digester.addSetProperties("server/service/authorization");
		digester.addSetNext("server/service/authorization", "setAuthorizationConfig");
		digester.addCallMethod("server/service/authorization/param", "addParam", 2);
		digester.addCallParam("server/service/authorization/param", 0, "name");
		digester.addCallParam("server/service/authorization/param", 1);

		// setup auditing
		digester.addObjectCreate("server/service/auditing", "com.bittrust.config.BasicModuleConfig");
		digester.addSetProperties("server/service/auditing");
		digester.addSetNext("server/service/auditing", "setAuditingConfig");
		digester.addCallMethod("server/service/auditing/param", "addParam", 2);
		digester.addCallParam("server/service/auditing/param", 0, "name");
		digester.addCallParam("server/service/auditing/param", 1);
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
