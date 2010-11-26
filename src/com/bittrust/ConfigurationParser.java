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
	
	public static void main(String[] args) {
		Digester digester = new Digester();
		
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
		

		try {
			File f = new File("configuration.xml");
			
			if(!f.exists()) {
				System.err.println("Cannot find file: " + f.getAbsolutePath());
				return;
			}
		
			ServerConfig sc = (ServerConfig)digester.parse(f);
			
			System.out.println("PORT: " + sc.getPort());
			
			ArrayList<ServiceConfig> scs = sc.getServiceConfigs();
			
			for(ServiceConfig s:scs) {
				System.out.println("URL: " + s.getUrl());
				
				System.out.println("CLASS NAME: " + s.getAuthenticationConfig().getClassName());
				System.out.println("CLASS NAME: " + s.getAuthorizationConfig().getClassName());
				System.out.println("CLASS NAME: " + s.getAuditingConfig().getClassName());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}
