/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import com.bittrust.http.PhaaasContext;

/**
 * @interface HttpRequestor
 */
public interface HttpRequestor {

	/**
	 * Makes the request found in the context and set the response in the context.
	 * @param context The context for which the HTTP request should operate in.
	 */
	public void request(PhaaasContext context);
}
