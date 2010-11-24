/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authorization;

import org.apache.http.HttpRequest;

/**
 * @interface Authorizer
 */
public interface Authorizer {
	public boolean authorize(HttpRequest request);
}
