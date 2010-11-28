/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

/**
 * @interface Authenticator
 */
public interface Authenticator {

	/**
	 * Attempt to authenticate a user.
	 * @param request The HTTP request.
	 * @return True if the user is authenticated, false otherwise.
	 */
	public boolean authenticate(HttpRequest request, JSONObject sessionMetaData);
	
	/**
	 * Given a request, return the user trying to authenticate.
	 * @param request The HTTP request. 
	 * @return The user attempting to authenticate.
	 */
	public String getUser(HttpRequest request);
	
	/**
	 * Generate the proper response given a failed authentication.
	 * @param request The HTTP request.
	 * @param response The response to send back to the client.
	 * @param context The context.
	 */
	public void authenticationFailed(HttpRequest request, HttpResponse response, HttpContext context);

}
