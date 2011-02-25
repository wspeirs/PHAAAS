/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.session;

import com.bittrust.credential.Principal;

/**
 * @interface SessionStore
 * 
 * Handles all sessioning
 */
public interface SessionStore {
	
	/**
	 * Creates a new session storing the associated principal and returning the session ID.
	 * @param principal The principal which is stored in the session.
	 * @return The newly created session ID.
	 */
	public String createSession(Principal principal);
	
	/**
	 * Given a session ID validates that it valid and active.
	 * @param sessionID The session ID to validate.
	 * @return True if the session is valid and active, false otherwise.
	 */
	public boolean validateSession(String sessionId);
	
	/**
	 * Stores a principal in a session.
	 * @param sessionID The session ID to store the Principal.
	 * @param principal The Principal to store in the session.
	 */
	public void storePrincipal(String sessionId, Principal principal);
	
	/**
	 * Retrieves previously stored Principal.
	 * @param sessionID The session ID of the Principal to retrieve.
	 * @return The stored Principal or null if one is not found.
	 */
	public Principal retrievePrincipal(String sessionId);
	
	/**
	 * Deletes a session for a Principal
	 * @param sessionID The session to delete.
	 */
	public void deleteSession(String sessionId);
}
