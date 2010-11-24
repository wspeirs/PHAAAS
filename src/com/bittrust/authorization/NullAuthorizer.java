/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import org.apache.http.HttpRequest;

/**
 * @class NullAuthorizer
 */
public class NullAuthorizer implements Authorizer {

	private final boolean result;
	
	/**
	 * Setup the authorizer to always return one result.
	 * @param result The result to return.
	 */
	public NullAuthorizer(boolean result) {
		this.result = result;
	}
	
	@Override
	public boolean authorize(HttpRequest request) {
		return result;
	}

}
