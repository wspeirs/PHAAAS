/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import org.apache.http.HttpRequest;

/**
 * Used to modify requests sent to backend servers.
 * @interface RequestModifier
 */
public interface RequestModifier {
	
	/**
	 * Modifies the request to the server.
	 * 
	 * The modification usually means injecting headers and/or rewriting URLs.
	 * @param context The context of the request which includes the principal making the request.
	 * @return The new request to be made.
	 */
	public HttpRequest modifyRequest(PhaaasContext context);

}
