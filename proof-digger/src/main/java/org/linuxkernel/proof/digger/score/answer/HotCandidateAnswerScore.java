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
import java.util.Map;

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
 * 对候选答案进行评分 【热词评分组件】 热词的长度要大于一 
 * 先找出问题中词频最高的词 
 * 然后找出离这个词最近的候选答案 
 * 候选答案的分值翻倍
 *
 * @author 杨尚川
 */
public class HotCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(HotCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Issue question, Proof evidence, SolutionCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("热词评分开始");
        Solution bestCandidateAnswer = null;
        int miniDistance = Integer.MAX_VALUE;
        //1、对证据进行分词
        List<Term> terms = Tools.getTerms(evidence.getTitle() + "," + evidence.getSnippet());
        //2、找出热词
        Map.Entry<String, Integer> hot = question.getHot();
        if (hot == null) {
            LOG.debug("热词评分失败，未能找出热词");
            return;
        }
        LOG.debug("热词：" + hot.getKey() + " " + hot.getValue());
        //3、找出热词的位置数组
        List<Integer> hotTermOffes = new ArrayList<>();
        for (Term term : terms) {
            if (term.getName().equals(hot.getKey())) {
                hotTermOffes.add(term.getOffe());
            }
        }
        for (Solution candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            //4、找出候选答案的位置数组
            List<Integer> candidateAnswerOffes = new ArrayList<>();
            for (Term term : terms) {
                if (term.getName().equals(candidateAnswer.getAnswer())) {
                    candidateAnswerOffes.add(term.getOffe());
                }
            }
            //5、计算热词和候选答案的最近距离
            for (int candidateAnswerOffe : candidateAnswerOffes) {
                for (int hotTermOffe : hotTermOffes) {
                    int abs = Math.abs(candidateAnswerOffe - hotTermOffe);
                    if (miniDistance > abs) {
                        miniDistance = abs;
                        bestCandidateAnswer = candidateAnswer;
                    }
                }
            }
        }
        if (bestCandidateAnswer != null && miniDistance > 0) {
            LOG.debug("miniDistance:" + miniDistance);
            double score = bestCandidateAnswer.getScore();
            score *= scoreWeight.getHotCandidateAnswerScoreWeight();
            LOG.debug("候选答案 " + bestCandidateAnswer.getAnswer() + " 分值：" + score);
            bestCandidateAnswer.addScore(score);
        } else {
            LOG.debug("没有最佳候选答案");
        }
        LOG.debug("热词评分结束");
        LOG.debug("*************************");
    }
}