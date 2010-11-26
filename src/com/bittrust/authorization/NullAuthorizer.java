/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class NullAuthorizer
 */
public class NullAuthorizer implements Authorizer {

	private BasicModuleConfig config;
	private final boolean result;
	
	public NullAuthorizer(BasicModuleConfig config) {
		this.config = config;
		this.result = Boolean.parseBoolean(config.getParam("result"));
	}
	
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

	@Override
	public void authorizationFailed(HttpRequest request, HttpResponse response,	HttpContext context) {
		try {
			StringEntity entity = new StringEntity("Authorization Failed");
			
			response.setHeader("Content-Length", entity.getContentLength()+"");
			response.setEntity(entity);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

}
