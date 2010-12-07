/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import java.util.Map;

import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.HttpUtils.StatusCode;
import com.bittrust.http.PhaaasContext;

/**
 * @class NullAuthorizer
 */
public class NullAuthorizer implements Authorizer {

	private BasicModuleConfig config;
	private final boolean result;
	
	public NullAuthorizer(BasicModuleConfig config) {
		this.config = config;
		this.result = Boolean.parseBoolean(this.config.getParam("result"));
	}
	
	/**
	 * Setup the authorizer to always return one result.
	 * @param result The result to return.
	 */
	public NullAuthorizer(boolean result) {
		this.result = result;
	}
	
	@Override
	public boolean authorize(PhaaasContext context) {
		if(result == false) {
			HttpResponse response = HttpUtils.generateResponse(StatusCode.UNAUTHORIZED, "Authorization Failed");
			
			// set the response in the context
			context.setHttpResponse(response);
		} else {
			Credential cred = context.getCredential();
			Principal principal = new Principal(cred.getUserName(), cred.getProperties());
			
			context.setPrincipal(principal);
		}
		
		return result;
	}
}
