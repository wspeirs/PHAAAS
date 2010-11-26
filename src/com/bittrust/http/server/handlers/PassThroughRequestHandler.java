/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import com.bittrust.auditing.Verbose;
import com.bittrust.authentication.NullAuthenticator;
import com.bittrust.authorization.NullAuthorizer;


/**
 * @class PassThroughRequestHandler
 * 
 * Handle unauthenticated requests.
 */
public class PassThroughRequestHandler extends AbstractRequestHandler {

	private final static NullAuthenticator authenticator = new NullAuthenticator(true);
	private final static NullAuthorizer authorizor = new NullAuthorizer(true);
	private final static Verbose auditor = new Verbose();
	
	public PassThroughRequestHandler() {
		super(null,
			  PassThroughRequestHandler.authenticator,
			  PassThroughRequestHandler.authorizor,
			  PassThroughRequestHandler.auditor);
	}

}
