/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.authentication;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

/**
 * @class ActiveDirectory
 */
public class ActiveDirectory implements Authenticator {
	
	private Hashtable<Object, String> ldapEnv = new Hashtable<Object, String>();
	private LdapContext ldapCtx;
	
	public static void main(String[] args) {
		ActiveDirectory ad = new ActiveDirectory();
		
		ad.authenticateUser("test", "test");
	}

	@Override
	public boolean authenticate(HttpRequest request, JSONObject sessionMetaData) {
		
		
		return false;
	}
	
	public boolean authenticateUser(String username, String password) {
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		 
		// set security credentials, note using simple clear-text authentication
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, "test");	// username
		ldapEnv.put(Context.SECURITY_CREDENTIALS, "test");	// password
		
		// the ldap server
		ldapEnv.put(Context.PROVIDER_URL, "ldap://192.168.1.33");
		
		// we need to chase referrals when retrieving attributes
		ldapEnv.put(Context.REFERRAL, "follow");
		
		try {
			// make the connection to the server
			ldapCtx = new InitialLdapContext(ldapEnv, null);
			
			Attributes attrs = ldapCtx.getAttributes("cn=test,cn=Users,dc=ad,dc=es,dc=com", new String[] { "memberOf" });
			
			System.out.println("ATTRS: " + attrs);
			
			Attribute attr = attrs.get("memberOf");
			
			for(int i=0; i < attr.size(); ++i) {
				System.out.println("GROUP: " + attr.get(i));
			}
			
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void authenticationFailed(HttpRequest request, HttpResponse response, HttpContext context) {
	}

	@Override
	public String getUser(HttpRequest request) {
		return null;
	}

}
