/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;


/**
 * Used to modify requests sent to backend servers.
 * @interface RequestModifier
 */
public interface RequestModifier {
	
	/**
	 * Modifies the request to the server which is in the context.
	 * 
	 * The modification usually means injecting headers and/or rewriting URLs.
	 * @param context The context of the request which includes the principal making the request.
	 */
	public void modifyRequest(PhaaasContext context);

}
