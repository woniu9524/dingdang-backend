package org.dingdang.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import org.dingdang.common.ApiResponse;

import org.dingdang.common.utils.QwenUtil;
import org.dingdang.common.utils.SM2Tool;
import org.dingdang.common.utils.UserContextUtil;
import org.dingdang.domain.DailyLearningStats;
import org.dingdang.domain.SysUser;
import org.dingdang.domain.UserWordBook;
import org.dingdang.domain.WordGenerateHistory;
import org.dingdang.domain.vo.WordMasteryVo;
import org.dingdang.domain.vo.WordbookWord;
import org.dingdang.domain.vo.WordbooksMstVo;
import org.dingdang.mapper.WordBookMapper;
import org.dingdang.service.WordBookService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/9 1:45 星期四
 * @Description:
 */

@Service
@AllArgsConstructor
public class WordBookServiceImpl implements WordBookService {

    private final WordBookMapper wordBookMapper;
    private final SM2Tool sm2Tool;

    @Override
    public List<WordbooksMstVo> getWordBookList() {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        return wordBookMapper.getWordBookList(currentUser.getUserId());
    }

    @Override
    public ApiResponse useWordBook(Long bookNo) {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        UserWordBook userWordBook = new UserWordBook();
        userWordBook.setBookNo(bookNo);
        userWordBook.setUserId(currentUser.getUserId());
        int flag = wordBookMapper.useWordBook(userWordBook);
        if (flag > 0) {
            return ApiResponse.ofSuccess();
        } else {
            return ApiResponse.ofMessage(400, "使用单词书失败");
        }

    }

    @Override
    public ApiResponse stopUseWordBook(Long bookNo) {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        UserWordBook userWordBook = new UserWordBook();
        userWordBook.setBookNo(bookNo);
        userWordBook.setUserId(currentUser.getUserId());
        int flag = wordBookMapper.stopUseWordBook(userWordBook);
        if (flag > 0) {
            return ApiResponse.ofSuccess();
        } else {
            return ApiResponse.ofMessage(400, "停用单词书失败");
        }
    }

    @Override
    public ApiResponse getWordList() {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        List<WordbookWord> wordbookWordList = wordBookMapper.getWordList(currentUser.getUserId());
        // 如果没有单词书，wordBooksMstList为空
        if (wordbookWordList.isEmpty()) {
            return ApiResponse.ofMessage(400, "没有单词书");
        }
        return ApiResponse.ofSuccess(wordbookWordList);
    }

    @Override
    public ApiResponse scoreWord(String word, Integer grade) {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        sm2Tool.updateReview(currentUser.getUserId(), word, grade);
        return ApiResponse.ofSuccess();
    }

    @Override
    public ApiResponse getWordNeedLearn(Integer page, Integer size) {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        int offset = (page - 1) * size;
        List<WordbookWord> wordbookWordList = wordBookMapper.getWordsToLearn(currentUser.getUserId(), offset, size);
        if (wordbookWordList.isEmpty()) {
            return ApiResponse.ofMessage(400, "没有单词书");
        }

        return ApiResponse.ofSuccess(wordbookWordList);


    }

    @Override
    public ApiResponse getWordReviewToday() {
        // 获取用户
        SysUser sysUser = UserContextUtil.getCurrentUser();
        List<WordbookWord> wordbookWordList = wordBookMapper.getWordReviewToday(sysUser.getUserId());
        return ApiResponse.ofSuccess(wordbookWordList);
    }

    @Override
    public ApiResponse getTodayLearnCount() {
        // 获取用户
        SysUser sysUser = UserContextUtil.getCurrentUser();
        return ApiResponse.ofSuccess(wordBookMapper.getTodayLearnCount(sysUser.getUserId()));
    }

    @Override
    public ApiResponse textGeneration() {
        try {
            SysUser sysUser = UserContextUtil.getCurrentUser();
            if(wordBookMapper.hasGenerateToday(sysUser.getUserId())){
                return ApiResponse.ofMessage(400, "今天已经生成过文本了，请查看历史");
            }
            List<WordbookWord> words = wordBookMapper.getWordReviewToday(sysUser.getUserId());

            // 按 eFactor 降序排序，并取前 50 个单词
            List<Map<String, String>> textList = words.stream()
                    .sorted((w1, w2) -> Double.compare(w2.getEFactor(), w1.getEFactor()))
                    .limit(20)
                    .map(wordbookWord -> Map.of("word", wordbookWord.getWord(), "eFactor", wordbookWord.getEFactor().toString()))
                    .toList();
            if(textList.isEmpty()){
                return ApiResponse.ofMessage(400, "今天没有单词需要复习");
            }

            String text = JSON.toJSONString(textList);

            String jsonString = QwenUtil.generateReadingText(text);
            // 可能有多余的内容，只需要[]和里面的内容是json数组
            int startIndex = jsonString.indexOf("[");
            int endIndex = jsonString.lastIndexOf("]") + 1;

            // 检查索引范围
            if (startIndex != -1 && startIndex < endIndex && endIndex <= jsonString.length()) {
                jsonString = jsonString.substring(startIndex, endIndex);
            } else {
                throw new IllegalArgumentException("生成的文本不包含有效的 JSON 数组");
            }
            jsonString=jsonString.replaceAll("english","English");
            jsonString=jsonString.replaceAll("chinese","Chinese");

            // 转化为json对象
            JSONArray jsonArray = JSON.parseArray(jsonString);
            WordGenerateHistory wordGenerateHistory = new WordGenerateHistory();
            wordGenerateHistory.setGenerateText(jsonString);
            wordGenerateHistory.setUserId(sysUser.getUserId());
            wordGenerateHistory.setTimestamp(new Timestamp(System.currentTimeMillis()));

            // 保存生成历史记录到数据库
            wordBookMapper.insertWordGenerateHistory(wordGenerateHistory);

            return ApiResponse.ofSuccess(jsonArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.ofMessage(400, "生成失败,尝试重新生成");
        }
    }

    @Override
    public ApiResponse getHistoryRecentDays() {
        SysUser sysUser = UserContextUtil.getCurrentUser();
        List<String> recentSevenDays = wordBookMapper.getRecentSevenDays(sysUser.getUserId());
        return ApiResponse.ofSuccess(recentSevenDays);
    }

    @Override
    public ApiResponse getHistoryByDay(String day) {
        SysUser sysUser = UserContextUtil.getCurrentUser();
        String history = wordBookMapper.getHistoryByDay(sysUser.getUserId(), day);
        JSONArray jsonArray = JSON.parseArray(history);
        return ApiResponse.ofSuccess(jsonArray);
    }

    @Override
    public ApiResponse getWordLearnHistory() {
        SysUser sysUser = UserContextUtil.getCurrentUser();
        List<DailyLearningStats> wordGenerateHistoryList = wordBookMapper.getWordLearnHistory(sysUser.getUserId());
        return ApiResponse.ofSuccess(wordGenerateHistoryList);
    }

    @Override
    public ApiResponse getWordMastery() {
        SysUser sysUser = UserContextUtil.getCurrentUser();
        List<WordMasteryVo> wordMasteryVoList = wordBookMapper.getWordMastery(sysUser.getUserId());
        return ApiResponse.ofSuccess(wordMasteryVoList);
    }


}
