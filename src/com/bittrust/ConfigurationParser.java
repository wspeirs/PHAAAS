/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.bittrust.config.ServerConfig;

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
		
		// get the global session and auditor configs
		addBasicModuleConfig("server/sessionStore", "setSessionConfig");
		addBasicModuleConfig("server/auditing", "setAuditConfig");
		
		// get defaults for all other modules
		addBasicModuleConfig("server/principalProvider", "setPrincipalConfig");
		addBasicModuleConfig("server/credentialProvider", "setCredentialConfig");
		addBasicModuleConfig("server/authentication", "setAuthenticationConfig");
		addBasicModuleConfig("server/authorization", "setAuthorizationConfig");
		addBasicModuleConfig("server/requestModifier", "setRequestConfig");
		addBasicModuleConfig("server/responseModifier", "setResponseConfig");
		
		
		// setup the services
		digester.addObjectCreate("server/service", "com.bittrust.config.ServiceConfig");
		digester.addSetProperties("server/service");
		digester.addSetNext("server/service", "addServiceConfig", "com.bittrust.config.ServiceConfig");
		
		// setup the per service modules
		addBasicModuleConfig("server/service/principalProvider", "setPrincipalConfig");
		addBasicModuleConfig("server/service/credentialProvider", "setCredentialConfig");
		addBasicModuleConfig("server/service/authentication", "setAuthenticationConfig");
		addBasicModuleConfig("server/service/authorization", "setAuthorizationConfig");
		addBasicModuleConfig("server/service/requestModifier", "setRequestConfig");
		addBasicModuleConfig("server/service/responseModifier", "setResponseConfig");
	}
	
	private void addBasicModuleConfig(String moduleName, String methodName) {
		digester.addObjectCreate(moduleName, "com.bittrust.config.BasicModuleConfig");
		digester.addSetProperties(moduleName);
		digester.addSetNext(moduleName, methodName);
		digester.addCallMethod(moduleName + "/param", "addParam", 2);
		digester.addCallParam(moduleName + "/param", 0, "name");
		digester.addCallParam(moduleName + "/param", 1);
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
			//e.printStackTrace();
		} catch (SAXException e) {
			System.err.println("ERROR PARSING CONFIG FILE: " + e.getLocalizedMessage());
		}
		
		return sc;
	}
}
