package com.honghu.wxmp_chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.honghu.wxmp_chat.entity.MessageResponseBody;
import com.honghu.wxmp_chat.entity.MessageSendBody;
import com.honghu.wxmp_chat.utils.HttpUtil;
import com.honghu.wxmp_chat.utils.IllegalWorkUtil;
import com.honghu.wxmp_chat.utils.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import toolgood.words.IllegalWordsSearch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author honghu
 */
@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {

    @Value("${openapi.key}")
    private String apiKey;
    /**
     * 接口请求地址
     */
    private final String url = "https://api.openai.com/v1/completions";

    @Resource
    RedisHelper redisHelper;

    private final String human = "Human:";
    /**
     * 定义ai的名字
     */
    private final String Ai = "小橘:";

    @Override
    public String reply(String messageContent, String userKey) {
        // 默认信息
        String message = "Human:你好\n小橘:你好\n";
        String response = "";
//        if (redisHelper.hasKey(userKey)) {
//            // 如果存在key，拿出来
//            message = redisHelper.get(userKey);
//        }
//        // 拼接字符,设置回去
//        message = message + human + messageContent + "\n";
//        redisHelper.setEx(userKey, message, 60, TimeUnit.SECONDS);
        // 调用接口获取数据
        response = HttpUtil.callOpenApi(messageContent, userKey);
//        JSONObject obj = getReplyFromGPT(message);
//        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
//        // 存储对话内容，让机器人更加智能
//        if (messageResponseBody != null) {
//            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
//                String replyText = messageResponseBody.getChoices().get(0).getText();
//                // 拼接字符,设置回去
//                new Thread(() -> {
//                    String msg = redisHelper.get(userKey);
//                    msg = msg + Ai + replyText + "\n";
//                    redisHelper.setEx(userKey, msg, 60, TimeUnit.SECONDS);
//                }).start();
//                response = replyText.replace("小橘:", "");
//            }
//        }
        if ("".equals(response)) {
            response = "暂时不明白你说什么!";
        }
        if (IllegalWorkUtil.containsIllegalWord(response)) {
            response = "您提问的问题涉及敏感，暂时无法回答!";
        }
        if (redisHelper.isThinking(userKey)) {
            redisHelper.setLastResult(userKey, response);
            redisHelper.clearThinking(userKey);
        }
        return response;
    }

    private JSONObject getReplyFromGPT(String message) {
        String url = this.url;
        Map<String, String> header = new HashMap();
        header.put("Authorization", "Bearer " + apiKey);
        header.put("Content-Type", "application/json");
        MessageSendBody messageSendBody = buildConfig();
        messageSendBody.setPrompt(message);
        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        log.info("发送的数据：" + body);
        // 发送请求
        String data = HttpUtil.doPostJson(url, body, header);
        JSONObject obj = JSON.parseObject(data);
        return obj;
    }

    /**
     * 构建请求体
     *
     * @return
     */
    private MessageSendBody buildConfig() {
        MessageSendBody messageSendBody = new MessageSendBody();
        messageSendBody.setModel("text-davinci-003");
        messageSendBody.setTemperature(0.9);
        messageSendBody.setMaxTokens(1000);
        messageSendBody.setTopP(1);
        messageSendBody.setFrequencyPenalty(0.0);
        messageSendBody.setPresencePenalty(0.6);
        List<String> stop = new ArrayList<>();
        stop.add(" train:");
        stop.add(" Human:");
        messageSendBody.setStop(stop);
        return messageSendBody;
    }

    /**
     * 解决大文章问题超5秒问题，但是目前事个人订阅号，没有客服接口权限，暂时没用
     *
     * @param messageContent
     * @param userKey
     * @return
     */
    public String getArticle(String messageContent, String userKey) {
        String url = "https://api.openai.com/v1/completions";
        Map<String, String> header = new HashMap();
        header.put("Authorization", "Bearer " + apiKey);
        header.put("Content-Type", "application/json");

        MessageSendBody messageSendBody = buildConfig();
        messageSendBody.setMaxTokens(150);
        messageSendBody.setPrompt(messageContent);
        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        String data = HttpUtil.doPostJson(url, body, header);
        log.info("返回的数据：" + data);
        JSONObject obj = JSON.parseObject(data);
        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
        if (messageResponseBody != null) {
            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
                String replyText = messageResponseBody.getChoices().get(0).getText();
                return replyText.replace("train:", "");
            }
        }
        return "暂时不明白你说什么!";
    }
}
