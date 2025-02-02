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

package org.linuxkernel.proof.digger.datasource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.linuxkernel.proof.digger.files.FilesConfig;
import org.linuxkernel.proof.digger.model.Proof;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.CommonIssueSolutionSystem;
import org.linuxkernel.proof.digger.system.IssueSolutionSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从特定格式的文本文件中检索问题及其对应的证据
 *
 * @author 杨尚川
 */
public class FileDataSource implements DataSource {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataSource.class);

    private List<String> files = new ArrayList<>();

    public FileDataSource(String file) {
        this.files.add(file);
    }

    public FileDataSource(List<String> files) {
        this.files.addAll(files);
    }

    @Override
	public Issue getIssue(String questionStr) {
		return getAndAnswerQuestion(questionStr, null);
	}

	@Override
	public List<Issue> getIssues() {
		return getAndAnswerQuestions(null);
	}
    @Override
    public Issue getAndAnswerQuestion(String questionStr, IssueSolutionSystem questionAnsweringSystem) {
        for (Issue question : getIssues()) {
            String q = question.getIssue().trim().replace("?", "").replace("？", "");
            questionStr = questionStr.trim().replace("?", "").replace("？", "");
            if (q.equals(questionStr)) {
                //回答问题
                if (questionAnsweringSystem != null) {
                    questionAnsweringSystem.answerQuestion(question);
                }
                return question;
            }
        }
        return null;
    }

    @Override
    public List<Issue> getAndAnswerQuestions(IssueSolutionSystem questionAnsweringSystem) {
        List<Issue> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));
                Issue question = null;
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    if (line.trim().startsWith("?") || line.indexOf("?") == 1) {
                        //在构造下一个问题之前回答上一个问题（好好体会，结合文件格式）
                        //回答问题
                        if (questionAnsweringSystem != null && question != null) {
                            questionAnsweringSystem.answerQuestion(question);
                        }
                        String qs = line.substring(line.indexOf(".") + 1).trim();

                        String questionStr = null;
                        String expectAnswer = null;
                        String[] attrs = qs.split("[:|：]");
                        if (attrs == null) {
                            questionStr = qs;
                        }
                        if (attrs != null && attrs.length == 1) {
                            questionStr = attrs[0];
                        }
                        if (attrs != null && attrs.length == 2) {
                            questionStr = attrs[0];
                            expectAnswer = attrs[1];
                        }
                        LOG.info("Question:" + questionStr);
                        LOG.info("ExpectAnswer:" + expectAnswer);

                        question = new Issue();
                        question.setIssue(questionStr);
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    Proof answer = new Proof();
                    if (line.startsWith("Title:")) {
                        answer.setTitle(line.substring(6).trim());
                    }
                    //读下一行
                    line = reader.readLine();
                    if (line.startsWith("Snippet:")) {
                        answer.setSnippet(line.substring(8).trim());
                    }
                    if (answer.getTitle() != null && answer.getSnippet() != null && question != null) {
                        question.addEvidence(answer);
                    }
                    //读下一行
                    line = reader.readLine();
                }

                //回答最后一个问题
                if (questionAnsweringSystem != null && question != null) {
                    questionAnsweringSystem.answerQuestion(question);
                }
            } catch (FileNotFoundException e) {
                LOG.error("文件找不到", e);
            } catch (UnsupportedEncodingException e) {
                LOG.error("编码错误", e);
            } catch (IOException e) {
                LOG.error("IO错误", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOG.error("关闭文件错误", e);
                    }
                }
            }
        }
        return questions;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
        List<Issue> questions = dataSource.getIssues();
        for (Issue question : questions) {
            LOG.info(question.toString());
        }
        Issue question = dataSource.getIssue("APDPlat的发起人是谁？");
        IssueSolutionSystem questionAnsweringSystem = new CommonIssueSolutionSystem();
        questionAnsweringSystem.answerQuestion(question);
    }
}