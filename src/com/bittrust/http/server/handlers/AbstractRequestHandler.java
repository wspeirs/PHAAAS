/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.http.client.HttpRequestor;

/**
 * @class AbstractRequestHandler
 * 
 * An abstract handler which provides the basic framework for all other handlers
 */
public abstract class AbstractRequestHandler implements HttpRequestHandler {
	
	private Set<String> allowedHeaders;
	private Authenticator authenticator;
	private Authorizer authorizer;
	private HttpRequestor httpRequestor;
	
	public AbstractRequestHandler() {
		this.allowedHeaders = null;
		this.authenticator = null;
		this.authorizer = null;
		this.httpRequestor = null;
	}
	
	public AbstractRequestHandler(Set<String> allowedHeaders, Authenticator authenticator, Authorizer authorizer, HttpRequestor httpRequestor) {
		this.allowedHeaders = allowedHeaders;
		this.authenticator = authenticator;
		this.authorizer = authorizer;
		this.httpRequestor = httpRequestor;
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
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		// attempt to authenticate the user
		if(!authenticator.authenticate(request)) {
			authenticateFailed(request, response, context);
			return;
		}
		
		// see if the user is authorized
		if(!authorizer.authorize(request)) {
			authorizeFailed(request, response, context);
			return;
		}
		
		// white list the request
		if(allowedHeaders != null)
			request = whitelistRequest(request);
		
		// make the request to the resource
		HttpResponse clientResponse = httpRequestor.request(request, context);
		
		// copy over the client's response
		if(clientResponse != null)
			copyResponse(clientResponse, response);
	}
	
	//
	// Maybe these two should be methods of their respective classes?
	public abstract void authenticateFailed(HttpRequest request, HttpResponse response, HttpContext context);

	public abstract void authorizeFailed(HttpRequest request, HttpResponse response, HttpContext context);

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
	
	private void copyResponse(HttpResponse sourceResponse, HttpResponse destinationResponse) {
		// copy over the status line
		destinationResponse.setStatusLine(sourceResponse.getStatusLine());
		
		// copy over the headers
		destinationResponse.setHeaders(sourceResponse.getAllHeaders());
		
		// copy over the entity
		destinationResponse.setEntity(sourceResponse.getEntity());
	}
}
