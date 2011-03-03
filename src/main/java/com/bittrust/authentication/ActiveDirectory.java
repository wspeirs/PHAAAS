/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;
import com.bittrust.http.HttpUtils;
import com.bittrust.http.PhaaasContext;

/**
 * @class ActiveDirectory
 */
public class ActiveDirectory implements Authenticator {
	
	private Hashtable<Object, String> ldapEnv = new Hashtable<Object, String>();
	private LdapContext ldapCtx;
	
	private String host = null;
	private String baseDN = null;
	
	public ActiveDirectory(String host, String baseDN) {
		this.host = host;
		this.baseDN = baseDN;
	}
	
	public ActiveDirectory(BasicModuleConfig config) {
		this.host = config.getParam("host");
		this.baseDN = config.getParam("baseDN");
	}
	
	public static void main(String[] args) {
		ActiveDirectory ad = new ActiveDirectory("192.168.1.33", "dc=ad,dc=es,dc=com");
		HashMap<String, String> p = new HashMap<String, String>();
		
		p.put("password", "test");
		Credential cred = new Credential("test", p);
		PhaaasContext context = new PhaaasContext(null);
		
		context.setCredential(cred);
		if(ad.authenticate(context))
			System.out.println("AUTHED");
		else
			System.out.println("UNAUTHED");
	}

	public boolean authenticate(PhaaasContext context) {
		Credential credential = context.getCredential();
		String username = credential.getUsername();
		
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		 
		// set security credentials, note using simple clear-text authentication
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, username);	// username
		ldapEnv.put(Context.SECURITY_CREDENTIALS, credential.getProperty("password"));	// password
		
		// the ldap server
		ldapEnv.put(Context.PROVIDER_URL, "ldap://" + host);
		
		// we need to chase referrals when retrieving attributes
		ldapEnv.put(Context.REFERRAL, "follow");
		
		try {
			// make the connection to the server
			ldapCtx = new InitialLdapContext(ldapEnv, null);
			
			Attributes attrs = ldapCtx.getAttributes("cn=" + username + ",cn=Users," + baseDN, new String[] { "memberOf" });
			
			System.out.println("ATTRS: " + attrs);
			
			Attribute attr = attrs.get("memberOf");
			
			// create our new principal
			Principal principal = new Principal(username);
			
			for(int i=0; i < attr.size(); ++i) {
//				System.out.println("GROUP: " + attr.get(i));
				principal.addGroup(attr.get(i).toString());	// add the group to the principal
			}
			
			context.setPrincipal(principal);
			
		} catch (NamingException e) {
			StringBuilder sb = new StringBuilder("<html><head><title>Authentication Failed</title></head><body>");
			
			sb.append(e.getExplanation());
			sb.append("</body></html>");
			
			context.setHttpResponse(HttpUtils.generateResponse(HttpUtils.StatusCode.UNAUTHENTICATED, sb.toString()));
			return false;
		}
		
		return true;
	}
}
