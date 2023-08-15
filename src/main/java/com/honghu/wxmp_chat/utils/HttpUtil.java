package com.honghu.wxmp_chat.utils;

import com.alibaba.fastjson.JSONObject;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.util.OpenAiUtils;
import okhttp3.*;
import okhttp3.Request.Builder;


import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author honghu
 */
public class HttpUtil {
    static X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
    static OkHttpClient client = new OkHttpClient().newBuilder().
            sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager),manager)
            .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier()).build();

    public static String callOpenApi(String request,String user){
        List<String> chatCompletion = OpenAiUtils.createChatCompletion(request, user);
        return chatCompletion.get(0);
    }
    /**
     * post请求xml入参
     *
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public static String doPostXml(String url, String data) throws IOException {
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * post请求json入参
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doPostJson(String url, String data) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String doPostJson(String url, String data, Map<String, String> headerMap) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        try {
            // 拼接参数
            OkHttpClient client2 = client.newBuilder().readTimeout(60, TimeUnit.SECONDS).build();
            Builder builder = new Builder().url(url).post(body);
            if (headerMap != null && !headerMap.isEmpty()) {
                headerMap.forEach(builder::addHeader);
            }
            // 发送请求
            Request request = builder.build();
            Response response = client2.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        Response response = null;
        String result = "";
        Request request = new Builder().url(url).build();
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取信贷接口请求密钥
     *
     * @param userName
     * @param passwd
     * @return
     */
    public static String getUserCreditUrlKey(String userName, String passwd) {
        try {
            final Base64.Encoder encoder = Base64.getEncoder();
            final String text = userName + ":" + passwd;
            final byte[] textByte = text.getBytes("UTF-8");
            final String encodedText = encoder.encodeToString(textByte);
            return "Basic " + encodedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}



