/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;

/**
 * Provides a context for the PHAAAS modules to share information.
 * 
 * There are special methods for getting and setting the HttpResponse.
 * The HttpRequest however is immutable. 
 * @class PhaaasContext
 */
public class PhaaasContext extends BasicHttpContext {
	
	private static final String REQUEST_ATTRIBUTE = "PHAAAS_REQUEST";
	private static final String RESPONSE_ATTRIBUTE = "PHAAAS_RESPONSE";
	private static final String CREDENTIAL_ATTRIBUTE = "PHAAAS_CREDENTIAL";
	private static final String PRINCIPAL_ATTRIBUTE = "PHAAAS_PRINCIPAL";
	private static final String SESSION_ATTRIBUTE = "PHAAAS_SESSION";

	public PhaaasContext(HttpRequest request) {
		super();
		super.setAttribute(REQUEST_ATTRIBUTE, request);
	}
	
	public PhaaasContext(final HttpContext parent, HttpRequest request) {
		super(parent);
		super.setAttribute(REQUEST_ATTRIBUTE, request);
	}
	
	public HttpRequest getHttpRequest() {
		return (HttpRequest) super.getAttribute(REQUEST_ATTRIBUTE);
	}
	
	public HttpResponse getHttpResponse() {
		return (HttpResponse) super.getAttribute(RESPONSE_ATTRIBUTE);
	}
	
	public void setHttpResponse(HttpResponse response) {
		super.setAttribute(RESPONSE_ATTRIBUTE, response);
	}
	
	public Credential getCredential() {
		return (Credential) super.getAttribute(CREDENTIAL_ATTRIBUTE);
	}
	
	public void setCredential(Credential credential) {
		super.setAttribute(CREDENTIAL_ATTRIBUTE, credential);
	}
	
	public Principal getPrincipal() {
		return (Principal) super.getAttribute(PRINCIPAL_ATTRIBUTE);
	}
	
	public void setPrincipal(Principal principal) {
		super.setAttribute(PRINCIPAL_ATTRIBUTE, principal);
	}
	
	public String getSessionId() {
		return (String) super.getAttribute(SESSION_ATTRIBUTE);
	}
	
	public void setSessionId(String sessionId) {
		super.setAttribute(SESSION_ATTRIBUTE, sessionId);
	}
	
	@Override
	public Object getAttribute(String id) {
		return super.getAttribute(id);
	}

	@Override
	public Object removeAttribute(String id) {
		if(id.equals(REQUEST_ATTRIBUTE))
			throw new IllegalArgumentException("Cannot remove the HttpRequest attribute");
		
		return super.removeAttribute(id);
	}

	@Override
	public void setAttribute(String id, Object obj) {
		if(id.equals(REQUEST_ATTRIBUTE))
			throw new IllegalArgumentException("Cannot set the HttpRequest attribute");
		
		super.setAttribute(id, obj);
	}

}
