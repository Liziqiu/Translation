package com.gdut.http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Translation {
	/*参数 from和to的值有：
	中文	zh	英语	en   日语	jp	韩语	kor   西班牙语	spa	 法语 fra   泰语	th	阿拉伯语	ara  俄罗斯语	ru	葡萄牙语	pt   粤语	yue	文言文wyw
	白话文	zh	自动检测	auto   德语	de	意大利语	it   荷兰语	nl	希腊语	el*/
	
	public static final String translateURL = "http://fanyi.baidu.com/transapi";
	
	public static Responed translate(Request request){
		Responed respone = new Responed();
		HttpRequest.Builder builder = new HttpRequest.Builder();
		builder.setUrl(translateURL);
		try {
			builder.addParams("from", request.from);
			builder.addParams("to", request.to);
			builder.addParams("query", request.source);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpRequest translate = builder.create();
		translate.doGet();
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
