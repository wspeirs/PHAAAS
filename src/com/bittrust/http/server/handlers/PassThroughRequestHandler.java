/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import com.bittrust.authentication.NullAuthenticator;
import com.bittrust.authorization.NullAuthorizer;
import com.bittrust.http.client.BasicHttpRequestor;


/**
 * @class PassThroughRequestHandler
 * 
 * Handle unauthenticated requests.
 */
public class PassThroughRequestHandler extends AbstractRequestHandler {

	private final static NullAuthenticator authenticator = new NullAuthenticator(true);
	private final static NullAuthorizer authorizor = new NullAuthorizer(true);
	private final static BasicHttpRequestor requestor = new BasicHttpRequestor();
	
	public PassThroughRequestHandler() {
		super(null,
			  PassThroughRequestHandler.authenticator,
			  PassThroughRequestHandler.authorizor,
			  PassThroughRequestHandler.requestor);
	}

}
