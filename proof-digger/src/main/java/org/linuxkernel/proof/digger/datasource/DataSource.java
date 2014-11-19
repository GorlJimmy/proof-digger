
package org.linuxkernel.proof.digger.datasource;

import java.util.List;

import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.QuestionAnsweringSystem;

public interface DataSource {

    /**
     * 获取多个问题以及问题的多个证据
     *
     * @return 多个问题以及问题的多个证据
     */
    public List<Issue> getQuestions();

    /**
     * 获取问题以及问题的多个证据
     *
     * @param questionStr 问题
     * @return 问题以及问题的多个证据
     */
    public Issue getQuestion(String questionStr);

    /**
     * 边获取问题边进行解答
     *
     * @param questionAnsweringSystem
     * @return
     */
    public List<Issue> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem);

    /**
     * 获取问题并进行解答
     *
     * @param questionStr
     * @param questionAnsweringSystem
     * @return
     */
    public Issue getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem);

}