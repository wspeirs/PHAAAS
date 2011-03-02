/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @class NullProvider
 * Checks for an existing principal or creates a bogus credential.
 */
public class NullProvider implements PrincipalProvider, CredentialProvider {

	private String username;
	
	public NullProvider(BasicModuleConfig config) {
		this.username = config.getParam("username");
	}
	
	/**
	 * Create a bogus credential using the username passed in the config.
	 */
	public CredentialProviderResult getCredentialFromHttpRequest(PhaaasContext context) {
		// create & store a bogus credential
		context.setCredential(new Credential(username, new HashMap<String, String>()));

		return CredentialProviderResult.CREDENTIAL_FOUND;
	}

	@Override
	public PrincipalProviderResult getPrincipalFromHttpRequest(PhaaasContext context, SessionStore sessionStore) {
		String sessionID = HttpUtils.getCookie(context.getHttpRequest(), PrincipalProvider.SESSION_COOKIE);
		
		// if we don't have an ID, we don't have a principal
		if(sessionID == null)
			return PrincipalProviderResult.PRINCIPAL_NOT_FOUND;
		
		context.setSessionId(sessionID);	// save the session ID in the context
		
		// try to find the principal in the session store
		Principal principal = sessionStore.retrievePrincipal(sessionID);
		
		if(principal == null) {
			return PrincipalProviderResult.PRINCIPAL_NOT_FOUND;
		} else { // create a bogus principal
			context.setSessionId(sessionID);	// set the session ID
			context.setPrincipal(principal);	// store the principal
			return PrincipalProviderResult.PRINCIPAL_FOUND;
		}
	}

	@Override
	public void savePrincipalInSessionStore(PhaaasContext context, SessionStore sessionStore) {
		sessionStore.storePrincipal(context.getSessionId(), context.getPrincipal());
	}

	@Override
	public void setPrincipalInHttpResponse(PhaaasContext context) {
		HttpResponse response = context.getHttpResponse();
		String host = HttpUtils.getHeader(context.getHttpRequest(), "Host");
		
		// set the cookie for the current session only
		HttpUtils.setCookie(response, host, -1, PrincipalProvider.SESSION_COOKIE, context.getSessionId());
	}

	@Override
	public void createPrincipal(PhaaasContext context) {
		Credential credential = context.getCredential();
		
		context.setSessionId(UUID.randomUUID().toString());	// generate a random UUID as the ID
		context.setPrincipal(new Principal(credential.getUsername())); // create the new Principal
	}

}
