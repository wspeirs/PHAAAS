/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.credential.providers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.HttpUtils.StatusCode;
import com.bittrust.http.PhaaasContext;
import com.bittrust.session.SessionStore;

/**
 * @class BasicAuth
 */
public class BasicAuth implements CredentialProvider {
	private final String realm;
	
	public BasicAuth(BasicModuleConfig config) {
		realm = config.getParam("realm");
	}
	
	@Override
	public CredentialProviderResult getCredentialFromHttpRequest(PhaaasContext context) {
		HttpRequest request = context.getHttpRequest();
		Header authHeader = null;
		
		// make sure we have the authentication header and it's basic auth
		if(null == (authHeader = request.getFirstHeader("Authorization")) ||
		   false == authHeader.getValue().substring(0, 5).equalsIgnoreCase("basic")) {
			return generateAuthNeededResponse(context);	// send the response back
		}
		
		// we found what we think is basic auth, try to decode and create a credential
		String authString = new String(Base64.decodeBase64(authHeader.getValue().substring(6)));
		
		// if we don't find a : then send back that we need auth
		if(!authString.contains(":")) {
			return generateAuthNeededResponse(context);	// send the response back
		}
		
		// get the index of the : and create a credential
		int index = authString.indexOf(':');
		String userName = authString.substring(0, index);
		Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("password", authString.substring(index+1));
		
		Credential cred = new Credential(userName, properties);
		
		context.setCredential(cred);
		
		return CredentialProviderResult.CREDENTIAL_FOUND;
	}
	
	private CredentialProviderResult generateAuthNeededResponse(PhaaasContext context) {
		HttpResponse response = HttpUtils.generateResponse(StatusCode.UNAUTHENTICATED, "Authentication Needed");
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
		
		context.setHttpResponse(response);
		return CredentialProviderResult.SEND_RESPONSE;	// send the response back
	}

}
