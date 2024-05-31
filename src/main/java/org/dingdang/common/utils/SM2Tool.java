package org.dingdang.common.utils;

import org.dingdang.domain.UserWordLearning;
import org.dingdang.mapper.WordBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/18 3:07 星期六
 * @Description: SM2算法工具类，用于管理用户单词的复习计划
 */
@Component
public class SM2Tool {

    @Autowired
    private WordBookMapper wordBookMapper;

    /**
     * 更新单词的复习数据
     *
     * @param userId 用户ID
     * @param word   单词
     * @param grade  用户对单词记忆的评分
     */
    public void updateReview(Long userId, String word, int grade,Long bookNo) {


        // 从数据库中查询单词信息
        UserWordLearning wordLearning = wordBookMapper.selectWord(userId, word,bookNo);

        // 补充数据统计
        if (wordLearning == null){
            wordBookMapper.upsertDailyLearnCount(userId);
        }else{
            wordBookMapper.upsertDailyReviewCount(userId);
        }

        // 如果评分为6，表示删除，不需要继续学习
        if (grade == 6) {
            // 设置need_review为0，不再需要复习
            wordBookMapper.upsertNeedReview(userId, word,bookNo, 0,6,1);
            return;
        }

        // 如果查询结果为空，说明这是第一次学习该单词
        if (wordLearning == null) {
            wordLearning = new UserWordLearning();
            wordLearning.setUserId(userId);
            wordLearning.setWord(word);
            wordLearning.setEFactor(2.5); // 初始EFactor值
            wordLearning.setInterval(0);
            wordLearning.setReviewCount(0);
            wordLearning.setNeedReview(1); // 初次学习需要复习
            wordLearning.setBookNo(bookNo);
        }

        // 获取单词的当前EFactor、复习间隔和复习次数
        double eFactor = wordLearning.getEFactor();
        int interval = Math.toIntExact(wordLearning.getInterval());
        int reviewCount = Math.toIntExact(wordLearning.getReviewCount());

        // 根据评分调整复习间隔和EFactor
        if (grade >= 3) {
            if (reviewCount == 0) {
                interval = 1; // 第一次复习间隔为1天
            } else if (reviewCount == 1) {
                interval = 6; // 第二次复习间隔为6天
            } else {
                interval = (int) Math.round(interval * eFactor); // 之后的复习间隔为上一次间隔乘以EFactor
            }
            // 调整EFactor，EFactor最小值为1.3
            eFactor = Math.max(1.3, eFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02)));
        } else {
            interval = 1; // 如果评分低于3，下次复习间隔设为1天
        }

        // 计算下次复习的日期
        LocalDateTime nextReviewDate = LocalDateTime.now().plusDays(interval);
        reviewCount++; // 复习次数加1

        // 更新单词的复习数据
        wordLearning.setEFactor(eFactor);
        wordLearning.setInterval(interval);
        wordLearning.setReviewCount(reviewCount);
        wordLearning.setLastReviewDate(Timestamp.valueOf(LocalDateTime.now()));
        wordLearning.setNextReviewDate(Timestamp.valueOf(nextReviewDate));
        wordLearning.setLastGrade(grade);
        wordLearning.setNeedReview(1); // 设置为需要复习

        // 将更新后的数据存回数据库
        wordBookMapper.upsertWord(wordLearning);
    }

    /**
     * 检查单词是否需要复习
     *
     * @param userId 用户ID
     * @param word   单词
     * @return 是否需要复习
     */
//    public boolean isDueForReview(Long userId, String word) {
//        UserWordLearning wordLearning = wordBookMapper.selectWord(userId, word);
//
//        if (wordLearning != null) {
//            LocalDateTime nextReviewDate = wordLearning.getNextReviewDate().toLocalDateTime();
//            return LocalDateTime.now().isAfter(nextReviewDate);
//        }
//
//        return false;
//    }

    /**
     * 获取用户需要复习的全部单词
     *
     * @param userId 用户ID
     * @return 需要复习的单词列表
     */
    public List<String> getAllDueWords(Long userId) {
        return wordBookMapper.selectAllDueWords(userId, Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * 复习所有到期的单词
     *
     * @param userId 用户ID
     */
//    public void reviewDueWords(Long userId) {
//        List<String> dueWords = getAllDueWords(userId);
//
//        Scanner scanner = new Scanner(System.in);
//
//        for (String word : dueWords) {
//            System.out.println("Review word: " + word);
//            System.out.println("Rate your memory (0-5): ");
//            int grade = scanner.nextInt();
//            updateReview(userId, word, grade);
//        }
//    }
}
