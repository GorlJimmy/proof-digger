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

package org.linuxkernel.proof.digger.model;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.linuxkernel.proof.digger.parser.WordSegment;


public class Proof {

    private String title;
    private String snippet;
    private double score = 1.0;
    private SolutionCollection solutionCollection;

    public List<String> getTitleTerms() {
        List<String> result = new ArrayList<>();
        List<Term> terms = WordSegment.parse(title);
        for (Term term : terms) {
            result.add(term.getName());
        }
        return result;
    }

    public List<String> getSnippetTerms() {
        List<String> result = new ArrayList<>();
        List<Term> terms = WordSegment.parse(snippet);
        for (Term term : terms) {
            result.add(term.getName());
        }
        return result;
    }

    
    public List<String> getTerms() {
        List<String> result = new ArrayList<>();
        List<Term> terms = WordSegment.parse(title + snippet);
        for (Term term : terms) {
            result.add(term.getName());
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

	public double getScore() {
        return score;
    }

    public void addScore(double score) {
        this.score += score;
    }

    public SolutionCollection getSolutionCollection() {
		return solutionCollection;
	}

	public void setSolutionCollection(SolutionCollection solutionCollection) {
		this.solutionCollection = solutionCollection;
	}

	public SolutionCollection getCandidateAnswerCollection() {
        return solutionCollection;
    }

    public void setCandidateAnswerCollection(SolutionCollection candidateAnswerCollection) {
        this.solutionCollection = candidateAnswerCollection;
    }
}