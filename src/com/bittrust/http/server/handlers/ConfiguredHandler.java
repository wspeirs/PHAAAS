/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.lang.reflect.InvocationTargetException;

import com.bittrust.auditing.Auditor;
import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.config.BasicModuleConfig;
import com.bittrust.config.ServiceConfig;

/**
 * @class ConfiguredHandler
 * 
 * The authentication, authorization, and auditing modules are set via the configuration file.
 */
public class ConfiguredHandler extends AbstractRequestHandler {

	private ServiceConfig serviceConfig = null;
	
	public ConfiguredHandler(ServiceConfig serviceConfig) throws Exception {
		this.serviceConfig = serviceConfig;
		
		try {
			// setup the authenticator
			Class<Authenticator> authClass = (Class<Authenticator>) Class.forName(serviceConfig.getAuthenticationConfig().getClassName());
			Authenticator auth = (Authenticator)authClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getAuthenticationConfig());
			this.setAuthenticator(auth);

			// setup the authorizer
			Class<Authorizer> authzClass = (Class<Authorizer>) Class.forName(serviceConfig.getAuthorizationConfig().getClassName());
			Authorizer authz = (Authorizer)authzClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getAuthorizationConfig());
			this.setAuthorizer(authz);

			// setup the auditor
			Class<Auditor> auditClass = (Class<Auditor>) Class.forName(serviceConfig.getAuditingConfig().getClassName());
			Auditor audit = (Auditor)auditClass.getConstructor(new Class[] { BasicModuleConfig.class }).newInstance(serviceConfig.getAuditingConfig());
			this.setAuditor(audit);

		} catch (ClassNotFoundException e) {
			System.err.println("COULD NOT FIND THE CLASS: " + e.getLocalizedMessage());
			throw e;
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
