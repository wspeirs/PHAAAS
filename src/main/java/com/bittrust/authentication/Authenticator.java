/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import com.bittrust.http.PhaaasContext;

/**
 * @interface Authenticator
 */
public interface Authenticator {

	/**
	 * Attempts to authenticate a user generating a principal upon successful authentication.
	 * 
	 * If the user successfully authenticates, then a principal is created and placed in the context.
	 * If the user unsuccessfully authenticates, then a response is placed in the context.
	 * @param context The context of the authentication which contains the credential to authenticate.
	 * @return True if the user successfully authenticates, false otherwise.
	 */
	public boolean authenticate(PhaaasContext context);
	
}
