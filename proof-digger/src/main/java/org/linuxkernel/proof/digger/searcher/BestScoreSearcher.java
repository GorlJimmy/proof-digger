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

package org.linuxkernel.proof.digger.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linuxkernel.proof.digger.score.answer.CandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.CombinationCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.HotCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.MoreTextualAlignmentCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.NullCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.TermDistanceCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.TermDistanceMiniCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.TermFrequencyCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.answer.TextualAlignmentCandidateAnswerScore;
import org.linuxkernel.proof.digger.score.evidence.BigramEvidenceScore;
import org.linuxkernel.proof.digger.score.evidence.CombinationEvidenceScore;
import org.linuxkernel.proof.digger.score.evidence.EvidenceScore;
import org.linuxkernel.proof.digger.score.evidence.NullEvidenceScore;
import org.linuxkernel.proof.digger.score.evidence.SkipBigramEvidenceScore;
import org.linuxkernel.proof.digger.score.evidence.TermMatchEvidenceScore;
import org.linuxkernel.proof.digger.select.CandidateAnswerSelect;
import org.linuxkernel.proof.digger.select.CommonCandidateAnswerSelect;
import org.linuxkernel.proof.digger.system.CommonIssueSolutionSystem;
import org.linuxkernel.proof.digger.system.IssueSolutionSystem;
import org.linuxkernel.proof.digger.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于寻找最佳评分组件
 *
 * @author 杨尚川
 */
public class BestScoreSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(BestScoreSearcher.class);

    public void search() {
        //候选答案提取器(不可以同时使用多个提取器)
        CandidateAnswerSelect candidateAnswerSelect = new CommonCandidateAnswerSelect();
        //证据评分组件列表
        List<EvidenceScore> evidenceScores = new ArrayList<>();
        evidenceScores.add(new TermMatchEvidenceScore());
        evidenceScores.add(new BigramEvidenceScore());
        evidenceScores.add(new SkipBigramEvidenceScore());
        evidenceScores.add(new CombinationEvidenceScore());
        evidenceScores.add(new NullEvidenceScore());
        //候选答案评分组件列表
        List<CandidateAnswerScore> candidateAnswerScores = new ArrayList<>();
        candidateAnswerScores.add(new TermFrequencyCandidateAnswerScore());
        candidateAnswerScores.add(new TermDistanceCandidateAnswerScore());
        candidateAnswerScores.add(new TermDistanceMiniCandidateAnswerScore());
        candidateAnswerScores.add(new TextualAlignmentCandidateAnswerScore());
        candidateAnswerScores.add(new MoreTextualAlignmentCandidateAnswerScore());
        //candidateAnswerScores.add(new RewindTextualAlignmentCandidateAnswerScore());
        candidateAnswerScores.add(new HotCandidateAnswerScore());
        candidateAnswerScores.add(new CombinationCandidateAnswerScore());
        candidateAnswerScores.add(new NullCandidateAnswerScore());

        Map<String, Double> map = new HashMap<>();

        for (EvidenceScore evidenceScore : evidenceScores) {
            for (CandidateAnswerScore candidateAnswerScore : candidateAnswerScores) {
                //组装问答系统
                IssueSolutionSystem questionAnsweringSystem = new CommonIssueSolutionSystem();
                //1、指定问答系统的 候选答案提取器
                questionAnsweringSystem.setCandidateAnswerSelect(candidateAnswerSelect);
                //2、指定问答系统的 证据评分组件
                questionAnsweringSystem.setEvidenceScore(evidenceScore);
                //3、忽略 候选答案评分组件
                questionAnsweringSystem.setCandidateAnswerScore(candidateAnswerScore);
                //回答问题
                questionAnsweringSystem.answerQuestions();
                //获取MRR值
                double MRR = questionAnsweringSystem.getMRR();

                StringBuilder par = new StringBuilder();
                par.append(evidenceScore.getClass())
                        .append("-")
                        .append(candidateAnswerScore.getClass())
                        .append(" ")
                        .append(":")
                        .append(questionAnsweringSystem.getPerfectCount())
                        .append(":")
                        .append(questionAnsweringSystem.getNotPerfectCount())
                        .append(":")
                        .append(questionAnsweringSystem.getWrongCount());
                map.put(par.toString(), MRR);
            }
        }
        int i = 1;
        List<Map.Entry<String, Double>> entrys = Tools.sortByDoubleValue(map);
        for (Map.Entry<String, Double> entry : entrys) {
            LOG.info((i++) + "、" + entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new BestScoreSearcher().search();
    }
}