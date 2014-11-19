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

package org.linuxkernel.proof.digger.filter;

import java.util.Iterator;
import java.util.List;

import org.linuxkernel.proof.digger.model.Solution;
import org.linuxkernel.proof.digger.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 如果候选答案出现在问题中，则过滤
 *
 * @author 杨尚川
 */
public class CandidateAnswerCanNotInQustionFilter implements CandidateAnswerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CandidateAnswerCanNotInQustionFilter.class);

    @Override
    public void filter(Issue question, List<Solution> candidateAnswers) {
        //对问题分词
        List<String> questionTerms = question.getTerms();
        StringBuilder str = new StringBuilder();
        str.append("对问题分词: ");
        for (String questionTerm : questionTerms) {
            str.append(questionTerm).append(" ");
        }
        LOG.debug(str.toString());
        //答案不能在问题中，去掉
        Iterator<Solution> iterator = candidateAnswers.iterator();
        while (iterator.hasNext()) {
            Solution candidateAnswer = iterator.next();
            if (questionTerms.contains(candidateAnswer.getAnswer())) {
                iterator.remove();
                LOG.debug("去掉问题中的词：" + candidateAnswer.getAnswer());
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}