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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.linuxkernel.proof.digger.config.Config;
import org.linuxkernel.proof.digger.files.FilesConfig;
import org.linuxkernel.proof.digger.model.Proof;
import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.IssueSolutionSystem;
import org.linuxkernel.proof.digger.util.MySQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从Baidu搜索问题的证据
 *
 * @author 杨尚川
 */
public class BaiduDataSource implements DataSource {

	private static final Logger LOG = LoggerFactory.getLogger(BaiduDataSource.class);

	private static final int PAGE = 1;
	private static final int PAGESIZE = 10;
	private final List<String> files = new ArrayList<>();

	public BaiduDataSource() {
	}

	public BaiduDataSource(String file) {
		this.files.add(file);
	}

	public BaiduDataSource(List<String> files) {
		this.files.addAll(files);
	}

	@Override
	public Issue getIssue(String str_issue) {
		return getAndAnswerQuestion(str_issue, null);
	}

	@Override
	public List<Issue> getIssues() {
		return getAndAnswerQuestions(null);
	}

	@Override
	public List<Issue> getAndAnswerQuestions(IssueSolutionSystem questionAnsweringSystem) {
		List<Issue> issues = new ArrayList<>();

		for (String file : files) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));) {
				String line = reader.readLine();
				while (line != null) {
					if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
						line = reader.readLine();
						continue;
					}
					String str_issue = null;
					String str_solution = null;
					String[] pairs_attrs = line.trim().split("[:|：]");

					if (pairs_attrs != null && pairs_attrs.length == 2) {
						str_issue = pairs_attrs[0];
						str_solution = pairs_attrs[1];
					}
					LOG.info("Issue: " + str_issue);
					LOG.info("Solution: " + str_solution);
					Issue issue = getIssue(str_issue);
					if (issue != null) {
						issue.setExpectAnswer(str_solution);
						issues.add(issue);
					}

					if (questionAnsweringSystem != null && issue != null) {
						questionAnsweringSystem.answerQuestion(issue);
					}

					line = reader.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return issues;
	}

	@Override
	public Issue getAndAnswerQuestion(String questionStr, IssueSolutionSystem questionAnsweringSystem) {
		// 1、先从本地缓存里面找
//		Issue question = MySQLUtils.getQuestionFromDatabase("baidu:", questionStr);
//		if (question != null) {
//			// 数据库中存在
//			LOG.info("从数据库中查询到Question：" + question.getIssue());
//			// 回答问题
//			if (questionAnsweringSystem != null) {
//				questionAnsweringSystem.answerQuestion(question);
//			}
//			return question;
//		}
//		// 2、本地缓存里面没有再查询baidu
		Issue question = new Issue();
		question.setIssue(questionStr);

		String query = "";
		try {
			query = URLEncoder.encode(question.getIssue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("url构造失败", e);
			return null;
		}
		String referer = "http://www.baidu.com/";
		for (int i = 0; i < PAGE; i++) {
			query = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + query + "&oq=" + query + "&usm=3&f=8&bs=" + query
					+ "&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * PAGESIZE;
			LOG.debug(query);
			List<Proof> evidences = searchBaidu(query, referer);
			referer = query;
			if (evidences != null && evidences.size() > 0) {
				question.addEvidences(evidences);
			} else {
				LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
				break;
			}
		}
		LOG.info("Question：" + question.getIssue() + " 搜索到Evidence " + question.getEvidences().size() + " 条");
		if (question.getEvidences().isEmpty()) {
			return null;
		}
		// 3、将baidu查询结果加入本地缓存
		if (question.getEvidences().size() > 7) {
			LOG.info("将Question：" + question.getIssue() + " 加入MySQL数据库");
//			MySQLUtils.saveQuestionToDatabase("baidu:", question);
		}

		// 回答问题
		if (questionAnsweringSystem != null) {
			questionAnsweringSystem.answerQuestion(question);
		}
		return question;
	}

	private List<Proof> searchBaidu(String url, String referer) {
		List<Proof> proofs = new ArrayList<>();
		try {
			Document document = Jsoup.connect(url).header("Accept", Config.ACCEPT).header("Accept-Encoding", Config.ENCODING)
					.header("Accept-Language", Config.LANGUAGE).header("Connection", Config.CONNECTION).header("User-Agent", Config.USER_AGENT)
					.header("Host", Config.HOST).header("Referer", referer).get();
//			LOG.info(document.toString());
			String resultCssQuery = "html > body > div > div > div > div > div.result";
			Elements elements = document.select(resultCssQuery);
			
			for (Element element : elements) {
				LOG.info("log: "+element.toString());
				Elements subElements = element.select("h3 > a");
			
				if (subElements.size() != 1) {
					continue;
				}
				String title = subElements.get(0).text();
				if (title == null || "".equals(title.trim())) {
					continue;
				}
				subElements = element.select("div.c-abstract");
				if (subElements.size() != 1) {
					continue;
				}
				String snippet = subElements.get(0).text();
				if (snippet == null || "".equals(snippet.trim())) {
					continue;
				}
				Proof proof = new Proof();
				proof.setTitle(title);
				proof.setSnippet(snippet);

				proofs.add(proof);
			}
		} catch (Exception ex) {
			LOG.error("encountered error while search: ", ex);
		}
		return proofs;
	}

	public static void main(String args[]) {
		Issue question = new BaiduDataSource(FilesConfig.personNameQuestions).getIssue("北京大学校长是谁？");
		LOG.info(question.toString());
	}
}