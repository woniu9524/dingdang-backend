package org.dingdang.service;

import org.dingdang.common.ApiResponse;
import org.dingdang.domain.WordBooksMst;
import org.dingdang.domain.vo.WordbooksMstVo;

import java.util.List;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/9 1:44 星期四
 * @Description:
 */
public interface WordBookService {


    List<WordbooksMstVo> getWordBookList();

    ApiResponse useWordBook(Long bookNo);

    ApiResponse stopUseWordBook(Long bookNo);

    ApiResponse getWordList();

    ApiResponse scoreWord(String word, Integer grade);

    ApiResponse getWordNeedLearn(Integer page, Integer size);

    ApiResponse getWordReviewToday();

    ApiResponse getTodayLearnCount();

    ApiResponse textGeneration();

    ApiResponse getHistoryRecentDays();

    ApiResponse getHistoryByDay(String day);

    ApiResponse getWordLearnHistory();

    ApiResponse getWordMastery();
}