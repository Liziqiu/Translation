package com.gdut.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class HttpStringRequest extends HttpRequest<String>{

	@Override
	protected String parse(InputStream data) {
		try {
			return new String(InputStreamToBytes(data),parseCharset(getResponeHeader()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String parseCharset(Map<String, String> headers) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equalsIgnoreCase("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return "ISO-8859-1";
    }

}
