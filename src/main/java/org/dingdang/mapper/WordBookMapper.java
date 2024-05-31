package org.dingdang.mapper;

import org.apache.ibatis.annotations.*;
import org.dingdang.domain.*;
import org.dingdang.domain.vo.WordMasteryVo;
import org.dingdang.domain.vo.WordbookWord;
import org.dingdang.domain.vo.WordbooksMstVo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:59 星期二
 * @Description:
 */
@Mapper
public interface WordBookMapper {

    List<WordbooksMstVo> getWordBookList(Long userId);


    int useWordBook(UserWordBook userWordBook);

    int stopUseWordBook(UserWordBook userWordBook);

    // 获取单词书中的单词
    List<WordbookWord> getWordList(@Param("userId") Long userId);

    // 查询单词信息
    @Select("SELECT * FROM user_word_learning WHERE user_id = #{userId} AND word = #{word} AND book_no=#{bookNo}")
    UserWordLearning selectWord(@Param("userId") Long userId, @Param("word") String word, @Param("bookNo") Long bookNo);

    // 更新单词复习信息
    @Insert("INSERT INTO user_word_learning (user_id, word, e_factor, interval, review_count, last_review_date, next_review_date, last_grade, need_review,book_no) VALUES (#{userId}, #{word}, #{eFactor}, #{interval}, #{reviewCount}, #{lastReviewDate}, #{nextReviewDate}, #{lastGrade}, #{needReview},#{bookNo}) ON CONFLICT (user_id, word) DO UPDATE SET e_factor = EXCLUDED.e_factor, interval = EXCLUDED.interval, review_count = EXCLUDED.review_count, last_review_date = EXCLUDED.last_review_date, next_review_date = EXCLUDED.next_review_date, last_grade = EXCLUDED.last_grade, need_review = EXCLUDED.need_review,book_no=EXCLUDED.book_no")
    void upsertWord(UserWordLearning wordLearning);

    // 更新单词的need_review状态
    @Insert("INSERT INTO user_word_learning (user_id, word, book_no, need_review, last_grade, review_count) VALUES (#{userId}, #{word}, #{bookNo}, #{needReview}, #{lastGrade}, #{reviewCount}) ON CONFLICT (user_id, word) DO UPDATE SET book_no = EXCLUDED.book_no, need_review = EXCLUDED.need_review, last_grade = EXCLUDED.last_grade, review_count = EXCLUDED.review_count")
    void upsertNeedReview(@Param("userId") Long userId, @Param("word") String word, @Param("bookNo") Long bookNo, @Param("needReview") int needReview, @Param("lastGrade") int lastGrade, @Param("reviewCount") int reviewCount);

    // 查询需要复习的单词
    @Select("SELECT word FROM user_word_learning WHERE user_id = #{userId} AND need_review = 1 AND next_review_date <= #{currentDate}")
    List<String> selectAllDueWords(@Param("userId") Long userId, @Param("currentDate") Timestamp currentDate);

    List<WordbookWord> getWordsToLearn(Long userId, int offset, Integer size);

    List<WordbookWord> getWordReviewToday(Long userId);

    Map<String, Integer> getTodayLearnCount(Long userId);

    void insertWordGenerateHistory(WordGenerateHistory wordGenerateHistory);

    // 查询是否存在生成记录
    @Select("SELECT EXISTS (SELECT 1 FROM word_generate_history WHERE DATE(timestamp) = CURRENT_DATE and user_id=#{userId})  AS has_generate_today;")
    boolean hasGenerateToday(Long userId);

    // 查询用户生成的历史记录的最近七个日期
    @Select("SELECT date FROM (SELECT DISTINCT DATE(timestamp) AS date FROM word_generate_history WHERE user_id=#{userId}) AS unique_dates ORDER BY date DESC LIMIT 7")
    List<String> getRecentSevenDays(Long userId);


    @Select("SELECT generate_text FROM word_generate_history WHERE DATE(timestamp) = DATE(#{day}) AND user_id=#{userId} ORDER BY timestamp DESC limit 1")
    String getHistoryByDay(Long userId, String day);

    void upsertDailyLearnCount(Long userId);

    void upsertDailyReviewCount(Long userId);

    @Select("SELECT * FROM daily_learning_stats where user_id=#{userId} AND EXTRACT(YEAR FROM learning_date) = EXTRACT(YEAR FROM CURRENT_DATE) ORDER BY learning_date")
    List<DailyLearningStats> getWordLearnHistory(Long userId);

    List<WordMasteryVo> getWordMastery(Long userId);

    void updateWordLearnBookNo(Long userId, Long bookNo);

    @Select("SELECT book_no FROM user_word_books WHERE user_id=#{userId}")
    Long getWordLearnBookNo(Long userId);
}