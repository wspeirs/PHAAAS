/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;


/**
 * Used to modify the response from the server back to the client.
 * @interface ResponseModifier
 */
public interface ResponseModifier {

	/**
	 * Modifies the response in the context from the server as it's sent to the client.
	 * @param context The context of the request.
	 */
	public void modifyResponse(PhaaasContext context);
}
