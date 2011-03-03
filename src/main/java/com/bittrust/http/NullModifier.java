/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust.http;

import com.bittrust.config.BasicModuleConfig;

/**
 * @class NullModifier
 */
public class NullModifier implements RequestModifier, ResponseModifier {

	public NullModifier(BasicModuleConfig config) {
	}
	
	public void modifyRequest(PhaaasContext context) {
	}

	public void modifyResponse(PhaaasContext context) {
	}

}
