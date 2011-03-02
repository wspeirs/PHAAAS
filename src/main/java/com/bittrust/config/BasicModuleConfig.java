/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @class BasicModuleConfig
 */
public class BasicModuleConfig {

	private String className;
	private Map<String, List<String>> params = new HashMap<String, List<String>>();

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}
	
	/**
	 * Returns a list of values associated with the parameter name.
	 * @param param The parameter name, converted to lower case.
	 * @return A list of values associated with the parameter, or null if not found.
	 */
	public List<String> getParams(String param) {
		return params.get(param);
	}
	
	/**
	 * Gets a parameter from the configuration.
	 * @param param The parameter name, converted to lower case.
	 * @return The value of the parameter, or null if nothing is found.
	 */
	public String getParam(String param) {
		param = param.toLowerCase();
		List<String> list = params.get(param);
		
		if(list != null)
			return list.get(0);
		else
			return null;
	}
	
	/**
	 * Adds a parameter to the configuration. All param names are converted to lower case first.
	 * @param param The parameter name, converted to lower case. 
	 * @param value The value of the parameter.
	 */
	public void addParam(String param, String value) {
		param = param.toLowerCase();
		
		if(params.containsKey(param)) {
			params.get(param).add(value);
		} else {
			List<String> values = new LinkedList<String>();
			
			values.add(value);
			params.put(param, values);
		}
	}
}
