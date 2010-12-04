/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.protocol.HttpRequestHandlerRegistry;

import com.bittrust.http.server.handlers.PhaaasRequestHandler;

/**
 * @class HttpServer
 * 
 * The main class that sets up the sockets for receiving requests from clients.
 */
public class HttpServer implements Runnable {
	private HttpRequestHandlerRegistry resolver;
	private ExecutorService pool;
	private ServerSocket serverSocket;
	private boolean running;

	/**
	 * Setup the HTTP server to listen for connections.
	 * @param port The port to listen on.
	 * @param threadPoolSize The size of the thread pool.
	 * @throws IOException
	 */
	public HttpServer(short port, int threadPoolSize) throws IOException {
		this.resolver = new HttpRequestHandlerRegistry();	// create a new registry for the resolvers
		this.serverSocket = new ServerSocket(port);			// setup the server socket on the correct port
		this.pool = Executors.newFixedThreadPool(threadPoolSize);	// setup the thread pool for the connections
	}
	
	public void setHandler(String url, PhaaasRequestHandler handler) {
		resolver.register(url, handler);
	}
	
	/**
	 * Start processing requests.
	 */
	public void run() {
		running = true;
		
		while(running) {
			try {
				// accept an incoming connection
				Socket socket = serverSocket.accept();
				
				// start the worker for this connection
				pool.execute(new RequestWorker(socket, resolver));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop processing requests, gracefully shutting down.
	 */
	public void stop() {
		running = false;
		
		pool.shutdown();
	}
	
	/**
	 * Forcefully shutdown the server.
	 * Requests might or might not be authenticated, authorized, audited, or responses sent.
	 */
	public void shutdown() {
		running = false;
		
		pool.shutdownNow();
	}
}
