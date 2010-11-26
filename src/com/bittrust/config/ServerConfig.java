/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

import java.util.ArrayList;

/**
 * @class ServerConfig
 */
public class ServerConfig {
	
	private short port = 80;
	private ArrayList<ServiceConfig> serviceConfigs = new ArrayList<ServiceConfig>();
	
	public void setPort(short port) {
		this.port = port;
	}
	
	public short getPort() {
		return port;
	}

	public ArrayList<ServiceConfig> getServiceConfigs() {
		return serviceConfigs;
	}

	public void addServiceConfig(ServiceConfig serviceConfig) {
		this.serviceConfigs.add(serviceConfig);
	}
}
