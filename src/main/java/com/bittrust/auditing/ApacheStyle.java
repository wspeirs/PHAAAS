/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.auditing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;
import com.bittrust.credential.Credential;
import com.bittrust.credential.Principal;

/**
 * @class ApacheStyle
 */
public class ApacheStyle implements Auditor {

	private FileWriter fileWriter;
	private String requestLine;
	
	public ApacheStyle(BasicModuleConfig config) {
		if(config.getParam("log_file") != null)
			try { fileWriter = new FileWriter(new File(config.getParam("log_file"))); }
			catch (IOException e) {	e.printStackTrace(); }
	}
	
	@Override
	public StringBuilder receivedConnection(InetAddress address) {
		StringBuilder sb =  new StringBuilder(address.getHostAddress().toString());
		
		sb.append(" - ");
		
		return sb;
	}

	@Override
	public void receivedRequest(StringBuilder sb, HttpRequest request) {
		requestLine = request.getRequestLine().toString();
	}

	@Override
	public void credentialFound(StringBuilder sb, Credential cred) {
	}

	@Override
	public void principalFound(StringBuilder sb, Principal principal) {
		sb.append(principal.getUsername());
		sb.append(" [");
		sb.append(new Date().toString());
		sb.append("] \"");
		sb.append(requestLine);
		sb.append("\" ");
	}

	@Override
	public void authenticationFailed(StringBuilder sb, HttpResponse response) {
		sb.append("AUTHENTICATION FAILED");
	}

	@Override
	public void authorizationFailed(StringBuilder sb, HttpResponse response) {
		sb.append("AUTHORIZATION FAILED");
	}

	@Override
	public void serverResponse(StringBuilder sb, HttpResponse response) {
		sb.append(response.getStatusLine().getStatusCode());
		sb.append(" ");
		
		HttpEntity entity = response.getEntity();
		
		// might not have an entity
		if(entity == null)
			sb.append(0);
		else
			sb.append(entity.getContentLength());
	}

	@Override
	public void writeLog(StringBuilder sb) {
		if(fileWriter == null) {	// just write to STDOUT
			System.out.println(sb.toString());
		} else {
			sb.append("\n");
			try {
				fileWriter.write(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public File getLogLocation() {
		return null;
	}

	@Override
	public void setLogLocation(File file) {
	}
}