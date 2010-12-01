/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import com.bittrust.credential.Principal;
import com.bittrust.http.PhaaasContext;

/**
 * @interface Authorizer
 */
public interface Authorizer {
	
	/**
	 * Attempts to authorize a request for a given principal.
	 * 
	 * The request that was made can be found in the PhaaasContext.
	 * The principal can be modified to include group information or properties.
	 * @param context The context of the authentication which includes the principal to authenticate.
	 * @return True if the principal is authorized, false otherwise.
	 */
	public boolean authorize(PhaaasContext context);

}
