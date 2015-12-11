package com.gdut.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class HttpRequest<T> {
	private String Url;
	private String method;
	private Map<String,String> header;
	private StringBuilder param;

	private int ResopneCode;
	private T ResponeContent = null;
	private Map<String,String> ResponeHeader;
	private String Error = null;



	public HttpRequest() {
		param = new StringBuilder();
		header = new HashMap<String,String>();
		ResponeHeader = new HashMap<String,String>();
	}

	protected abstract T parse(InputStream data) throws httpError;

	public void addParam(String key,String value) throws httpError{
		if(key == null || key.trim().isEmpty())return;
		if(value == null || value.trim().isEmpty())return;
		if(param.length() != 0){
			param.append("&");
		}
		try {
			value = URLEncoder.encode(value,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new httpError("HttpRequest params error:"+e.toString());
		}
		param.append(key+"="+value);
	}

	public void addHeader(String key,String value){
		header.put(key, value);
	}
	public int getResopneCode() {
		return ResopneCode;
	}
	public T getResponeContent() {
		return ResponeContent;
	}
	public Map<String, String> getResponeHeader() {
		return ResponeHeader;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public boolean connect() throws httpError{
		InputStream respone = null;
		if("get".equalsIgnoreCase(method)){
			respone = doGet();
		}else if("post".equalsIgnoreCase(method)){
			//TODO
		}
		if(respone == null || ResopneCode !=200){
			return false;
		}
		ResponeContent = parse(respone);
		if(respone!=null){
			try {
				respone.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	private InputStream doGet() throws httpError {
		StringBuilder urlStr = new StringBuilder(Url);
		InputStream in = null;
		InputStream result = null;
		if(param.length()>0){
			urlStr.append("?"+param.toString());
		}
		try {
			URL url = new URL(urlStr.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			for(Entry<String,String> v:header.entrySet()){
				conn.setRequestProperty(v.getKey(), v.getValue());
			}

			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(10*1000);
			ResopneCode = conn.getResponseCode();
			for(Entry<String,List<String>> head: conn.getHeaderFields().entrySet()){
				if(head.getKey()!=null && head.getValue()!=null){
					ResponeHeader.put(head.getKey(), head.getValue().get(0));
				}
			}
			if(ResopneCode == 200){
				in = conn.getInputStream();
			}else{
				in = conn.getErrorStream();
				Error = new String(InputStreamToBytes(in),parseCharset(ResponeHeader));
                throw new httpError("http respone "+ ResopneCode +" error:"+Error);
            }
			result = in;
		} catch (Exception e) {
			if(in != null){
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			throw new httpError("doGet occur error:"+e.toString());
		}
		return result;
	}
	public byte[] InputStreamToBytes(InputStream in){
		byte[] result=null;
		byte[] buffter = new byte[1024];
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try{
			int len = in.read(buffter);
			while(len != -1){
				output.write(buffter, 0, len);
				len = in.read(buffter);
			}
			result = output.toByteArray();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
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
