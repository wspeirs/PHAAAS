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
import com.bittrust.credential.providers.PrincipalProvider;
import com.bittrust.credential.providers.PrincipalProvider.PrincipalProviderResult;
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
	private PrincipalProvider principalProvider;
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
			// setup the principal provider
			@SuppressWarnings("unchecked")
			Class<PrincipalProvider> princClass = (Class<PrincipalProvider>) Class.forName(serviceConfig.getPrincipalConfig().getClassName());
			this.principalProvider = (PrincipalProvider)princClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getPrincipalConfig());

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
		
		// attempt to get a principal from the request
		PrincipalProviderResult ppRes = principalProvider.getPrincipalFromHttpRequest(context, sessionStore);
		
		// see if we need to send the response back
		if(PrincipalProviderResult.SEND_RESPONSE == ppRes) {
			// copy over the response
			HttpUtils.copyResponse(response, context);
			 
			// log the response
			auditor.serverResponse(log, response);
			auditor.writeLog(log);
			
			return; // we're done here
		}
		
		// see if we need to look for credentials
		if(PrincipalProviderResult.PRINCIPAL_NOT_FOUND == ppRes) {
			// attempt to get a credential from the HTTP request
			CredentialProviderResult credRes = credentialProvider.getCredentialFromHttpRequest(context);

			// see if we need to send the response back OR we couldn't find a credential
			if(CredentialProviderResult.SEND_RESPONSE == credRes ||
			   CredentialProviderResult.CREDENTIAL_NOT_FOUND == credRes) {
				// copy over the response
				HttpUtils.copyResponse(response, context);
				 
				// log the response
				auditor.serverResponse(log, response);
				auditor.writeLog(log);
				
				return; // we're done here
			}
			
			// log the fact that we got the creds
			auditor.credentialFound(log, context.getCredential());
			
			// we have a valid credential, see if it authenticates
			if(!authenticator.authenticate(context)) {
				// copy over the response
				HttpUtils.copyResponse(response, context);
				 
				// log the response
				auditor.authenticationFailed(log, response);
				auditor.writeLog(log);
				
				return; // we're done here
			}
			
			// see if the authenticator created the principal for us
			if(context.getPrincipal() == null)
				principalProvider.createPrincipal(context);

			// save our principal in the session store
			principalProvider.savePrincipalInSessionStore(context, sessionStore);
		} else {	// we found a principal
			auditor.principalFound(log, context.getPrincipal());
		}
		
		//
		// By here we have a valid principal in the context
		//
		
		// check to see if the principal is authorized
		if(!authorizer.authorize(context)) {
			// save the principal in the response even if not authorized, authenticated
			principalProvider.setPrincipalInHttpResponse(context);
			 
			// log that authz failed
			auditor.authorizationFailed(log, context.getHttpResponse());
			auditor.writeLog(log);
			
			return; // we're all done here
		}
			
		// modify the request to send to the server
		requestModifier.modifyRequest(context);
		
		// send the request to the server
		httpRequestor.request(context);
		
		// modify the response
		responseModifier.modifyResponse(context);
		
		// save the principal in the response
		principalProvider.setPrincipalInHttpResponse(context);
		 
		// copy over the response
		HttpUtils.copyResponse(response, context);
		 
		// log the response
		auditor.serverResponse(log, response);
		auditor.writeLog(log);
	}
}
