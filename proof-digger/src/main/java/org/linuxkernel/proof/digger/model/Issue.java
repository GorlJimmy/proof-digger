package org.linuxkernel.proof.digger.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.linuxkernel.proof.digger.filter.CandidateAnswerCanNotInQustionFilter;
import org.linuxkernel.proof.digger.filter.CandidateAnswerFilter;
import org.linuxkernel.proof.digger.parser.WordParser;
import org.linuxkernel.proof.digger.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Issue {

    private static final Logger LOG = LoggerFactory.getLogger(Issue.class);
    private String question;
    private final List<Proof> evidences = new ArrayList<>();

    private Type questionType = Type.PERSON_NAME;
    private String expectAnswer;
    private CandidateAnswerFilter candidateAnswerFilter = new CandidateAnswerCanNotInQustionFilter();

    //候选的问题类型，对问题进行分类的时候，可能会有多个类型
    private final Set<Type> candidateQuestionTypes = new HashSet<>();

    public void clearCandidateQuestionType() {
        candidateQuestionTypes.clear();
    }

    public void addCandidateQuestionType(Type questionType) {
        candidateQuestionTypes.add(questionType);
    }

    public void removeCandidateQuestionType(Type questionType) {
        candidateQuestionTypes.remove(questionType);
    }

    public Set<Type> getCandidateQuestionTypes() {
        return candidateQuestionTypes;
    }

    public Map.Entry<String, Integer> getHot() {
        List<String> questionTerms = getTerms();
        Map<String, Integer> map = new HashMap<>();
        List<Term> terms = WordParser.parse(getText());
        for (Term term : terms) {
            Integer count = map.get(term.getName());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(term.getName(), count);
        }
        Map<String, Integer> questionMap = new HashMap<>();
        for (String questionTerm : questionTerms) {
            Integer count = map.get(questionTerm);
            if (questionTerm.length() > 1 && count != null) {
                questionMap.put(questionTerm, count);
                LOG.debug("问题热词统计: " + questionTerm + " " + map.get(questionTerm));
            }
        }
        List<Map.Entry<String, Integer>> list = Tools.sortByIntegerValue(questionMap);
        Collections.reverse(list);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public int getExpectAnswerRank() {
        if (expectAnswer == null) {
            LOG.info("未指定期望的答案");
            return -2;
        }
        List<Solution> candidateAnswers = this.getAllCandidateAnswer();
        int len = candidateAnswers.size();
        for (int i = 0; i < len; i++) {
            Solution candidateAnswer = candidateAnswers.get(i);
            if (expectAnswer.trim().equals(candidateAnswer.getAnswer().trim())) {
                return (i + 1);
            }
        }
        return -1;
    }

    /**
     * 对问题进行分词
     *
     * @return 分词结果
     */
    public List<String> getTerms() {
        List<String> result = new ArrayList<>();
        List<Term> terms = WordParser.parse(question.replace("?", "").replace("？", ""));
        for (Term term : terms) {
            result.add(term.getName());
        }
        return result;
    }

    /**
     * 获取所有候选答案
     *
     * @return 所有候选答案
     */
    public List<Solution> getAllCandidateAnswer() {
        Map<String, Double> map = new HashMap<>();
        for (Proof evidence : evidences) {
            for (Solution candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                Double score = map.get(candidateAnswer.getAnswer());
                //候选答案的分值和证据的分值 用于计算最终的候选答案分值
                Double candidateAnswerFinalScore = candidateAnswer.getScore() + evidence.getScore();
                if (score == null) {
                    score = candidateAnswerFinalScore;
                } else {
                    score += candidateAnswerFinalScore;
                }
                map.put(candidateAnswer.getAnswer(), score);
            }
        }

        //组装候选答案
        List<Solution> candidateAnswers = new ArrayList<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String answer = entry.getKey();
            Double score = entry.getValue();
            if (answer != null && score != null && score > 0 && score < Double.MAX_VALUE) {
                Solution candidateAnswer = new Solution();
                candidateAnswer.setAnswer(answer);
                candidateAnswer.setScore(score);
                candidateAnswers.add(candidateAnswer);
            }
        }
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        //过滤候选答案
        if (candidateAnswerFilter != null) {
            candidateAnswerFilter.filter(this, candidateAnswers);
        }
        //分值归一化
        if (candidateAnswers.size() > 0) {
            double baseScore = candidateAnswers.get(0).getScore();
            for (Solution candidateAnswer : candidateAnswers) {
                double score = candidateAnswer.getScore() / baseScore;
                candidateAnswer.setScore(score);
            }
        }

        return candidateAnswers;
    }

    /**
     * 获取topN候选答案
     *
     * @param topN
     * @return topN候选答案
     */
    public List<Solution> getTopNCandidateAnswer(int topN) {
        List<Solution> topNcandidateAnswers = new ArrayList<>();
        List<Solution> allCandidateAnswers = getAllCandidateAnswer();
        if (topN > allCandidateAnswers.size()) {
            topN = allCandidateAnswers.size();
        }
        for (int i = 0; i < topN; i++) {
            topNcandidateAnswers.add(allCandidateAnswers.get(i));
        }
        return topNcandidateAnswers;
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Proof evidence : evidences) {
            text.append(evidence.getTitle()).append(evidence.getSnippet());
        }
        return text.toString();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Proof> getEvidences() {
        return this.evidences;
    }

    public void addEvidences(List<Proof> evidences) {
        this.evidences.addAll(evidences);
    }

    public void addEvidence(Proof evidence) {
        this.evidences.add(evidence);
    }

    public void removeEvidence(Proof evidence) {
        this.evidences.remove(evidence);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("?. ").append(question).append("\n\n");
        for (Proof evidence : this.evidences) {
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("Snippet: ").append(evidence.getSnippet()).append("\n\n");
        }

        return result.toString();
    }

    public String toString(int index) {
        StringBuilder result = new StringBuilder();
        result.append("?").append(index).append(". ").append(question).append("\n\n");
        for (Proof evidence : this.evidences) {
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("Snippet: ").append(evidence.getSnippet()).append("\n\n");
        }

        return result.toString();
    }

    public String getExpectAnswer() {
        return expectAnswer;
    }

    public void setExpectAnswer(String expectAnswer) {
        this.expectAnswer = expectAnswer;
    }

    public void setQuestionType(Type questionType) {
        this.questionType = questionType;
    }

    public Type getQuestionType() {
        return questionType;
    }

    public CandidateAnswerFilter getCandidateAnswerFilter() {
        return candidateAnswerFilter;
    }

    public void setCandidateAnswerFilter(CandidateAnswerFilter candidateAnswerFilter) {
        this.candidateAnswerFilter = candidateAnswerFilter;
    }
}