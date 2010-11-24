/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * @interface HttpRequestor
 */
public interface HttpRequestor {

	public HttpResponse request(HttpRequest request, HttpContext context);
}
