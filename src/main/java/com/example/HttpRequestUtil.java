package com.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    private static int timeout = 60000;

    /**
     * 不带超时时间的post请求，默认60s超时
     *
     * @param postUrl
     * @param bodyContent
     * @return
     * @throws Exception
     */
    public static String sendPost(String postUrl, String bodyContent) throws Exception {
        return sendPost(postUrl, bodyContent, timeout);
    }

    /**
     * 带超时时间的post请求
     *
     * @return
     */
    public static String sendPost(String postUrl, String bodyContent, Integer setTimeout) throws Exception {

        logger.info("开始发送Post请求，请求地址：{},body：{}", postUrl, bodyContent);
        try {
            /**
             * 获取时间戳
             */
            long time = System.currentTimeMillis();
            String timestamp = String.valueOf(time / 1000);
            // 根据地址获取请求
            HttpClient httpclient = HttpClients.createDefault();

            HttpPost httppost = new HttpPost(postUrl);
            httppost.addHeader(HTTP.CONTENT_TYPE, "application/json");
            httppost.setHeader("timestamp", timestamp);
            //填充请求内容
            StringEntity entity = new StringEntity(bodyContent, "utf-8");
            // entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
            RequestConfig config = RequestConfig.DEFAULT;
            httppost.setConfig(setTimeOutConfig(config, setTimeout));
            /**
             * 发送请求
             */
            // 执行post请求
            HttpResponse response = httpclient.execute(httppost);
            String jsonResult = EntityUtils.toString(response.getEntity());
            logger.info("发送post请求交易成功，返回数据：{}", jsonResult);
            return jsonResult;
        } catch (Exception e) {
            logger.error("发送post请求异常:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 不带超时时间的get请求，默认60s超时
     *
     * @param getUrl
     * @return
     * @throws Exception
     */
    public static String sendGet(String getUrl) throws Exception {
        return sendGet(getUrl, timeout);
    }

    /**
     * 带超时时间的get请求
     *
     * @return
     */
    public static String sendGet(String getUrl, Integer setTimeout) throws Exception {

        logger.info("开始发送Get请求，请求地址：{}", getUrl);
        try {
            /** 获取时间戳 */
            long time = System.currentTimeMillis();
            String timestamp = String.valueOf(time / 1000);
            // 根据地址获取请求
            HttpClient httpclient = HttpClients.createDefault();
            // 设置请求头信息
            HttpGet httpGet = new HttpGet(getUrl);
            httpGet.addHeader(HTTP.CONTENT_TYPE, "application/json");
            httpGet.setHeader("timestamp", timestamp);

            RequestConfig config = RequestConfig.DEFAULT;
            httpGet.setConfig(setTimeOutConfig(config, setTimeout));
            /** 发送请求 */
            HttpResponse response = httpclient.execute(httpGet);
            String jsonResult = EntityUtils.toString(response.getEntity());
            logger.info("发送get请求交易成功，返回数据：{}", jsonResult);
            return jsonResult;
        } catch (Exception e) {
            logger.error("发送get请求异常:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 设置默认超时时间
     *
     * @param requestConfig
     * @param setTimeout
     * @return
     */
    private static RequestConfig setTimeOutConfig(RequestConfig requestConfig, Integer setTimeout) {
        //设置请求超时时间，单位毫秒
        if (setTimeout != null) {
            timeout = setTimeout;
        }
        return RequestConfig.copy(requestConfig)
            .setConnectionRequestTimeout(timeout)
            .setConnectTimeout(timeout)
            .setSocketTimeout(timeout)
            .build();
    }
}
