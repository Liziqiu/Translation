package com.gdut.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {
	private URL url;
	private String UrlStr;
	private Map<String,String> header;
	private String param;
	
	private int ResopneCode;
	private String ResponeContent;
	private Map<String,String> ResponeHeader;
	
	
	public HttpRequest() {
		ResponeHeader = new HashMap<String,String>();
	}

	public void setUrlStr(String urlStr) {
		UrlStr = urlStr;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setHeader(Map<String,String> header) {
		this.header = header;
	}

	public void setParam(String param) {
		this.param = param;
	}
	
	public int getResopneCode() {
		return ResopneCode;
	}

	public String getResponeContent() {
		return ResponeContent;
	}

	public Map<String, String> getResponeHeader() {
		return ResponeHeader;
	}

	public void doGet(){
		HttpURLConnection conn;
		InputStream in=null;
		try {
			url = new URL(UrlStr+"?"+param);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			//conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			for(Entry<String,String> v:header.entrySet()){
				conn.setRequestProperty(v.getKey(), v.getValue());
			}
            conn.setConnectTimeout(30000);
			conn.connect();
			
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
			if(in==null)return;
			ResponeContent = new String(InputStreamToBytes(in),parseCharset(ResponeHeader));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
	public static class Builder{
		private String Url;
		private String Method;
		private Map<String,String> header;
		private List<String> params;
		
		
		
		public Builder() {
			header = new HashMap<String,String>();
			params = new ArrayList<String>();
		}

		public void setUrl(String url) {
			Url = url;
		}
		
		public void setMethod(String method) {
			Method = method;
		}

		public void addHeader(String key,String value){
			header.put(key, value);
		}
		
		public void addParams(String key,String value) throws UnsupportedEncodingException{
			if(key == null || value == null){
				return;
			}
			value = URLEncoder.encode(value, "utf-8");
			params.add(key);
			params.add(value);
		}
		
		public HttpRequest create(){
			HttpRequest request = new HttpRequest();
			request.setUrlStr(Url);
			request.setHeader(header);
			request.setParam(GenerateParamStr());
			return request;
		}

		private String GenerateParamStr() {
			String result = null;
			if(!params.isEmpty()){
				StringBuilder sb = new StringBuilder();
				int len = params.size();
				for(int i = 0;i<len;i=i+2){
					sb.append(params.get(i));
					sb.append("=");
					sb.append(params.get(i+1));
					sb.append("&");
				}
				sb.deleteCharAt(sb.length()-1);
				result = sb.toString();
			}
			return result;
		}
	}
}
