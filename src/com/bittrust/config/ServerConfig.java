/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.bittrust.auditing.Auditor;
import com.bittrust.session.SessionStore;

/**
 * @class ServerConfig
 */
public class ServerConfig {
	
	private short port = 80;
	private int threadCount = 10;
	private ArrayList<ServiceConfig> serviceConfigs = new ArrayList<ServiceConfig>();
	
	// modules
	private Auditor auditor = null;
	private SessionStore sessionStore = null;

	
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
		// add in the global auditor & sessionStore if not specified
		if(serviceConfig.getAuditor() == null)
			serviceConfig.setAuditor(auditor);
		
		if(serviceConfig.getSessionStore() == null)
			serviceConfig.setSessionStore(sessionStore);
		
		this.serviceConfigs.add(serviceConfig);
	}
	
	public void setSessionConfig(BasicModuleConfig sessionConfig) {
		try {
			Class<SessionStore> sessionClass = (Class<SessionStore>) Class.forName(sessionConfig.getClassName());
			this.sessionStore = (SessionStore)sessionClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(sessionConfig);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void setAuditConfig(BasicModuleConfig auditConfig) {
		try {
			Class<Auditor> auditClass = (Class<Auditor>) Class.forName(auditConfig.getClassName());
			this.auditor = (Auditor)auditClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(auditConfig);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
