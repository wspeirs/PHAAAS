/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import com.bittrust.config.BasicModuleConfig;

/**
 * @interface Authorizer
 */
public interface Authorizer {
	
	public boolean authorize(HttpRequest request);

	public void authorizationFailed(HttpRequest request, HttpResponse response, HttpContext context);

}
