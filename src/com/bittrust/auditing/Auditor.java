/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.auditing;

import java.io.File;
import java.net.InetAddress;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

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
	 * Log when we receive a connection from a host.
	 * @param sb The StringBuilder to use.
	 * @param address The address of the host that connected.
	 */
	public void receivedConnection(StringBuilder sb, InetAddress address);
	
	/**
	 * Log when a request is received.
	 * @param request The request.
	 * @return The StringBuilder containing the log thus far.
	 */
	public StringBuilder receivedRequest(HttpRequest request, String user);
	
	/**
	 * Log when a request is received with a pre-made StringBuilder.
	 * @param sb The StringBuilder to use.
	 * @param request The request.
	 */
	public void receivedRequest(StringBuilder sb, HttpRequest request, String user);
	
	/**
	 * Log a failed authentication.
	 * @param sb The StringBuilder to use.
	 * @param user The user who failed auth.
	 */
	public void authenticationFailed(StringBuilder sb, String user);
	
	/**
	 * Log a failed authenorization.
	 * @param sb The StringBuilder to use.
	 * @param user The user who failed authz.
	 */
	public void authorizationFailed(StringBuilder sb, String user);
	
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
