/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.auditing;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class Verbose
 */
public class Verbose implements Auditor {

	private BasicModuleConfig config;
	
	public Verbose(BasicModuleConfig config) {
		this.config = config;
	}
	
	public Verbose() {
		
	}
}
