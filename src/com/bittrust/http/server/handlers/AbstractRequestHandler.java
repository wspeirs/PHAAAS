/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONObject;

import com.bittrust.auditing.Auditor;
import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.credential.providers.CredentialProvider;
import com.bittrust.credential.providers.CredentialProvider.CredentialProviderResult;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;
import com.bittrust.http.RequestModifier;
import com.bittrust.http.ResponseModifier;
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
	private CredentialProvider credentialProvider;
	private Authenticator authenticator;
	private Authorizer authorizer;
	private RequestModifier requestModifier;
	private ResponseModifier responseModifier;
	
	
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
	public final void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
		
		// log the connection
		StringBuilder log = auditor.receivedConnection((InetAddress)httpContext.getAttribute("REMOTE_ADDRESS"));
		
		// create the PhaaasContext
		PhaaasContext context = new PhaaasContext(httpContext, request);
		
		// get the credentials from the request
		CredentialProviderResult credRes = credentialProvider.getCredentialOrPrincipal(sessionStore, context);
		
		boolean isAuthorized = false;
		
		// based upon the result we do different things
		switch(credRes) {
		case CREDENTIAL_FOUND:	// creds found, continue with auth
			context.setPrincipal(authenticator.authenticate(context));
		case PRINCIPAL_FOUND:	// we have a principal from a previous authentication or from the authenticator
			isAuthorized = authorizer.authorize(context);
			break;
		case SEND_RESPONSE:		// we need to send a response back to the client
			HttpUtils.copyResponse(response, context.getHttpResponse());
			return;
		}

		// not authorized so copy the response and send it to the client
		if(!isAuthorized) {
			String sessionId = context.getSessionId();
			HttpResponse unauthResponse = context.getHttpResponse();
			
			// see if we've saved this principal yet, if not then save it
			// at this point auth passed, so we want to save this session
			if(sessionId == null) {
				String host = context.getHttpRequest().getFirstHeader("Host").toString();
				sessionId = sessionStore.createSession(context.getPrincipal());
				HttpUtils.setCookie(unauthResponse, host, SESSION_COOKIE, sessionId);
			}
			
			// copy the response over
			HttpUtils.copyResponse(response, unauthResponse);
			return;	// this will return the response to the client
		}
		
		// modify the request to send to the server
		HttpRequest modifiedRequest = requestModifier.modifyRequest(context);
		
		// send the request to the server
		context.setHttpResponse(HttpUtils.makeRequest(modifiedRequest, context));
		
		// modify the response
		 HttpResponse responseForClient = responseModifier.modifyResponse(context);
		 
		 // copy over the response
		 HttpUtils.copyResponse(responseForClient, context.getHttpResponse());
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
	
}
