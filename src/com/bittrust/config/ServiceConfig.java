/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

/**
 * @class ServiceConfig
 */
public class ServiceConfig {

	private String url = "*";
	private BasicModuleConfig authenticationConfig = null;
	private BasicModuleConfig authorizationConfig = null;
	private BasicModuleConfig auditingConfig = null;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
}
