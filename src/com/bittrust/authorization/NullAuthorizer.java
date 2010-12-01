/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import com.bittrust.config.BasicModuleConfig;
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
	public boolean authorize(PhaaasContext context) {
		if(result == false) {
			try {
			HttpResponse response = HttpUtils.generateResponse(StatusCode.UNAUTHORIZED);
			StringEntity entity = new StringEntity("Authorization Failed");
			
			response.setHeader("Content-Length", entity.getContentLength()+"");
			response.setEntity(entity);
			
			// set the response in the context
			context.setHttpResponse(response);
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
