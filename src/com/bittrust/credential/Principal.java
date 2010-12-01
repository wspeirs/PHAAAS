/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @class Principal
 */
public class Principal {
	
	private String username;
	private Set<String> groups;
	private Map<String, Object> properties;
	
	public Principal(String username) {
		this.username = username;
		this.groups = new HashSet<String>();
	}

	/**
	 * Returns the username of the principal.
	 * @return The username for the principal.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Get an array of the groups this principal is a member of.
	 * @return An array of groups.
	 */
	public String[] getGroups() {
		return groups.toArray(new String[0]);
	}
	
	/**
	 * Adds a single group to the Principal.
	 * @param group The group to add.
	 */
	public void addGroup(String group) {
		groups.add(group);
	}
	
	/**
	 * Add a collection of groups to the Principal.
	 * @param groups The groups to add.
	 */
	public void addGroups(Collection<String> groups) {
		this.groups.addAll(groups);
	}
	
	/**
	 * Checks to see if the principal is a member of the given group.
	 * @param group The group to check membership of.
	 * @return True if the principal is a member of the group, false otherwise.
	 */
	public boolean isMemberOf(String group) {
		return groups.contains(group);
	}
	
	/**
	 * Sets a property for the user.
	 * @param name The name of the property.
	 * @param value The value of the property.
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}
	
	/**
	 * Gets an arbitrary property about the principal.
	 * @param property The name of the property.
	 * @return The property.
	 */
	public Object getProperty(String property) {
		return properties.get(property);
	}
	
	public String serialize() {
		//TODO: implement me
		return null;
	}
	
	public static Principal deserialize(String principal) {
		return null;
	}
}
