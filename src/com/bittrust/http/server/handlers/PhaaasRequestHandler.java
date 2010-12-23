/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.bittrust.auditing.Auditor;
import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.config.BasicModuleConfig;
import com.bittrust.config.ServiceConfig;
import com.bittrust.credential.providers.CredentialProvider;
import com.bittrust.credential.providers.CredentialProvider.CredentialProviderResult;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;
import com.bittrust.http.RequestModifier;
import com.bittrust.http.ResponseModifier;
import com.bittrust.http.client.HttpRequestor;
import com.bittrust.session.SessionStore;

/**
 * @class PhaaasRequestHandler
 * 
 * An abstract handler which provides the basic framework for all other handlers
 */
public class PhaaasRequestHandler implements HttpRequestHandler {
	
	// modules
	private CredentialProvider credentialProvider;
	private Authenticator authenticator;
	private Authorizer authorizer;
	private RequestModifier requestModifier;
	private ResponseModifier responseModifier;
	
	// possibly global modules
	private Auditor auditor;
	private SessionStore sessionStore;
	private HttpRequestor httpRequestor;	//TODO: Re-work this
	
	public PhaaasRequestHandler(ServiceConfig serviceConfig, HttpRequestor requestor) throws Exception {

		this.auditor = serviceConfig.getAuditor();
		this.sessionStore = serviceConfig.getSessionStore();
		this.httpRequestor = requestor;
		
		try {
			// setup the credential provider
			@SuppressWarnings("unchecked")
			Class<CredentialProvider> credClass = (Class<CredentialProvider>) Class.forName(serviceConfig.getCredentialConfig().getClassName());
			this.credentialProvider = (CredentialProvider)credClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getCredentialConfig());

			// setup the authenticator
			@SuppressWarnings("unchecked")
			Class<Authenticator> authClass = (Class<Authenticator>) Class.forName(serviceConfig.getAuthenticationConfig().getClassName());
			this.authenticator = (Authenticator)authClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getAuthenticationConfig());

			// setup the authorizer
			@SuppressWarnings("unchecked")
			Class<Authorizer> authzClass = (Class<Authorizer>) Class.forName(serviceConfig.getAuthorizationConfig().getClassName());
			this.authorizer = (Authorizer)authzClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getAuthorizationConfig());

			// setup the request modifier
			@SuppressWarnings("unchecked")
			Class<RequestModifier> requestClass = (Class<RequestModifier>) Class.forName(serviceConfig.getRequestConfig().getClassName());
			this.requestModifier = (RequestModifier)requestClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getRequestConfig());

			// setup the response modifier
			@SuppressWarnings("unchecked")
			Class<ResponseModifier> responseClass = (Class<ResponseModifier>) Class.forName(serviceConfig.getResponseConfig().getClassName());
			this.responseModifier = (ResponseModifier)responseClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getResponseConfig());

		} catch (ClassNotFoundException e) {
			System.err.println("COULD NOT FIND THE CLASS: " + e.getLocalizedMessage());
			throw e;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * The handle method to be implemented for handling the request
	 * @param request The HTTP request
	 * @param response The HTTP response
	 * @param context The HTTP execution context
	 */
	public final void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
		
		// log the connection & the request
		StringBuilder log = auditor.receivedConnection((InetAddress)httpContext.getAttribute("REMOTE_ADDRESS"));
		auditor.receivedRequest(log, request);
		
		// create the PhaaasContext
		PhaaasContext context = new PhaaasContext(httpContext, request);
		
		// get the credentials from the request
		CredentialProviderResult credRes = credentialProvider.getCredentialOrPrincipal(sessionStore, context);
		
		boolean isAuthorized = false;
		
		// based upon the result we do different things
		switch(credRes) {
		case CREDENTIAL_FOUND:	// creds found, continue with auth
			auditor.credentialFound(log, context.getCredential());	// log the fact that we got the creds
			
			// try to authenticate the user
			if(!authenticator.authenticate(context)) {
				HttpResponse ret = context.getHttpResponse();
				
				auditor.authenticationFailed(log, ret);
				auditor.writeLog(log);
				
				HttpUtils.copyResponse(response, ret);	// copy the response to send to the client
				return;
			}
			
		// if the auth works, we fall-through here
		case PRINCIPAL_FOUND:	// we have a principal from a previous authentication or from the authenticator
			auditor.principalFound(log, context.getPrincipal());
			isAuthorized = authorizer.authorize(context);
			break;
		case SEND_RESPONSE:		// we need to send a response back to the client
			HttpUtils.copyResponse(response, context.getHttpResponse());
			auditor.serverResponse(log, response);
			auditor.writeLog(log);
			return;
		}
		
		// at this point we have a good principal so we should make a session
		// granted the user might not be authorized, but they did authenticate
		String sessionId = context.getSessionId();
		String host = context.getHttpRequest().getFirstHeader("Host").toString();
		
		// see if we've saved this principal yet, if not then save it
		if(sessionId == null) {
			System.out.println("CREATING NEW SESSION");
			sessionId = sessionStore.createSession(context.getPrincipal());
		}

		// not authorized so copy the response and send it to the client
		if(!isAuthorized) {
			HttpResponse unauthResponse = context.getHttpResponse();
			
			// log that authz failed
			auditor.authorizationFailed(log, unauthResponse);
			auditor.writeLog(log);

			// set the session cookie here
			HttpUtils.setCookie(unauthResponse, host, SessionStore.SESSION_COOKIE, sessionId);
			
			// copy the response over
			HttpUtils.copyResponse(response, unauthResponse);
			return;	// this will return the response to the client
		}
		
		// modify the request to send to the server
		HttpRequest modifiedRequest = requestModifier.modifyRequest(context);
		
		// send the request to the server
		context.setHttpResponse(httpRequestor.request(modifiedRequest, context));
		
		// modify the response
		 HttpResponse responseForClient = responseModifier.modifyResponse(context);
		 
		 // insert the session cookie
		HttpUtils.setCookie(responseForClient, host, SessionStore.SESSION_COOKIE, sessionId);
		 
		 // copy over the response
		 HttpUtils.copyResponse(response, responseForClient);
		 
		 // log the response
		 auditor.serverResponse(log, response);
		 auditor.writeLog(log);
	}
}
