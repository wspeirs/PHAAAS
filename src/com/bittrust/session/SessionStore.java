/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.session;

/**
 * @interface SessionStore
 * 
 * Handles all sessioning
 */
public interface SessionStore {

	/**
	 * Creates a new session and returns the session ID.
	 * @return The newly created session ID.
	 */
	public String createSession();
	
	/**
	 * Given a session ID validates that it valid and active.
	 * @param sessionID The session ID to validate.
	 * @return True if the session is valid and active, false otherwise.
	 */
	public boolean validateSession(String sessionID);
	
	/**
	 * Stores meta data about a session.
	 * @param sessionID The session ID to store the associated meta data.
	 * @param metaData The meta data to store with the session.
	 */
	public void storeMetaData(String sessionID, String metaData);
	
	/**
	 * Retrieves previously stored meta data.
	 * @param sessionID The session ID to retrieve meta data for.
	 * @return The associated meta data.
	 */
	public String retrieveMetaData(String sessionID);
	
	/**
	 * Deletes a session and all associated meta data.
	 * @param sessionID The session to delete.
	 */
	public void deleteSession(String sessionID);
}
