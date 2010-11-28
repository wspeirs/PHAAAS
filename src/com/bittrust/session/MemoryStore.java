/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class MemoryStore
 */
public class MemoryStore implements SessionStore {
	
	private Map<String, String> sessions = new HashMap<String, String>();
	
	public MemoryStore(BasicModuleConfig config) {
		;	// we don't do anything with this config
	}
	
	@Override
	public synchronized String createSession() {
		String id = UUID.randomUUID().toString();	// generate a random UUID as the ID
		
		sessions.put(id, "");	// add the session to the store
		
		return id;	// return the ID
	}

	@Override
	public synchronized void storeMetaData(String sessionID, String metaData) {
		sessions.put(sessionID, metaData);
	}

	@Override
	public String retrieveMetaData(String sessionID) {
		return sessions.get(sessionID);
	}

	@Override
	public synchronized void deleteSession(String sessionID) {
		sessions.remove(sessionID);
	}

	@Override
	public boolean validateSession(String sessionID) {
		return sessions.containsKey(sessionID);
	}

}
