/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class NullModifier
 */
public class NullModifier implements RequestModifier, ResponseModifier {

	public NullModifier(BasicModuleConfig config) {
	}
	
	@Override
	public HttpRequest modifyRequest(PhaaasContext context) {
		return context.getHttpRequest();
	}

	@Override
	public HttpResponse modifyResponse(PhaaasContext context) {
		return context.getHttpResponse();
	}

}
