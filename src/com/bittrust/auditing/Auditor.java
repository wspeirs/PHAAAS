/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.auditing;

import java.io.File;
import java.net.InetAddress;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;

/**
 * @class Auditor
 */
public interface Auditor {
	
	public File getLogLocation();
	public void setLogLocation(File file);

	/**
	 * Log when we receive a connection from a host.
	 * @param address The address of the host that connected.
	 * @return The StringBuilder containing the log thus far.
	 */
	public StringBuilder receivedConnection(InetAddress address);

	/**
	 * Log when a request is received.
	 * @param sb The log message.
	 * @param request The request.
	 * @return The StringBuilder containing the log thus far.
	 */
	public void receivedRequest(StringBuilder sb, HttpRequest request);
	
	/**
	 * Log that we got a credential from the request.
	 * @param sb The log message.
	 * @param cred The credential.
	 */
	public void credentialFound(StringBuilder sb, Credential cred);
	
	/**
	 * Log that we found a principal (or created a new one).
	 * @param sb The log message.
	 * @param principal The principal.
	 */
	public void principalFound(StringBuilder sb, Principal principal);
	
	/**
	 * Log the fact that authentication failed.
	 * @param sb The log message.
	 * @param response The response to be sent back to the client.
	 */
	public void authenticationFailed(StringBuilder sb, HttpResponse response);
	
	/**
	 * Log the fact that authorization failed.
	 * @param sb The log message.
	 * @param response The response to be sent back to the client.
	 */
	public void authorizationFailed(StringBuilder sb, HttpResponse response);
	
	/**
	 * Log the response from a server.
	 * @param sb The StringBuilder to use.
	 * @param response The response from the server.
	 */
	public void serverResponse(StringBuilder sb, HttpResponse response);
	
	/**
	 * Actually writes the log out.
	 */
	public void writeLog(StringBuilder sb);
}
