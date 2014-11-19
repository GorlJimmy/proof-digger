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

import org.linuxkernel.proof.digger.model.SolutionCollection;
import org.linuxkernel.proof.digger.model.Proof;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.ScoreWeight;

/**
 * 候选答案评分组件
 *
 * @author 杨尚川
 */
public interface CandidateAnswerScore {

    /**
     * 对候选答案进行评分 候选答案的分值存储在CandidateAnswer的实例里面
     *
     * @param question 问题
     * @param evidence 证据
     * @param candidateAnswerCollection 候选答案集合
     */
    public void score(Issue question, Proof evidence, SolutionCollection candidateAnswerCollection);

    /**
     * 评分组件权重
     *
     * @param scoreWeight
     */
    public void setScoreWeight(ScoreWeight scoreWeight);
}