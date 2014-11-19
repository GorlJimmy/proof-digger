
package org.linuxkernel.proof.digger.datasource;

import java.util.List;

import org.linuxkernel.proof.digger.model.Issue;
import org.linuxkernel.proof.digger.system.IssueSolutionSystem;

public interface DataSource {

    
    public List<Issue> getIssues();

    
    public Issue getIssue(String str_issue);

    
    public List<Issue> getAndAnswerQuestions(IssueSolutionSystem questionAnsweringSystem);

    public Issue getAndAnswerQuestion(String questionStr, IssueSolutionSystem questionAnsweringSystem);

}