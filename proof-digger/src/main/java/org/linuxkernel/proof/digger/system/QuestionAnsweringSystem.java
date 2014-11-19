/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.linuxkernel.proof.digger.system;

import java.util.List;

import org.linuxkernel.proof.digger.datasource.DataSource;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.questiontypeanalysis.QuestionClassifier;
import org.linuxkernel.proof.digger.score.answer.CandidateAnswerScore;
import org.linuxkernel.proof.digger.score.evidence.EvidenceScore;
import org.linuxkernel.proof.digger.select.CandidateAnswerSelect;

/**
 * 问答系统
 *
 * @author 杨尚川
 */
public interface QuestionAnsweringSystem {

    /**
     * 问答系统使用的分类器
     *
     * @param questionClassifier
     */
    public void setQuestionClassifier(QuestionClassifier questionClassifier);

    public QuestionClassifier getQuestionClassifier();

    /**
     * 问答系统使用的数据源
     *
     * @param dataSource 数据源
     */
    public void setDataSource(DataSource dataSource);

    public DataSource getDataSource();

    /**
     * 候选答案提取器(不可以同时使用多个提取器)
     *
     * @param candidateAnswerSelect 候选答案提取组件
     */
    public void setCandidateAnswerSelect(CandidateAnswerSelect candidateAnswerSelect);

    public CandidateAnswerSelect getCandidateAnswerSelect();

    /**
     * 候选答案评分组件(可以同时使用多个组件 - 组合候选答案评分组件)
     *
     * @param candidateAnswerScore 候选答案评分组件
     */
    public void setCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore);

    public CandidateAnswerScore getCandidateAnswerScore();

    /**
     * 证据评分组件(可以同时使用多个组件 - 组合证据评分组件)
     *
     * @param evidenceScore 证据评分组件
     */
    public void setEvidenceScore(EvidenceScore evidenceScore);

    public EvidenceScore getEvidenceScore();

    /**
     * 回答问题，问题从问答系统使用的数据源中读取
     *
     * @return
     */
    public List<Issue> answerQuestions();

    /**
     * 利用dataSource搜索问题并回答问题
     *
     * @param questionStr 问题字符串
     * @return 问题
     */
    public Issue answerQuestion(String questionStr);

    /**
     * 回答指定的问题
     *
     * @param question 问题
     * @return
     */
    public Issue answerQuestion(Issue question);

    /**
     * 回答指定的多个问题
     *
     * @param questions 多个问题
     * @return
     */
    public List<Issue> answerQuestions(List<Issue> questions);

    /**
     * 输出回答完美的问题
     */
    public void showPerfectQuestions();

    /**
     * 输出回答不完美的问题
     */
    public void showNotPerfectQuestions();

    /**
     * 输出回答错误的问题
     */
    public void showWrongQuestions();

    /**
     * 输出未知类型的问题
     */
    public void showUnknownTypeQuestions();

    /**
     * 获取回答完美的问题
     *
     * @return
     */
    public List<Issue> getPerfectQuestions();

    /**
     * 获取回答不完美的问题
     *
     * @return
     */
    public List<Issue> getNotPerfectQuestions();

    /**
     * 获取回答错误的问题
     *
     * @return
     */
    public List<Issue> getWrongQuestions();

    /**
     * 获取未知类型的问题
     *
     * @return
     */
    public List<Issue> getUnknownTypeQuestions();

    /**
     * 获取问答系统的MRR指标
     *
     * @return
     */
    public double getMRR();

    /**
     * 获取回答问题数（有预期答案的问题）
     *
     * @return 回答问题数
     */
    public int getQuestionCount();

    /**
     * 获取回答完美问题数
     *
     * @return 回答完美问题数
     */
    public int getPerfectCount();

    /**
     * 获取回答不完美问题数
     *
     * @return 回答不完美问题数
     */
    public int getNotPerfectCount();

    /**
     * 获取回答错误问题数
     *
     * @return 回答错误问题数
     */
    public int getWrongCount();

    /**
     * 获取未知类型问题数
     *
     * @return 未知类型问题数
     */
    public int getUnknownTypeCount();
}