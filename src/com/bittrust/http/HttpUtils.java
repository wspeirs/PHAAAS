/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * @class HttpUtils
 */
public class HttpUtils {
	
	/**
	 * Go through the headers getting the requested header or null if not found.
	 * @param request The request to search.
	 * @param headerName The name of the header to find.
	 * @return The value of the header, or null if not found.
	 */
	public static String getHeader(HttpRequest request, String headerName) {
		HeaderIterator iterator = request.headerIterator();
		String ret = null;

		while(iterator.hasNext()) {
			Header h = iterator.nextHeader();
			
			if(h.getName().equalsIgnoreCase(headerName)) {
				
				ret = h.getValue();
				
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Returns the value of a cookie, given its name.
	 * @param cookieName The name of the cookie to find
	 * @return The value of the cookie or null if the cookie is not found.
	 */
	public static String getCookie(HttpRequest request, String cookieName) {
		String ret = null;
		String cookieHeader = getHeader(request, "Cookie");
		
		// if we cannot find the header, then cannot find the cookie
		if(cookieHeader == null)
			return ret;

		StringTokenizer tokenizer = new StringTokenizer(cookieHeader, "; ");

		// go through all the tokens splitting by ;
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			
			// make sure the token starts with cookieName=
			if(token.startsWith(cookieName + "=")) {
				tokenizer = new StringTokenizer(token, "=");
				
				tokenizer.nextToken();	// this cookieName
				ret = tokenizer.nextToken().replaceAll("\"", "");	// this is the value
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Copies a response based on the server's response.
	 * @param responseToClient The response to send to the client
	 * @param responseFromServer The response from the server
	 */
	public static void copyResponse(HttpResponse responseToClient, HttpResponse responseFromServer) {
		// copy over the status line
		responseToClient.setStatusLine(responseFromServer.getStatusLine());
		
		// copy over the headers
		responseToClient.setHeaders(responseFromServer.getAllHeaders());
		
		// copy over the entity
		responseToClient.setEntity(responseFromServer.getEntity());
	}
	
	/**
	 * Makes an HTTP request and returns the result.
	 * @param request The request to make.
	 * @param context The context of the request.
	 * @return The response from the request or null if an error occured.
	 */
	public static HttpResponse makeRequest(HttpRequest request, HttpContext context) {
		DefaultHttpClient client = new DefaultHttpClient();
		String host = request.getFirstHeader("Host").getValue();
		HttpHost httpHost = new HttpHost(host);
		HttpResponse response = null;
		
		try {
			response = client.execute(httpHost, request, context);
			
		} catch (ClientProtocolException e) {
			System.err.println("REQUEST: " + request.getRequestLine());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * Sets a cookie in a response.
	 * @param response The response to set the cookie in.
	 * @param host The hose to use in the cookie.
	 * @param cookieName The name of the cookie.
	 * @param cookieValue The value of the cookie.
	 */
	public static void setCookie(HttpResponse response, String host, String cookieName, String cookieValue) {
		// create a cookie
		HttpCookie cookie = new HttpCookie(cookieName, cookieValue);

		if(host != null) {
			// parse out the last 2 parts of the host for the domain
			int index = host.lastIndexOf('.');
			index = host.lastIndexOf('.', index-1);
			
			cookie.setDomain(host.substring(index, host.length()));
		}

		// setup the cookie
		cookie.setMaxAge(900);	// 15 minutes
		cookie.setPath("/");
		cookie.setVersion(1);	// set to the RFC 2965/2109 version
		
		response.addHeader("Set-Cookie", cookie.toString());
	}
	
	public enum StatusCode {
		UNAUTHENTICATED (401, "User Authentication Failed"),
		UNAUTHORIZED (403, "User Authorization Failed"),
		SERVER_ERROR (500, "Internal Server Error");
		
		private final int code;
		private final String reason;
		
		private StatusCode(int code, String reason) {
			this.code = code;
			this.reason = reason;
		}
		
		public int getCode() { return code; }
		public String getReason() { return reason; }
	}
	
	/**
	 * Create a BasicHttpResponse ONLY filling in the status line.
	 * @param code The code for the status line.
	 * @return A BasicHttpResponse with ONLY the status line filled in.
	 */
	public static BasicHttpResponse generateResponse(StatusCode code) {
		ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
		BasicHttpResponse ret = null;
		
		switch(code) {
		case UNAUTHENTICATED:
			ret = new BasicHttpResponse(pv, code.getCode(), code.getReason());
			break;
		case UNAUTHORIZED:
			ret = new BasicHttpResponse(pv, code.getCode(), code.getReason());
			break;
		default:
			ret = new BasicHttpResponse(pv, StatusCode.SERVER_ERROR.getCode(), StatusCode.SERVER_ERROR.getReason());
		}
		
		return ret;
	}
	
	/**
	 * Strip-out the headers from a request
	 * @param request The request to modify
	 * @param headers The headers to remove.
	 * @return A modified version of the request
	 */
	public static HttpRequest whitelistRequest(HttpRequest request, Set<String> headers) {
		HeaderIterator iterator = request.headerIterator();

		// loop over the headers
		while(iterator.hasNext()) {
			Header h = iterator.nextHeader();
			
			// if the header is not in the allowed list, then remove it
			if(headers.contains(h.getName()))
				request.removeHeader(h);
		}
		
		return request;	// return the request afters stripping headers
	}
}
