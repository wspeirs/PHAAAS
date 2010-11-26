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
	private int threadCount = 10;
	private ArrayList<ServiceConfig> serviceConfigs = new ArrayList<ServiceConfig>();
	
	public void setPort(short port) {
		this.port = port;
	}
	
	public short getPort() {
		return port;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public ArrayList<ServiceConfig> getServiceConfigs() {
		return serviceConfigs;
	}

	public void addServiceConfig(ServiceConfig serviceConfig) {
		this.serviceConfigs.add(serviceConfig);
	}
}
