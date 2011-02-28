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
	
	@Override
	public void modifyRequest(PhaaasContext context) {
	}

	@Override
	public void modifyResponse(PhaaasContext context) {
	}

}
