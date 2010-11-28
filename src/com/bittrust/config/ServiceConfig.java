/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

/**
 * @class ServiceConfig
 */
public class ServiceConfig {

	private String url = "*";
	private String host = "*";
	
	private BasicModuleConfig authenticationConfig = null;
	private BasicModuleConfig authorizationConfig = null;
	private BasicModuleConfig auditingConfig = null;
	private BasicModuleConfig sessionConfig = null;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public BasicModuleConfig getAuditingConfig() {
		return auditingConfig;
	}

	public void setAuditingConfig(BasicModuleConfig auditingConfig) {
		this.auditingConfig = auditingConfig;
	}

	public BasicModuleConfig getSessionConfig() {
		return sessionConfig;
	}

	public void setSessionConfig(BasicModuleConfig sessionConfig) {
		this.sessionConfig = sessionConfig;
	}
}
