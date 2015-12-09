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
	private T ResponeContent;
	private Map<String,String> ResponeHeader;



	public HttpRequest() {
		param = new StringBuilder();
		header = new HashMap<String,String>();
		ResponeHeader = new HashMap<String,String>();
	}

	protected abstract T parse(InputStream data);

	public void addParam(String key,String value) throws UnsupportedEncodingException{
		if(key == null || key.trim().isEmpty())return;
		if(value == null || value.trim().isEmpty())return;
		if(param.length() != 0){
			param.append("&");
		}
		value = URLEncoder.encode(value,"utf-8");
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

	public void connect(){
		InputStream respone = null;
		if("get".equalsIgnoreCase(method)){
			respone = doGet();
		}else if("post".equalsIgnoreCase(method)){
			//TODO
		}
		if(respone == null){
			return;
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
	}

	private InputStream doGet() {
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
			}
			result = in;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
