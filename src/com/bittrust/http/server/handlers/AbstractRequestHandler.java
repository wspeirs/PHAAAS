/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetAddress;
import java.util.Set;

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
import com.bittrust.http.HTTPUtils;
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
		
		// log the connection
		StringBuilder log = auditor.receivedConnection((InetAddress)context.getAttribute("REMOTE_ADDRESS"));
		
		boolean needsAuth = true;
		String sessionID = HTTPUtils.getCookie(request, SESSION_COOKIE);
		String sessionMetaData = null;
		String user = null;
		
		// only if the session is valid do we NOT need to auth
		if(sessionID != null) {
			if(sessionStore.validateSession(sessionID) == true) {
				needsAuth = false;	// we have a valid session ID, so no auth needed
				sessionMetaData = sessionStore.retrieveMetaData(sessionID);
				// get the user from the session data
			} else { // we got a session ID, but it is bogus
				sessionID = null;
				user = authenticator.getUser(request);
			}
		}

		// log the request
		auditor.receivedRequest(request, user);
		
		// attempt to authenticate the user
		if(needsAuth && !authenticator.authenticate(request)) {
			auditor.authenticationFailed(log, user);
			auditor.writeLog(log);
			authenticator.authenticationFailed(request, response, context);
			return;
		} else { // we have a user that has authenticated
			sessionID = sessionStore.createSession();	// create a session
			// store the user name in the meta data
		}

		
		// see if the user is authorized
		if(!authorizer.authorize(request)) {
			auditor.authorizationFailed(log, user);
			auditor.writeLog(log);
			authorizer.authorizationFailed(request, response, context);
			return;
		}
		
		// white list the request
		if(allowedHeaders != null)
			request = whitelistRequest(request);
		
		// make the request to the resource
		HttpResponse serverResponse = httpRequestor.request(request, context);
		
		// copy over the client's response (stupid pass by value...)
		if(serverResponse != null) {
			if(!needsAuth)	// we didn't need auth, so we already had a session cookie
				createResponse(response, serverResponse);
			else	// we needed to auth, so we must have created a new session cookie
				createResponse(response, serverResponse, HTTPUtils.getHeader(request, "Host"), sessionID);
		}
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
	 * Creates a response based on the server's response.
	 * @param responseToClient The response to send to the client
	 * @param responseFromServer The response from the server
	 */
	private void createResponse(HttpResponse responseToClient, HttpResponse responseFromServer) {
		// copy over the status line
		responseToClient.setStatusLine(responseFromServer.getStatusLine());
		
		// copy over the headers
		responseToClient.setHeaders(responseFromServer.getAllHeaders());
		
		// copy over the entity
		responseToClient.setEntity(responseFromServer.getEntity());
	}

	/**
	 * Creates a response based on the server's response and injects the session cookie.
	 * @param responseToClient The response to send to the client
	 * @param responseFromServer The response from the server
	 * @param host The host to set for the cookie.
	 * @param sessionID The session ID for this session
	 */
	private void createResponse(HttpResponse responseToClient, HttpResponse responseFromServer, String host, String sessionID) {
		createResponse(responseToClient, responseFromServer);	// create the response

		// create a cookie for the session ID
		HttpCookie phaaasCookie = new HttpCookie(SESSION_COOKIE, sessionID);

		if(host != null) {
			// parse out the last 2 parts of the host for the domain
			int index = host.lastIndexOf('.');
			index = host.lastIndexOf('.', index-1);
			
			phaaasCookie.setDomain(host.substring(index, host.length()));
		}

		// setup the cookie
		phaaasCookie.setMaxAge(900);	// 15 minutes
		phaaasCookie.setPath("/");
		phaaasCookie.setVersion(1);		// set to the RFC 2965/2109 version
		
		responseToClient.addHeader("Set-Cookie", phaaasCookie.toString());
	}

}
