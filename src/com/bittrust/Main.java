package com.bittrust;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;

import com.bittrust.config.ServerConfig;
import com.bittrust.config.ServiceConfig;
import com.bittrust.http.client.BasicHttpRequestor;
import com.bittrust.http.client.HttpRequestor;
import com.bittrust.http.server.HttpServer;
import com.bittrust.http.server.handlers.PhaaasRequestHandler;

public class Main {

	/**
	 * @param args
	 * @throws HttpException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {

		// parse out the configuration file
		ConfigurationParser cp = new ConfigurationParser();
		ServerConfig sc = cp.parse(new File("configuration.xml"));
		
		// make sure we have a valid configuration
		if(sc == null)
			return;

		// setup the server
		HttpServer server = new HttpServer(sc.getPort(), sc.getThreadCount());
		
		ArrayList<ServiceConfig> serviceConfigs = sc.getServiceConfigs();
		
		// go through each services and create a handler for it
		for(ServiceConfig config:serviceConfigs) {
			HttpRequestor requestor = new BasicHttpRequestor();	// this will come from the config at some point
			PhaaasRequestHandler ch = new PhaaasRequestHandler(config, requestor);
			
			server.setHandler(config.getUrl(), ch);
		}
		
		// start-up the server
		Thread t = new Thread(server);
		
		t.start();
		
		t.join();
	}
	
}
