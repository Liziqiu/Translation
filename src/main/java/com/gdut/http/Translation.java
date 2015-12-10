package com.gdut.http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Translation {
	
	public static final String translateURL = "http://fanyi.baidu.com/transapi";
	
	public Responed translate(Request request) throws httpError{
		Responed respone = new Responed();

		HttpStringRequest translate = new HttpStringRequest();
		translate.setMethod("get");
		translate.setUrl(translateURL);
		translate.addParam("from", request.from);
		translate.addParam("to", request.to);
		translate.addParam("query", request.source);
		translate.connect();

		if(translate.getResopneCode()!=200){
			respone.result = translate.getResponeContent();
            respone.isError = true;
			return respone;
		}
		try {
			JSONObject translateRespones = new JSONObject(translate.getResponeContent());
			respone.result = translateRespones.getJSONArray("data").getJSONObject(0).getString("dst");

			//debug start
			if ("[]".equals(translateRespones.getString("liju"))) {
				//System.out.println(translate.getResponeContent());
				return respone;
			}

			//debug end

			String liju = translateRespones.getJSONObject("liju").getString("double");
			JSONArray lijus = new JSONArray(liju);
			int liju_len = lijus.length();
			for (int index = 0; index < liju_len; index++) {
				JSONArray source_sentence = lijus.getJSONArray(index).getJSONArray(0);
				JSONArray translate_sentence = lijus.getJSONArray(index).getJSONArray(1);
				StringBuilder source = new StringBuilder();
				StringBuilder translated = new StringBuilder();
				int soutce_len = source_sentence.length();
				int translate_len = translate_sentence.length();
				for (int i = 0; i < soutce_len; i++) {
					source.append(source_sentence.getJSONArray(i).getString(0));
					if (source_sentence.getJSONArray(i).length() == 5) {
						source.append(source_sentence.getJSONArray(i).getString(4));
					}
				}
				for (int i = 0; i < translate_len; i++) {
					translated.append(translate_sentence.getJSONArray(i).getString(0));
					if (translate_sentence.getJSONArray(i).length() == 5) {
						translated.append(translate_sentence.getJSONArray(i).getString(4));
					}
				}
				respone.sample.add(new Entry(source.toString(), translated.toString()));
			}
		}catch(Exception e){
			e.printStackTrace();
			respone.isError = true;
		}
		return respone;
	}
	public static class Request{
		public String from="zh";
		public String to="en";
		public String source;
		public Request(String from, String to, String source) {
			super();
			this.from = from;
			this.to = to;
			this.source = source;
		}
		public Request(String source) {
			super();
			this.source = source;
		}
		
	}
	public static class Responed{
		public String result=null;
		public boolean isError = false;
		public List<Entry> sample;
		public Responed() {
			sample = new ArrayList<Entry>();
		}
		
	}
	public static class Entry{
		public String source;
		public String translated;
		public Entry(String source, String translated) {
			super();
			this.source = source;
			this.translated = translated;
		}
		
	}
}
