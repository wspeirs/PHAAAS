/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http.server.handlers;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * @class FileServerHandler
 */
public class FileServerHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)	throws HttpException, IOException {
		StringEntity entity = new StringEntity("File Server");
		
		response.setHeader("Content-Length", entity.getContentLength()+"");
		response.setEntity(entity);
	}

}
