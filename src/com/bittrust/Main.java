package com.bittrust;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.http.HttpException;

import com.bittrust.config.ServerConfig;
import com.bittrust.config.ServiceConfig;
import com.bittrust.http.client.BasicHttpRequestor;
import com.bittrust.http.client.HttpRequestor;
import com.bittrust.http.client.ProxyHttpRequestor;
import com.bittrust.http.server.HttpServer;
import com.bittrust.http.server.handlers.PhaaasRequestHandler;

public class Main {

	/**
	 * @param args
	 * @throws HttpException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		
		// create our command-line options
		Options options = createCommandLineOptions();
		CommandLineParser parser = new PosixParser();
		CommandLine commandLine = parser.parse(options, args);
		
		// see if we should print the help
		if(commandLine.hasOption("h")) {
			HelpFormatter helpFormatter = new HelpFormatter();
			
			helpFormatter.printHelp("phaaas", options);
			
			return;
		}
		
		File configFile = new File("configuration.xml");
		
		// check for a different configuration file
		if(commandLine.hasOption("c")) {
			configFile = new File(commandLine.getOptionValue("c"));
		}
		
		// parse out the configuration file
		ConfigurationParser cp = new ConfigurationParser();
		ServerConfig sc = cp.parse(configFile);
		
		// make sure we have a valid configuration
		if(sc == null) {
			System.err.println("Invalid configuration file");
			return;
		}
		
		// see if the user just wants to check the config file
		if(commandLine.hasOption("check")) {
			System.out.println("*** SERVER ***");
			System.out.println("    HTTP PORT: " + sc.getPort());
			System.out.println(" THREAD COUNT: " + sc.getThreadCount());
			
			// print out the global auditor & session store
			if(sc.getAuditor() == null) {
				System.out.println("NO AUDITOR SET!!!");
				return;
			} else {
				System.out.println("      AUDITOR: " + sc.getAuditor().getClass().getCanonicalName());
			}
			
			if(sc.getSessionStore() == null) {
				System.out.println("NO SESSION STORE SET!!!");
				return;
			} else {
				System.out.println("SESSION STORE: " + sc.getSessionStore().getClass().getCanonicalName());
			}
			
			System.out.println();
			System.out.println("*** SERVICES ***");
			System.out.println();

			// go through the services
			for(ServiceConfig config:sc.getServiceConfigs()) {
				System.out.println("     SERVICE HOST: " + config.getHost());
				System.out.println("      URL PATTERN: " + config.getUrl());
				System.out.println("    CRED PROVIDER: " + config.getCredentialConfig().getClassName());
				System.out.println("    AUTHENTICATOR: " + config.getAuthenticationConfig().getClassName());
				System.out.println("       AUTHORIZER: " + config.getAuthorizationConfig().getClassName());
				System.out.println(" REQUEST MODIFIER: " + config.getRequestConfig().getClassName());
				System.out.println("RESPONSE MODIFIER: " + config.getResponseConfig().getClassName());
				
				System.out.println();
			}
			
			return;
		}

		// setup the server
		HttpServer server = new HttpServer(sc.getPort(), sc.getThreadCount());
		
		ArrayList<ServiceConfig> serviceConfigs = sc.getServiceConfigs();
		
		// go through each services and create a handler for it
		for(ServiceConfig config:serviceConfigs) {
			HttpRequestor requestor = null;
			
			// if the host is *, then we simply proxy the request
			if(config.getHost().equals("*"))
				requestor = new ProxyHttpRequestor();
			else
				requestor = new BasicHttpRequestor(config.getHost());
				
			PhaaasRequestHandler ch = new PhaaasRequestHandler(config, requestor);
			
			server.setHandler(config.getUrl(), ch);
			System.out.println("SETTING: " + requestor.getClass().getCanonicalName() + " FOR: " + config.getUrl());
		}
		
		// start-up the server
		Thread t = new Thread(server);
		
		t.start();
		
		t.join();
	}
	
	private static Options createCommandLineOptions() {
		Options options = new Options();

		// setup the configuration file option
		@SuppressWarnings("static-access")
		Option configOption = OptionBuilder.withLongOpt("config")
										   .hasArg()
										   .withArgName("configuration.xml")
										   .withDescription("Configuration file")
										   .create("c");
		// create the check option
		@SuppressWarnings("static-access")
		Option checkOption = OptionBuilder.withLongOpt("check")
										  .withDescription("Check the configuration file")
										  .create();

		// create the plugins directory option
		@SuppressWarnings("static-access")
		Option pluginsOption = OptionBuilder.withLongOpt("plugins-dir")
										    .hasArg()
										    .withArgName("/path/to/plugins")
										    .withDescription("Path to the plugins directory")
										    .create("p");

		// add all the options from above
		options.addOption(configOption);
		options.addOption(checkOption);
		options.addOption(pluginsOption);

		// add new simple options
		options.addOption(new Option("h", "help", false, "Print this help message"));

		return options;
	}
	
}
