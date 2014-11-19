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

package org.linuxkernel.proof.digger.questiontypeanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.model.Type;
import org.linuxkernel.proof.digger.questiontypeanalysis.patternbased.DefaultPatternMatchResultSelector;
import org.linuxkernel.proof.digger.questiontypeanalysis.patternbased.PatternBasedMultiLevelQuestionClassifier;
import org.linuxkernel.proof.digger.questiontypeanalysis.patternbased.PatternMatchResultSelector;
import org.linuxkernel.proof.digger.questiontypeanalysis.patternbased.PatternMatchStrategy;
import org.linuxkernel.proof.digger.questiontypeanalysis.patternbased.QuestionPattern;
import org.linuxkernel.proof.digger.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据预先标注的语料来判断【模式识别】的准确性
 *
 * @author 杨尚川
 */
public class ValidateClassifier {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateClassifier.class);

    private static PatternBasedMultiLevelQuestionClassifier questionClassifier = null;
    private static final List<String> no = new ArrayList<>();
    private static final List<String> wrong = new ArrayList<>();
    private static final List<String> right = new ArrayList<>();
    private static final List<String> unknown = new ArrayList<>();

    static {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");
        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();
        questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);
    }

    private static void validate(String filePrefix, Type rightQuestionType) {
        String file = "/org/apdplat.qa/questiontypeanalysis/" + filePrefix + "_name_questions.txt";
        Set<String> questions = Tools.getIssues(file);
        LOG.info("从文件中加载" + questions.size() + "个问题：" + file);

        for (String q : questions) {
            q = q.split(":")[0];
            Issue question = questionClassifier.classify(q);
            if (question == null) {
                no.add(rightQuestionType + "不能识别：" + q);
            } else if (question.getQuestionType() != rightQuestionType) {
                wrong.add(rightQuestionType + "识别不正确: " + q + " type:" + question.getQuestionType().name() + " 候选类型：" + question.getCandidateQuestionTypes());
            } else if (question.getQuestionType() == rightQuestionType) {
                right.add(rightQuestionType + "识别正确: " + q + " type:" + question.getQuestionType().name());
            } else {
                unknown.add(rightQuestionType + "未知情况: " + q);
            }
        }
    }

    private static void validate() {
        validate("person", Type.PERSON_NAME);
        validate("location", Type.LOCATION_NAME);
        validate("organization", Type.ORGANIZATION_NAME);
        validate("number", Type.NUMBER);
        validate("time", Type.TIME);

        int total = no.size() + right.size() + wrong.size() + unknown.size();
        LOG.info("识别总数: " + total);
        LOG.info("识别正确数: " + right.size());
        for (String item : right) {
            LOG.info(item);
        }
        LOG.info("识别错误数: " + wrong.size());
        for (String item : wrong) {
            LOG.info(item);
        }
        LOG.info("不能识别数: " + no.size());
        for (String item : no) {
            LOG.info(item);
        }
        if (unknown.size() > 0) {
            LOG.info("未知情况: " + unknown.size());
            for (String item : unknown) {
                LOG.info(item);
            }
        }
        LOG.info("问题分类识别统计");
        LOG.info("识别总数: " + total);
        LOG.info("识别正确数: " + right.size());
        LOG.info("识别错误数: " + wrong.size());
        LOG.info("不能识别数: " + no.size());
        LOG.info("识别正确率: " + (double) right.size() / total * 100 + "%");
        LOG.info("识别错误率: " + (double) wrong.size() / total * 100 + "%");
        LOG.info("不能识别率: " + (double) no.size() / total * 100 + "%");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        validate();
    }
}