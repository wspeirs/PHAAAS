package com.bittrust;

import java.io.IOException;

import org.apache.http.HttpException;

import com.bittrust.http.server.HttpServer;
import com.bittrust.http.server.handlers.FileServerHandler;
import com.bittrust.http.server.handlers.PassThroughRequestHandler;

public class Main {

	/**
	 * @param args
	 * @throws HttpException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		HttpServer server = new HttpServer((short) 8080, 5);
		
		PassThroughRequestHandler passThrough = new PassThroughRequestHandler();
		FileServerHandler fileServer = new FileServerHandler();
		
		server.setHandler("/phaaas/*", fileServer);
		server.setHandler("*", passThrough);
		
		Thread t = new Thread(server);
		
		t.start();
		
		t.join();
	}
	
}
