package com.gdut.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class HttpStringRequest extends HttpRequest<String>{

	@Override
	protected String parse(InputStream data) throws httpError {
		try {
			return new String(InputStreamToBytes(data),parseCharset(getResponeHeader()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new httpError("HttpStringRequest parse error:"+e.toString());
		}
	}

}
