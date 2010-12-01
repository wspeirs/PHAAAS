/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import org.apache.http.HttpResponse;

/**
 * Used to modify the response from the server back to the client.
 * @interface ResponseModifier
 */
public interface ResponseModifier {

	/**
	 * Modifies the response from the server as it's sent to the client.
	 * @param context The context of the request.
	 * @return The new response to send to the client.
	 */
	public HttpResponse modifyResponse(PhaaasContext context);
}
