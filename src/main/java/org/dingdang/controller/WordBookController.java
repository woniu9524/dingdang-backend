package org.dingdang.controller;

import lombok.AllArgsConstructor;
import org.dingdang.common.ApiResponse;
import org.dingdang.common.annotations.NoAuth;
import org.dingdang.common.utils.UserContextUtil;
import org.dingdang.domain.SysUser;
import org.dingdang.domain.UserWordBook;
import org.dingdang.domain.WordBooksMst;
import org.dingdang.domain.vo.WordbooksMstVo;
import org.dingdang.service.WordBookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/9 1:43 星期四
 * @Description:
 */

@RestController
@RequestMapping("v1/wordbook")
@AllArgsConstructor
public class WordBookController {

    private final WordBookService wordBookService;

    // 获取单词书列表
    @GetMapping("/list")
    public ApiResponse<List<WordbooksMstVo>> getWordBookList() {
        SysUser currentUser = UserContextUtil.getCurrentUser();
        return ApiResponse.ofSuccess(wordBookService.getWordBookList());
    }

    // 使用单词书（传入bookNo）
    @GetMapping("/use")
    public ApiResponse useWordBook(@RequestParam("bookNo") Long bookNo) {

        return wordBookService.useWordBook(bookNo);
    }

    // 停用单词书（传入bookNo）
    @GetMapping("/stopUse")
    public ApiResponse stopUseWordBook(@RequestParam("bookNo") Long bookNo) {

        return wordBookService.stopUseWordBook(bookNo);
    }

    // 获取单词书中的单词
    @GetMapping("/wordList")
    public ApiResponse getWordList() {
        return wordBookService.getWordList();
    }

    // 单词掌握程度打分
    @GetMapping("/score")
    public ApiResponse scoreWord(@RequestParam("word") String word, @RequestParam("grade") Integer grade) {
        return wordBookService.scoreWord(word, grade);
    }

    // 分页查询需要学习的单词
    @GetMapping("/wordNeedLearn")
    public ApiResponse getWordNeedLearn(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return wordBookService.getWordNeedLearn(page, size);
    }

    // 查询当天需要复习的单词列表
    @GetMapping("/wordReviewToday")
    public ApiResponse getWordReviewToday() {
        return wordBookService.getWordReviewToday();
    }

    // 获取今日学习数量和复习的数量
    @GetMapping("/todayLearnCount")
    public ApiResponse getTodayLearnCount() {
        return wordBookService.getTodayLearnCount();
    }

    // 阅读文本生成
    @GetMapping("/textGeneration")
    public ApiResponse textGeneration() {
        return wordBookService.textGeneration();
    }

    // 获取历史记录最近七个日期
    @GetMapping("/history/recentDays")
    public ApiResponse getHistoryRecentDays() {
        return wordBookService.getHistoryRecentDays();
    }

    // 获取某一天的历史记录
    @GetMapping("/history/data")
    public ApiResponse getHistoryByDay(@RequestParam("day") String day) {
        return wordBookService.getHistoryByDay(day);
    }

    // 获取单词学习历史
    @GetMapping("/wordHistory/list")
    public ApiResponse getWordLearnHistory() {
        return wordBookService.getWordLearnHistory();
    }

    // 获取单词掌握情况
    @GetMapping("/wordMastery")
    public ApiResponse getWordMastery() {
        return wordBookService.getWordMastery();
    }







}