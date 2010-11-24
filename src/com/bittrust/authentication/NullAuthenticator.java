/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import org.apache.http.HttpRequest;

/**
 * @class NullAuthenticator
 * 
 * Always return either true or false.
 */
public class NullAuthenticator implements Authenticator {
	
	private final boolean result;
	
	/**
	 * Setup the authenticator to always return one result
	 * @param result The result to return;
	 */
	public NullAuthenticator(boolean result) {
		this.result = result;
	}

	@Override
	public boolean authenticate(HttpRequest request) {
		return result;
	}

}
