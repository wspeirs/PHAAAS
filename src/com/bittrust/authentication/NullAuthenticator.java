/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

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

	@Override
	public Principal authenticate(PhaaasContext context) {
		Principal ret = null;
		
		if(result) {
			ret = new Principal("test");
		} else {
			try {
			HttpResponse response = HttpUtils.generateResponse(StatusCode.UNAUTHENTICATED);
			StringEntity entity = new StringEntity("Authentication Failed");
			
			response.setHeader("Content-Length", entity.getContentLength()+"");
			response.setEntity(entity);
			
			// set the response in the context
			context.setHttpResponse(response);
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
