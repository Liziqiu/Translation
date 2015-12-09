package com.gdut.http;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpAudioRequest extends HttpRequest<String>{

	private String audioFile;
	
	
	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
	@Override
	protected String parse(InputStream data) {
		String file = audioFile+"."+parseType(getResponeHeader());
		writeTofile(data, file);
		return file;
	}
	public String parseType(Map<String, String> headers) {
        String contentType = headers.get("Content-type");
        if (contentType != null) {
            String[] params = contentType.split("/");
            if(params == null || params.length<2 || !params[0].equalsIgnoreCase("audio")){
            	return "dat";
            }
            return params[1];
            
        }

        return "dat";
    }
	
	private void writeTofile(InputStream data ,String path){
		DataOutputStream out = null;
		FileOutputStream file = null;
		BufferedOutputStream buffter = null;
		byte[] temp = new byte[1024];
		try {
			file = new FileOutputStream(path);
			buffter = new BufferedOutputStream(file);
			out = new DataOutputStream(buffter);
			int len = data.read(temp);
			while(len != -1){
				out.write(temp, 0, len);
				len = data.read(temp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(file!=null){
				try {
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(buffter!=null){
				try {
					buffter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
