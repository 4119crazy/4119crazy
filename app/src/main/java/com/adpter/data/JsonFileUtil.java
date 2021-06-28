package com.adpter.data;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class JsonFileUtil {
    public JsonFileUtil(){

    }
    public static JsonFileUtil file=null;
    public static JsonFileUtil getJson(){
        if(file==null)
        return file=new JsonFileUtil();
        return file;
    }
    public JSONObject toverParseJsonFile(String areaCode, String code, String number) {
        try {
            JSONObject object = new JSONObject();
            object.put("areaCode",areaCode);
            object.put("code",code);
            object.put("number", number);
            return object;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public JSONObject toParseJsonFile(String area, String code, String number) {
        try {
            String language=getLanguage();
            JSONObject object = new JSONObject();
            object.put("area",area);
            object.put("code",code);
            object.put("number", number);
            object.put("language", language);
            return object;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }
    public String pariseJson(String data){
        try {
            JSONObject json = new JSONObject(data);
            String vdata=json.getString("data");
            return  vdata;
        }
        catch (Exception e){
            e.getStackTrace();
        }

        return null;
    }
    public static String postDownloadJson(String path, JSONObject post) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.addRequestProperty("Content-Type","application/json;charset=UTF-8");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.connect();
//            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//            printWriter.write(post.toString());
//            printWriter.flush();e
            OutputStream outputStream = httpURLConnection.getOutputStream();
//            byte[] data = post.toString().getBytes();
            //加密 data
            outputStream.write(post.toString().getBytes("utf-8"));
            outputStream.flush();
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while ((len = inputStream.read(arr)) != -1) {
                bos.write(arr, 0, len);
                bos.flush();
            }

//            data = bos.toByteArray();
            //解密 data
            //new String(data,"utf-8");
            String result = bos.toString("utf-8");
            bos.close();
            inputStream.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // TODO: 2021/4/13
        }

        return null;
    }

    public String get_uuid(){
        UUID own= UUID.randomUUID();
        return own.toString();
    }
    public String get_id(Context context){
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return androidId;
    }

    public String getLanguage(){
        Locale locale=Locale.getDefault();
        String lang=locale.getLanguage();
        return lang;
    }


}
