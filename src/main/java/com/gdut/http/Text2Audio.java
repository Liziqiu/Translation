package com.gdut.http;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import java.io.File;

public class Text2Audio {

	public static final String text2AudioURL = "http://tts.baidu.com/text2audio";
	public static String AUDIO_FILE_PATH;
    private AudioFileCache fileCache;
    
	public Text2Audio(String RootDir) {
		AUDIO_FILE_PATH = RootDir+"/text2audio";
        init();
	}

    private void init() {
        fileCache = new AudioFileCache();
        File file = new File(AUDIO_FILE_PATH);
        if(!file.exists()){
            file.mkdir();
            return;
        }

        File[] allFiles = file.listFiles();
        if(allFiles == null){
            return;
        }
        for(File f:allFiles){
            String key = f.getName();
            key = key.substring(0,key.lastIndexOf("."));
            String value = f.getAbsolutePath();
            fileCache.put(key,value);
        }

    }

    public String GetAudio(String lan,String txt) throws httpError{
		return GetAudio(lan,txt,10);
	}
	
	public String GetAudio(String lan,String txt,int speed) throws httpError{
		String result = lan+"#"+txt;
		if(!FileIsExist(result)){
			getFileFromHttpReq(result,lan,txt,speed);
		}
		return fileCache.get(result);
	}

	public boolean getFileFromHttpReq(String result,String lan,String txt,int speed) throws httpError {
		HttpAudioRequest request = new HttpAudioRequest();
		request.setAudioFile(AUDIO_FILE_PATH+"/"+result);
		request.setUrl(text2AudioURL);
		request.setMethod("GET");
		request.addParam("lan", lan);
		request.addParam("pid", "101");
		request.addParam("ie", "UTF-8");
		request.addParam("text", txt);
		request.addParam("spd", Integer.toString(speed));
		request.connect();
        if(request.getResponeContent()!=null){
            fileCache.put(result,request.getResponeContent());
            return true;
        }
		return false;
	}

	private boolean FileIsExist(String file) {
		return fileCache.get(file) != null;
	}

    class AudioFileCache{
        LruCache<String,String> cache = new LruCache<String,String>(100){
            @Override
            protected void entryRemoved(boolean evicted, String key, String oldValue, String newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                deleteFile(oldValue);
            }
        };
        public void put(String key,String value){
            cache.put(key,value);
        }
        public String get(String key){
            return cache.get(key);
        }
    }

    private boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public void AsyncGetAudio(String lan,String txt, final CallBack callBack){
        AsyncTask<String,Integer,String> task = new AsyncTask<String,Integer,String>(){
            private httpError error;
            @Override
            protected String doInBackground(String... params) {
                String lan = params[0];
                String txt = params[1];
                try {
                    String result = GetAudio(lan, txt);
                    return result;
                } catch (httpError e) {
                    e.printStackTrace();
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(error != null){
                    callBack.Error(error);
                }else{
                    callBack.Completed(s);
                }
            }
        };
        task.execute(lan,txt);
    }
    public void playSound(String audioFile,Context context) {
        SoundPool sp = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn/audioMaxVolumn;

        sp.play(sp.load(audioFile,1), volumnRatio, volumnRatio, 1, 0, 1);
    }
    public interface CallBack{
        public void Completed(String audioPath);
        public void Error(httpError error);
    }
}
