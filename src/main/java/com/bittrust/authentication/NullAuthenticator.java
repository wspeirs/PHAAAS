/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.HttpUtils.StatusCode;
import com.bittrust.http.PhaaasContext;

/**
 * @class NullAuthenticator
 * 
 * Always return either true or false.
 */
public class NullAuthenticator implements Authenticator {
	
	private final boolean result;
	
	public NullAuthenticator(BasicModuleConfig config) {
		this.result = Boolean.parseBoolean(config.getParam("result"));
	}
	
	/**
	 * Setup the authenticator to always return one result
	 * @param result The result to return;
	 */
	public NullAuthenticator(boolean result) {
		this.result = result;
	}

	public boolean authenticate(PhaaasContext context) {
		if(result) {
			context.setPrincipal(new Principal("test"));
		} else {
			HttpResponse response = HttpUtils.generateResponse(StatusCode.UNAUTHENTICATED, "Authentication Failed"); 
			
			// set the response in the context
			context.setHttpResponse(response);
		}
		
		return result;
	}
}
