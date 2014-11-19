
package org.linuxkernel.proof.digger.datasource;

import java.util.List;

import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.QuestionAnsweringSystem;

public interface DataSource {

    
    public List<Issue> getIssues();

    
    public Issue getIssue(String str_issue);

    
    public List<Issue> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem);

    public Issue getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem);

}