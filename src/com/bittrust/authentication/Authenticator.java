/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import com.bittrust.credential.Principal;
import com.bittrust.http.PhaaasContext;

/**
 * @interface Authenticator
 */
public interface Authenticator {

	/**
	 * Attempts to authenticate a user generating a principal upon successful authentication.
	 * 
	 * If the user is unsuccessfully authenticated then a proper response should be placed in
	 * the context and the method should return null.
	 * @param context The context of the authentication with contains the credential to authenticate.
	 * @return The principal upon successful authentication, null upon unsuccessful authentication.
	 */
	public Principal authenticate(PhaaasContext context);
	
}
