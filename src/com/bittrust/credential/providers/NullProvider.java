/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import java.util.HashMap;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @class NullProvider
 * Checks for an existing session or creates a bogus credential.
 */
public class NullProvider implements CredentialProvider {

	private String username;
	
	public NullProvider(BasicModuleConfig config) {
		this.username = config.getParam("username");
	}
	
	@Override
	public CredentialProviderResult getCredentialOrPrincipal(SessionStore sessionStore, PhaaasContext context) {
		String sessionID = HttpUtils.getCookie(context.getHttpRequest(), SessionStore.SESSION_COOKIE);
		Principal principal = sessionStore.retrievePrincipal(sessionID);
		
		// create a bogus credential
		if(principal == null) {
			context.setCredential(new Credential(username, new HashMap<String, String>()));
			return CredentialProviderResult.CREDENTIAL_FOUND;
		} else { // create a bogus principal
			context.setSessionId(sessionID);	// set the session ID
			context.setPrincipal(principal);	// store the principal
			return CredentialProviderResult.PRINCIPAL_FOUND;
		}
	}

}
