/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.auditing;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.Date;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class ApacheStyle
 */
public class ApacheStyle implements Auditor {

	private BasicModuleConfig config;
	private File file;
	
	public ApacheStyle(BasicModuleConfig config) {
		this.config = config;
	}
	
	@Override
	public StringBuilder receivedConnection(InetAddress address) {
		StringBuilder sb =  new StringBuilder();
		
		receivedConnection(sb, address);
		
		return sb;
	}

	@Override
	public void receivedConnection(StringBuilder sb, InetAddress address) {
		sb.append(address.toString());
		sb.append(" - ");
	}

	@Override
	public StringBuilder receivedRequest(HttpRequest request, String user) {
		StringBuilder sb = new StringBuilder();
		
		receivedRequest(sb, request, user);
		
		return sb;
	}

	@Override
	public void receivedRequest(StringBuilder sb, HttpRequest request, String user) {
		sb.append(user);
		sb.append(" [");
		sb.append(new Date().toString());
		sb.append("] \"");
		sb.append(request.getRequestLine().toString());
		sb.append("\" ");
	}

	@Override
	public void authenticationFailed(StringBuilder sb, String user) {
		sb.append("AUTHENTICATION FAILED");
	}

	@Override
	public void authorizationFailed(StringBuilder sb, String user) {
		sb.append("AUTHORIZATION FAILED");
	}

	@Override
	public void serverResponse(StringBuilder sb, HttpResponse response) {
		sb.append(response.getStatusLine().getStatusCode());
		sb.append(" ");
		sb.append(response.getEntity().getContentLength());
	}

	@Override
	public void writeLog(StringBuilder sb) {
		if(file == null) {	// just write to STDOUT
			System.out.println(sb.toString());
		} else {
			try {
				FileWriter fw = new FileWriter(file);
				
				sb.append("\n");
				
				fw.write(sb.toString());
			} catch (Exception e) {
				System.err.println("COULD NOT WRITE TO: " + file.getAbsolutePath() + " BECAUSE: " + e.getLocalizedMessage());
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
