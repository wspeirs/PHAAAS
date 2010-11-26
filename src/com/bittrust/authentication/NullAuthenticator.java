/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import com.bittrust.config.BasicModuleConfig;

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
	public boolean authenticate(HttpRequest request) {
		return result;
	}

	@Override
	public void authenticationFailed(HttpRequest request, HttpResponse response, HttpContext context) {
		try {
			StringEntity entity = new StringEntity("Authentication Failed");
			
			response.setHeader("Content-Length", entity.getContentLength()+"");
			response.setEntity(entity);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
