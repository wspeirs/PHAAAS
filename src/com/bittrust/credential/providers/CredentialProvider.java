/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import com.bittrust.http.PhaaasContext;

/**
 * @interface CredentialProvider
 */
public interface CredentialProvider {
	
	/**
	 * The result type for a CredentialProvider.
	 * 
	 * Determines the course of action for the RequestHander.
	 * @enum CredentialProviderResult
	 */
	enum CredentialProviderResult {
		CREDENTIAL_FOUND,		/** A credential was found in the request. */
		CREDENTIAL_NOT_FOUND,	/** A credential was <b>NOT</b> found in the request. */
		SEND_RESPONSE			/** A response should be sent to the client for more interaction. */
	}
	
	/**
	 * Create a credential based on an HttpRequest.
	 * 
	 * If a credential can be found in the request, then construct it, set it in the context, and return CREDENTIAL_FOUND.
	 * If more interaction is required with the client to obtain a credential, then set the HttpResponse in the context properly, and return SEND_RESPONSE.
	 * If a credential cannot be found, and no more interaction is required, then return CREDENTIAL_NOT_FOUND.
	 * @param context The PHAAAS context used to get the request and possibly set the credential or response.
	 * @return CREDENTIAL_FOUND if a credential was found in the request, CREDENTIAL_NOT_FOUND if a credential was not found, and SEND_RESPONSE if a response should be sent back to the client.
	 */
	public CredentialProviderResult getCredentialFromHttpRequest(PhaaasContext context);
}
