/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import java.util.HashMap;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @class NullProvider
 */
public class NullProvider implements CredentialProvider {

	public NullProvider(BasicModuleConfig config) {
	}
	
	@Override
	public CredentialProviderResult getCredentialOrPrincipal(SessionStore sessionStore, PhaaasContext context) {
		context.setCredential(new Credential("", new HashMap<String, String>()));
		
		return CredentialProviderResult.CREDENTIAL_FOUND;
	}

}
