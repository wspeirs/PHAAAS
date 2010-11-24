/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * @interface Authenticator
 */
public interface Authenticator {

	public boolean authenticate(HttpRequest request);
	
	public void authenticationFailed(HttpRequest request, HttpResponse response, HttpContext context);

}
