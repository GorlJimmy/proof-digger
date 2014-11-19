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

package org.linuxkernel.proof.digger.select;

import java.util.List;

import org.ansj.domain.Term;
import org.linuxkernel.proof.digger.datasource.DataSource;
import org.linuxkernel.proof.digger.datasource.FileDataSource;
import org.linuxkernel.proof.digger.files.FilesConfig;
import org.linuxkernel.proof.digger.model.Solution;
import org.linuxkernel.proof.digger.model.SolutionCollection;
import org.linuxkernel.proof.digger.model.Proof;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.parser.WordSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用候选答案提取组件
 *
 * @author 杨尚川
 */
public class CommonCandidateAnswerSelect implements CandidateAnswerSelect {

    private static final Logger LOG = LoggerFactory.getLogger(CommonCandidateAnswerSelect.class);

    @Override
    public void select(Issue question, Proof evidence) {
        SolutionCollection candidateAnswerCollection = new SolutionCollection();

        List<Term> terms = WordSegment.parse(evidence.getTitle() + evidence.getSnippet());
        for (Term term : terms) {
            if (term.getNatrue().natureStr.startsWith(question.getQuestionType().getNature()) && term.getName().length() > 1) {
                Solution answer = new Solution();
                answer.setAnswer(term.getName());
                candidateAnswerCollection.addAnswer(answer);
            }
        }
        evidence.setCandidateAnswerCollection(candidateAnswerCollection);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
        List<Issue> questions = dataSource.getIssues();

        CommonCandidateAnswerSelect commonCandidateAnswerSelect = new CommonCandidateAnswerSelect();
        int i = 1;
        for (Issue question : questions) {
            LOG.info("Question " + (i++) + ": " + question.getIssue());
            int j = 1;
            for (Proof evidence : question.getEvidences()) {
                LOG.info("	Evidence " + j + ": ");
                LOG.info("		Title: " + evidence.getTitle());
                LOG.info("		Snippet: " + evidence.getSnippet());
                LOG.info("	Evidence " + j + " 候选答案: ");
                commonCandidateAnswerSelect.select(question, evidence);
                for (Solution candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                    LOG.info("			" + candidateAnswer.getAnswer() + " : " + candidateAnswer.getScore());
                }
                j++;
                LOG.info("------------------------------------------------");
            }
            LOG.info("------------------------------------------------");
            LOG.info("");
        }
    }
}