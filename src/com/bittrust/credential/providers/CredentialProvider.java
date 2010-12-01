/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @interfacC CredentialProvider
 */
public interface CredentialProvider {
	
	enum CredentialProviderResult {
		CREDENTIAL_FOUND,
		PRINCIPAL_FOUND,
		SEND_RESPONSE
	}
	
	/**
	 * Create a credential or principal based on an HttpRequest.
	 * 
	 * If a credential can be found in the request, construct it and set it in the context.
	 * If a principal can be constructed (possibly from a saved session), construct it and set it in the context.
	 * If a neither can be constructed, then set a proper result in the context.
	 * Also, if multiple requests/responses are needed, simply set the result in the context.  
	 * @param sessonStore The session store to use to lookup a principal
	 * @param context The PHAAAS context used to get the request and set the credential, principal or response.
	 * @return CREDENTIAL_FOUND if a credential was found in the request, PRINCIPAL_FOUND if a principal could be constructed, SEND_RESPONSE if a response should be sent back to the client.
	 */
	public CredentialProviderResult getCredentialOrPrincipal(SessionStore sessionStore, PhaaasContext context);
}
