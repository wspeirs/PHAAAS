/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @interface PrincipalProvider
 */
public interface PrincipalProvider {
	
	public static final String SESSION_COOKIE = "PHAAAS_ID"; /** The cookie used to store the session ID */
	
	/**
	 * The result type for a CredentialProvider.
	 * 
	 * Determines the course of action for the RequestHander.
	 * @enum CredentialProviderResult
	 */
	enum PrincipalProviderResult {
		PRINCIPAL_FOUND,		/** A principal was found. */
		PRINCIPAL_NOT_FOUND,	/** A principal was <b>NOT</b> found. */
		SEND_RESPONSE			/** A response should be sent to the client for more interaction. */
	}

	/**
	 * Create a principal based on an HttpRequest.
	 * 
	 * If a principal can be found, then construct it, set it in the context, and return PRINCIPAL_FOUND.
	 * If more interaction is required with the client to obtain a principal, then set the HttpResponse in the context properly, and return SEND_RESPONSE.
	 * If a principal cannot be found, and no more interaction is required, then return PRINCIPAL_NOT_FOUND.
	 * @param context The PHAAAS context used to get the request and possibly set the principal or response.
	 * @param sessionStore The session store where principals are stored.
	 * @return PRINCIPAL_FOUND if a principal was found, PRINCIPAL_NOT_FOUND if a principal was not found, and SEND_RESPONSE if a response should be sent back to the client.
	 */
	public PrincipalProviderResult getPrincipalFromHttpRequest(PhaaasContext context, SessionStore sessionStore);
	
	/**
	 * Store the principal in the context in the SessionStore.
	 * 
	 * It is at the discretion of the PrincipalProvider on how the principal should be stored in the session store. The only
	 * requirement is that the PrincipalProvider must be able to retrieve the principal from the session store at a later time.
	 * @param context The PHAAAS context which contains the principal to store in the session store.
	 * @param sessionStore The session store used to store the principal.
	 */
	public void savePrincipalInSessionStore(PhaaasContext context, SessionStore sessionStore);
	
	/**
	 * Create a principal from the credential in the context.
	 * @param context The PHAAAS context which holds the credential and resulting principal.
	 */
	public void createPrincipal(PhaaasContext context);
	
	/**
	 * Store the principal in the context in the HttpResponse in the context.
	 * 
	 * It is at the discretion of the PrincipalProvider on how the principal should be stored in the HttpResponse. The only
	 * requirement is that the PrincipalProvider, given an HttpRequest after the HttpResponse is set, should be able to
	 * retrieve the principal.
	 * @param context The PHAAAS context which contains the principal and HttpResponse.
	 */
	public void setPrincipalInHttpResponse(PhaaasContext context);

}
