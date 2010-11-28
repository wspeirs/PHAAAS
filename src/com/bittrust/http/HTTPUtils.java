/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;

/**
 * @class HTTPUtils
 */
public class HTTPUtils {
	/**
	 * Go through the headers getting the requested header or null if not found.
	 * @param request The request to search.
	 * @param headerName The name of the header to find.
	 * @return The value of the header, or null if not found.
	 */
	public static String getHeader(HttpRequest request, String headerName) {
		HeaderIterator iterator = request.headerIterator();
		String ret = null;

		while(iterator.hasNext()) {
			Header h = iterator.nextHeader();
			
			if(h.getName().equalsIgnoreCase(headerName)) {
				
				ret = h.getValue();
				
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Returns the value of a cookie, given its name.
	 * @param cookieName The name of the cookie to find
	 * @return The value of the cookie or null if the cookie is not found.
	 */
	public static String getCookie(HttpRequest request, String cookieName) {
		String ret = null;
		String cookieHeader = getHeader(request, "Cookie");
		
		// if we cannot find the header, then cannot find the cookie
		if(cookieHeader == null)
			return ret;

		StringTokenizer tokenizer = new StringTokenizer(cookieHeader, "; ");

		// go through all the tokens splitting by ;
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			
			// make sure the token starts with cookieName=
			if(token.startsWith(cookieName + "=")) {
				tokenizer = new StringTokenizer(token, "=");
				
				tokenizer.nextToken();	// this cookieName
				ret = tokenizer.nextToken().replaceAll("\"", "");	// this is the value
				break;
			}
		}
		
		return ret;
	}

}
