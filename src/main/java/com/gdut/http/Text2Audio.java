package com.gdut.http;

import java.io.UnsupportedEncodingException;

public class Text2Audio {

	public static final String text2AudioURL = "http://tts.baidu.com/text2audio";
	public static final String AUDIO_FILE_PATH = "d:/audio/";
	
	public static String GetAudio(String lan,String txt){
		return GetAudio(lan,txt,2);
	}
	
	public static String GetAudio(String lan,String txt,int speed){
		String result = AUDIO_FILE_PATH+lan+"#"+txt;
		if(!FileIsExist(result)){
			getFileFromHttpReq(result,lan,txt,speed);
		}
		return result;
	}

	public static void getFileFromHttpReq(String result,String lan,String txt,int speed) {
		HttpAudioRequest request = new HttpAudioRequest();
		request.setAudioFile(result);
		request.setUrl(text2AudioURL);
		request.setMethod("GET");
		try {
			request.addParam("lan", lan);
			request.addParam("pid", "101");
			request.addParam("ie", "UTF-8");
			request.addParam("text", txt);
			request.addParam("spd", Integer.toString(speed));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.connect();
		
	}

	public static boolean FileIsExist(String file) {
		return false;
	}

}
