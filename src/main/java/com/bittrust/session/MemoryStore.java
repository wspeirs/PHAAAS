/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Principal;

/**
 * @class MemoryStore
 */
public class MemoryStore implements SessionStore {
	
	private Map<String, Principal> sessions = new HashMap<String, Principal>();
	
	public MemoryStore(BasicModuleConfig config) {
		;	// we don't do anything with this config
	}
	
	public synchronized String createSession(Principal principal) {
		String id = UUID.randomUUID().toString();	// generate a random UUID as the ID
		
		sessions.put(id, principal);	// add the session to the store
		
		return id;	// return the ID
	}

	public synchronized void storePrincipal(String sessionID, Principal principal) {
		sessions.put(sessionID, principal);
	}

	public Principal retrievePrincipal(String sessionID) {
		if(sessionID == null)
			return null;
		else
			return sessions.get(sessionID);
	}

	public synchronized void deleteSession(String sessionID) {
		sessions.remove(sessionID);
	}

	public boolean validateSession(String sessionID) {
		return sessions.containsKey(sessionID);
	}

}
