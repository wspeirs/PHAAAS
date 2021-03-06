/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.bittrust.PluginsLoader;
import com.bittrust.auditing.Auditor;
import com.bittrust.session.SessionStore;

/**
 * @class ServerConfig
 */
public class ServerConfig {
	
	// server specific settings
	private short port = 80;
	private int threadCount = 10;
	private ArrayList<ServiceConfig> serviceConfigs = new ArrayList<ServiceConfig>();
	private PluginsLoader loader;
	
	// global modules
	// these are shared across all services, unless one is specified for the service
	private Auditor auditor = null;
	private SessionStore sessionStore = null;
	
	// default modules
	// these are created for each individual service
	private BasicModuleConfig principalConfig = null;
	private BasicModuleConfig credentialConfig = null;
	private BasicModuleConfig authenticationConfig = null;
	private BasicModuleConfig authorizationConfig = null;
	private BasicModuleConfig requestConfig = null;
	private BasicModuleConfig responseConfig = null;

	public ServerConfig(PluginsLoader pl) {
		loader = pl;
	}
	
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
		
		// add in the default modules if not specified
		if(principalConfig != null && serviceConfig.getPrincipalConfig() == null)
			serviceConfig.setPrincipalConfig(principalConfig);
		
		if(credentialConfig != null && serviceConfig.getCredentialConfig() == null)
			serviceConfig.setCredentialConfig(credentialConfig);
		
		if(authenticationConfig != null && serviceConfig.getAuthenticationConfig() == null)
			serviceConfig.setAuthenticationConfig(authenticationConfig);
		
		if(authorizationConfig != null && serviceConfig.getAuthorizationConfig() == null)
			serviceConfig.setAuthorizationConfig(authorizationConfig);
		
		if(requestConfig != null && serviceConfig.getRequestConfig() == null)
			serviceConfig.setRequestConfig(requestConfig);
		
		if(responseConfig != null && serviceConfig.getResponseConfig() == null)
			serviceConfig.setResponseConfig(responseConfig);
		
		this.serviceConfigs.add(serviceConfig);
	}
	
	public PluginsLoader getLoader() {
		return loader;
	}

	public Auditor getAuditor() {
		return auditor;
	}

	public SessionStore getSessionStore() {
		return sessionStore;
	}

	public BasicModuleConfig getPrincipalConfig() {
		return principalConfig;
	}

	public void setPrincipalConfig(BasicModuleConfig principalConfig) {
		this.principalConfig = principalConfig;
	}

	public BasicModuleConfig getCredentialConfig() {
		return credentialConfig;
	}

	public void setCredentialConfig(BasicModuleConfig credentialConfig) {
		this.credentialConfig = credentialConfig;
	}

	public BasicModuleConfig getAuthenticationConfig() {
		return authenticationConfig;
	}

	public void setAuthenticationConfig(BasicModuleConfig authenticationConfig) {
		this.authenticationConfig = authenticationConfig;
	}

	public BasicModuleConfig getAuthorizationConfig() {
		return authorizationConfig;
	}

	public void setAuthorizationConfig(BasicModuleConfig authorizationConfig) {
		this.authorizationConfig = authorizationConfig;
	}

	public BasicModuleConfig getRequestConfig() {
		return requestConfig;
	}

	public void setRequestConfig(BasicModuleConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	public BasicModuleConfig getResponseConfig() {
		return responseConfig;
	}

	public void setResponseConfig(BasicModuleConfig responseConfig) {
		this.responseConfig = responseConfig;
	}

	public void setServiceConfigs(ArrayList<ServiceConfig> serviceConfigs) {
		this.serviceConfigs = serviceConfigs;
	}

	public void setSessionConfig(BasicModuleConfig sessionConfig) {
		try {
			@SuppressWarnings("unchecked")
			Class<SessionStore> sessionClass = (Class<SessionStore>) loader.loadClass(sessionConfig.getClassName());
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
			@SuppressWarnings("unchecked")
			Class<Auditor> auditClass = (Class<Auditor>) loader.loadClass(auditConfig.getClassName());
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
