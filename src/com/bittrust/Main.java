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

		// setup the server
		HttpServer server = new HttpServer(sc.getPort(), sc.getThreadCount());
		
		ArrayList<ServiceConfig> serviceConfigs = sc.getServiceConfigs();
		
		// go through each services and create a handler for it
		for(ServiceConfig config:serviceConfigs) {
			HttpRequestor requestor = new BasicHttpRequestor(config.getHost());	// this will come from the config at some point
			PhaaasRequestHandler ch = new PhaaasRequestHandler(config, requestor);
			
			server.setHandler(config.getUrl(), ch);
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
		Option configOption = OptionBuilder.withArgName("configuration.xml")
										   .withLongOpt("config")
										   .hasArg()
										   .withDescription("Configuration file")
										   .create("c");
		// create the check option
		@SuppressWarnings("static-access")
		Option checkOption = OptionBuilder.withLongOpt("check")
										  .withDescription("Check the configuration file")
										  .create();

		// add all the options from above
		options.addOption(configOption);
		options.addOption(checkOption);

		// add new simple options
		options.addOption(new Option("h", "help", false, "Print this help message"));

		return options;
	}
	
}
