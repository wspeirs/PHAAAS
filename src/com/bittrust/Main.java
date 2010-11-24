package com.bittrust;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.protocol.HttpService;

public class Main {
	private HttpRequestHandlerRegistry resolver;
	private HttpRequestHandler myHandler;
	
	private ServerSocket serverSocket;
	private Socket socket;

	public Main() throws Exception {
		resolver = new HttpRequestHandlerRegistry();
		myHandler = new MyHttpHandler();
		
		// register my handler with all URLs
		resolver.register("*", myHandler);
		
		serverSocket = new ServerSocket(8080);
		ExecutorService pool = Executors.newFixedThreadPool(5);

		
		while(true) {
			socket = serverSocket.accept();
			pool.execute(new SocketWorker(socket, resolver));
		}
	}

	/**
	 * @param args
	 * @throws HttpException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	private class SocketWorker extends Thread {
		private Socket socket;
		private DefaultHttpServerConnection connection;
		private HttpParams params;
		private HttpRequestHandlerResolver resolver;
		private HttpProcessor processor;
		private HttpResponseFactory responseFactory;
		private HttpContext context;

		public SocketWorker(Socket socket, HttpRequestHandlerResolver resolver) {
			this.socket = socket;
			this.resolver = resolver;
			this.params = new BasicHttpParams();
			this.connection = new DefaultHttpServerConnection();
			this.processor = new BasicHttpProcessor();
			this.responseFactory = new DefaultHttpResponseFactory();
			this.context = new BasicHttpContext();

			try {
				connection.bind(this.socket, this.params);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		public void run() {
			
			HttpService httpService = new HttpService(this.processor,
													  new DefaultConnectionReuseStrategy(),
													  this.responseFactory,
													  this.resolver,
													  this.params);

			while(connection.isOpen()) {
				try {
					httpService.handleRequest(connection, context);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	private class MyHttpHandler implements HttpRequestHandler {
		
		@Override
		public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
			response.setStatusCode(HttpStatus.SC_OK);
			response.addHeader("Content-Type", "text/plain");
			
			String entity = "Request Line: " + request.getRequestLine();
			response.setEntity(new StringEntity(entity));
			
			response.addHeader("Content-Length", entity.length() + "");
			
			System.out.println("Processed");
		}
		
	}

}
