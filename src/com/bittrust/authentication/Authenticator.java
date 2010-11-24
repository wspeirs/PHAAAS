/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import org.apache.http.HttpRequest;

/**
 * @interface Authenticator
 */
public interface Authenticator {

	public boolean authenticate(HttpRequest request);
}
