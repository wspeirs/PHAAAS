/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential;

import java.util.HashMap;
import java.util.Map;

/**
 * An immutable credential for a certain principal.
 * @class Credential
 */
public class Credential {
	
	private String username;
	private Map<String, String> properties;
	
	public Credential(String username, Map<String, String> properties) {
		this.username = username;
		this.properties = properties;
	}
	
	/**
	 * Return the username for this credential.
	 * @return The username for this credential.
	 */
	public String getUserName() {
		return username;
	}
	
	/**
	 * Returns a given property about the credential.
	 * @param property The property about being requested.
	 * @return The value of the property or null if the property doesn't exist.
	 */
	public String getProperty(String property) {
		return properties.get(property);
	}
	
	/**
	 * Returns a copy of the properties.
	 * @return A copy of the properties.
	 */
	public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);	// we want this to be immutable
	}
}
