package org.dingdang.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dingdang.common.ApiResponse;
import org.dingdang.common.Constants;
import org.dingdang.common.utils.CaffeineCacheUtil;
import org.dingdang.common.utils.IdWorker;
import org.dingdang.domain.AuthToken;
import org.dingdang.domain.SysUser;
import org.dingdang.mapper.AuthMapper;
import org.dingdang.mapper.WordBookMapper;
import org.dingdang.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.dingdang.common.utils.JwtUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:51 星期二
 * @Description:
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private WordBookMapper wordBookMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IdWorker idWorker;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;


    // 获取access_token的接口地址（GET）
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
    // 获取小程序码的接口地址（POST）
    private static final String GET_QR_CODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";


    private String getAccessTokenUrl() {
        return ACCESS_TOKEN_URL + "&appid=" + appid + "&secret=" + secret;
    }

    @Override
    public ResponseEntity<byte[]> loginByWechat(String scene) {
        byte[] imageContent = getQRCode(scene);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
    }

    @Override
    public ApiResponse isLogin(String scene) {
        String cachedScene = (String) CaffeineCacheUtil.get(Constants.WECHAT_SCENE_CACHE + scene);
        if (cachedScene == null || cachedScene.equals("invalid")) {
            return ApiResponse.ofMessage(400, "请刷新二维码重新扫码");
        }
        if (cachedScene.startsWith("token:")) {
            return ApiResponse.ofSuccess(cachedScene.substring(6));
        }
        if (cachedScene.equals("waiting")) {
            return ApiResponse.ofMessage(-1, "等待扫码");
        }
        return ApiResponse.ofMessage(400, "未知错误，请刷新二维码重新扫码");
    }

    @Override
    public ApiResponse handleWechatCallback(String scene, String code, Boolean useScene) {
        if (useScene) {
            // 从缓存中检索scene值，检查其是否有效
            String cachedScene = (String) CaffeineCacheUtil.get(Constants.WECHAT_SCENE_CACHE + scene);
            if (cachedScene == null) {
                // 无效的scene值
                CaffeineCacheUtil.put(Constants.WECHAT_SCENE_CACHE + scene, "invalid");
                return ApiResponse.ofMessage(400, "请刷新二维码重新扫码");
            }
        }

        JSONObject sessionInfo = getSessionByCode(code);
        if (sessionInfo == null) {
            return ApiResponse.ofMessage(400, "认证失败");
        }
        String openid = sessionInfo.getString("openid");

        // 判断是否注册过
        SysUser sysUser = authMapper.selectByOpenId(openid);
        if (sysUser == null) {
            // 未注册,实现注册逻辑
            sysUser = new SysUser();
            sysUser.setOpenId(openid);
            sysUser.setUserId(idWorker.nextId());
            sysUser.setUserName(openid);
            authMapper.register(sysUser);
            // 注册事件
            registerEvent(sysUser);
        }

        // 生成token
        String token = jwtUtil.generateToken(sysUser.getUserId(), sysUser.getOpenId());

        if (useScene) {
            // 更新缓存
            CaffeineCacheUtil.put(Constants.WECHAT_SCENE_CACHE + scene, "token:" + token);
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ApiResponse.ofSuccess(map);
    }



    private AuthToken getToken(String accessTokenUrl) {
        try {
            URL url = new URL(accessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String response = new String(connection.getInputStream().readAllBytes());
            JSONObject accessTokenObject = JSONObject.parseObject(response);
            return new AuthToken(accessTokenObject.getString("access_token"), accessTokenObject.getIntValue("expires_in"));
        } catch (IOException e) {
            log.error("Failed to get token", e);
            return null;
        }
    }

    public byte[] getQRCode(String scene) {
        try {
            AuthToken authToken = getToken(getAccessTokenUrl());
            if (authToken == null || authToken.getAccessToken() == null) {
                throw new IllegalStateException("Failed to retrieve valid access token.");
            }

            URL url = new URL(GET_QR_CODE_URL + "?access_token=" + authToken.getAccessToken());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Type", "image/png");
            connection.setDoOutput(true);
            CaffeineCacheUtil.put(Constants.WECHAT_SCENE_CACHE+scene, "waiting");
            log.info("scene: {}", scene);
            String jsonData = "{" +
                    "\"scene\": " + scene + "," +
                    "\"width\": 250" +
                    "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (InputStream is = connection.getInputStream()) {
                return is.readAllBytes();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            log.error("Failed to get QR code", e);
            throw new RuntimeException("Failed to get QR code: " + e.getMessage(), e);
        }
    }

    public JSONObject getSessionByCode(String code) {

        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
        );

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 注册事件
    private void registerEvent(SysUser sysUser) {

    }
}