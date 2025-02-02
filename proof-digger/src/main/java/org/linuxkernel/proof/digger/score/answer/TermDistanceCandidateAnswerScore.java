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

package org.linuxkernel.proof.digger.score.answer;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.linuxkernel.proof.digger.model.Solution;
import org.linuxkernel.proof.digger.model.SolutionCollection;
import org.linuxkernel.proof.digger.model.Proof;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.ScoreWeight;
import org.linuxkernel.proof.digger.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对候选答案进行评分 【词距评分组件】 分值+=原分值*（1/词距）
 *
 * @author 杨尚川
 */
public class TermDistanceCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(TermDistanceCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Issue question, Proof evidence, SolutionCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("词距评分开始");
        //1、对问题进行分词
        List<String> questionTerms = question.getTerms();
        //2、对证据进行分词
        List<Term> terms = Tools.getTerms(evidence.getTitle() + "," + evidence.getSnippet());
        for (Solution candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            //3、计算候选答案的词距
            int distance = 0;
            LOG.debug("计算候选答案 " + candidateAnswer.getAnswer() + " 的词距");
            //3.1 计算candidateAnswer的分布
            List<Integer> candidateAnswerOffes = new ArrayList<>();
            for (Term term : terms) {
                if (term.getName().equals(candidateAnswer.getAnswer())) {
                    candidateAnswerOffes.add(term.getOffe());
                }
            }
            for (String questionTerm : questionTerms) {
                //3.2 计算questionTerm的分布
                List<Integer> questionTermOffes = new ArrayList<>();
                for (Term term : terms) {
                    if (term.getName().equals(questionTerm)) {
                        questionTermOffes.add(term.getOffe());
                    }
                }
                //3.3 计算candidateAnswer和questionTerm的词距
                for (int candidateAnswerOffe : candidateAnswerOffes) {
                    for (int questionTermOffe : questionTermOffes) {
                        distance += Math.abs(candidateAnswerOffe - questionTermOffe);
                    }
                }
            }
            double score = candidateAnswer.getScore() / distance;
            score *= scoreWeight.getTermDistanceCandidateAnswerScoreWeight();
            LOG.debug("词距:" + distance + " ,分值：" + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("词距评分结束");
        LOG.debug("*************************");
    }
}