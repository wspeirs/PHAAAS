/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.lang.reflect.InvocationTargetException;

import com.bittrust.auditing.Auditor;
import com.bittrust.authentication.Authenticator;
import com.bittrust.authorization.Authorizer;
import com.bittrust.config.ServiceConfig;

/**
 * @class ConfiguredHandler
 * 
 * The authentication, authorization, and auditing modules are set via the configuration file.
 */
public class ConfiguredHandler extends AbstractRequestHandler {

	private ServiceConfig serviceConfig = null;
	
	public ConfiguredHandler(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
		
		try {
			// setup the authenticator
			Class<Authenticator> authClass = (Class<Authenticator>) Class.forName(serviceConfig.getAuthenticationConfig().getClassName());
			Authenticator auth = (Authenticator)authClass.getConstructor(null).newInstance(null);
			this.setAuthenticator(auth);

			// setup the authorizer
			Class<Authorizer> authzClass = (Class<Authorizer>) Class.forName(serviceConfig.getAuthenticationConfig().getClassName());
			Authorizer authz = (Authorizer)authzClass.getConstructor(null).newInstance(null);
			this.setAuthorizer(authz);

			// setup the auditor
			Class<Auditor> auditClass = (Class<Auditor>) Class.forName(serviceConfig.getAuthenticationConfig().getClassName());
			Auditor audit = (Auditor)authClass.getConstructor(null).newInstance(null);
			this.setAuditor(audit);

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
