/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;
import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.bittrust.auditing.Auditor;
import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.http.client.BasicHttpRequestor;
import com.bittrust.http.client.HttpRequestor;
import com.bittrust.session.SessionStore;

/**
 * @class AbstractRequestHandler
 * 
 * An abstract handler which provides the basic framework for all other handlers
 */
public abstract class AbstractRequestHandler implements HttpRequestHandler {
	
	private Set<String> allowedHeaders;
	private Authenticator authenticator;
	private Authorizer authorizer;
	private Auditor auditor;
	private SessionStore sessionStore;
	private HttpRequestor httpRequestor;
	
	private final static String SESSION_COOKIE = "PHAAASID";
	
	public AbstractRequestHandler() {
		this.allowedHeaders = null;
		this.authenticator = null;
		this.authorizer = null;
		this.httpRequestor = new BasicHttpRequestor();
	}
	
	public AbstractRequestHandler(Set<String> allowedHeaders, Authenticator authenticator, Authorizer authorizer, Auditor auditor) {
		this.allowedHeaders = allowedHeaders;
		this.authenticator = authenticator;
		this.authorizer = authorizer;
		this.auditor = auditor;
		this.httpRequestor = new BasicHttpRequestor();
	}

	/**
	 * @param allowedHeaders the allowedHeaders to set
	 */
	public void setAllowedHeaders(Set<String> allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}

	/**
	 * @param authenticator the authenticator to set
	 */
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * @param authorizer the authorizer to set
	 */
	public void setAuthorizer(Authorizer authorizer) {
		this.authorizer = authorizer;
	}

	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	/**
	 * @param httpRequestor the httpRequestor to set
	 */
	public void setHttpRequestor(HttpRequestor httpRequestor) {
		this.httpRequestor = httpRequestor;
	}

	/**
	 * The handle method to be implemented for handling the request
	 * @param request The HTTP request
	 * @param response The HTTP response
	 * @param context The HTTP execution context
	 */
	public final void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		
		// check to see if they have a valid session ID
		boolean needsAuth = true;
		String sessionID = getCookie(request, SESSION_COOKIE);
		
		// only if the session is valid do we NOT need to auth
		if(sessionID != null) {
			if(sessionStore.validateSession(sessionID) == true) {
				needsAuth = false;	// we have a valid session ID, so no auth needed
			} else { // we got a session ID, but it is bogus
				sessionID = null;
			}
		}
		
		System.out.println(request.getRequestLine() + " ID: " + sessionID + " AUTH: " + needsAuth);

		// attempt to authenticate the user
		if(needsAuth && !authenticator.authenticate(request)) {
			authenticator.authenticationFailed(request, response, context);
			return;
		}

		
		// see if the user is authorized
		if(!authorizer.authorize(request)) {
			authorizer.authorizationFailed(request, response, context);
			return;
		}
		
		// white list the request
		if(allowedHeaders != null)
			request = whitelistRequest(request);
		
		// make the request to the resource
		HttpResponse serverResponse = httpRequestor.request(request, context);
		
		// copy over the client's response (stupid pass by value...)
		if(serverResponse != null)
			createResponse(response, serverResponse, request, sessionID);
	}
	
	/**
	 * Strip-out the headers that are not in the allowed list
	 * @param request The request to modify
	 * @return A modified version of the request
	 */
	private HttpRequest whitelistRequest(HttpRequest request) {
		HeaderIterator iterator = request.headerIterator();

		// loop over the headers
		while(iterator.hasNext()) {
			Header h = iterator.nextHeader();
			
			// if the header is not in the allowed list, then remove it
			if(!allowedHeaders.contains(h.getName()))
				request.removeHeader(h);
		}
		
		return request;	// return the request afters stripping headers
	}
	
	/**
	 * Creates a response based on the client's response and the session ID
	 * @param responseToClient The response to send to the client
	 * @param responseFromServer The response from the server
	 * @param sessionID The session ID, if null then a new one will be created
	 */
	private void createResponse(HttpResponse responseToClient, HttpResponse responseFromServer, HttpRequest request, String sessionID) {
		// copy over the status line
		responseToClient.setStatusLine(responseFromServer.getStatusLine());
		
		// copy over the headers
		responseToClient.setHeaders(responseFromServer.getAllHeaders());
		
		// check to see if we need to create a new session
		if(sessionID == null) {
			HttpCookie phaaasCookie = new HttpCookie(SESSION_COOKIE, sessionStore.createSession());
			String host = getHeader(request, "Host");

			// parse out the last 2 parts of the host for the domain
			int index = host.lastIndexOf('.');
			index = host.lastIndexOf('.', index-1);
			
			// setup the cookie
			phaaasCookie.setMaxAge(900);	// 15 minutes
			phaaasCookie.setPath("/");
			phaaasCookie.setDomain(host.substring(index, host.length()));
			phaaasCookie.setVersion(1);		// set to the RFC 2965/2109 version
			
			responseToClient.addHeader("Set-Cookie", phaaasCookie.toString());
		}
		
		// copy over the entity
		responseToClient.setEntity(responseFromServer.getEntity());
	}
	
	/**
	 * Go through the headers getting the requested header or null if not found.
	 * @param request The request to search.
	 * @param headerName The name of the header to find.
	 * @return The value of the header, or null if not found.
	 */
	private String getHeader(HttpRequest request, String headerName) {
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
	private String getCookie(HttpRequest request, String cookieName) {
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
}
