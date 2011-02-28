/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.config;

import com.bittrust.auditing.Auditor;
import com.bittrust.session.SessionStore;

/**
 * @class ServiceConfig
 */
public class ServiceConfig {

	private String url = "*";
	private String host = "*";
	
	private BasicModuleConfig principalConfig = null;
	private BasicModuleConfig credentialConfig = null;
	private BasicModuleConfig authenticationConfig = null;
	private BasicModuleConfig authorizationConfig = null;
	private BasicModuleConfig requestConfig = null;
	private BasicModuleConfig responseConfig = null;
	
	private Auditor auditor = null;
	private SessionStore sessionStore = null;

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

	public Auditor getAuditor() {
		return auditor;
	}

	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	public SessionStore getSessionStore() {
		return sessionStore;
	}

	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

}
