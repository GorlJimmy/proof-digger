
package org.linuxkernel.proof.digger.system;

public class ScoreWeight {

    private double termFrequencyCandidateAnswerScoreWeight = 1;
    private double termDistanceCandidateAnswerScoreWeight = 1;
    private double termDistanceMiniCandidateAnswerScoreWeight = 1;
    private double textualAlignmentCandidateAnswerScoreWeight = 1;
    private double moreTextualAlignmentCandidateAnswerScoreWeight = 1;
    private double rewindTextualAlignmentCandidateAnswerScoreWeight = 1;
    private double hotCandidateAnswerScoreWeight = 1;

    private double termMatchEvidenceScoreWeight = 1;
    private double bigramEvidenceScoreWeight = 1;
    private double skipBigramEvidenceScoreWeight = 1;

    public double getTermFrequencyCandidateAnswerScoreWeight() {
        return termFrequencyCandidateAnswerScoreWeight;
    }

    public void setTermFrequencyCandidateAnswerScoreWeight(
            double termFrequencyCandidateAnswerScoreWeight) {
        this.termFrequencyCandidateAnswerScoreWeight = termFrequencyCandidateAnswerScoreWeight;
    }

    public double getTermDistanceCandidateAnswerScoreWeight() {
        return termDistanceCandidateAnswerScoreWeight;
    }

    public void setTermDistanceCandidateAnswerScoreWeight(
            double termDistanceCandidateAnswerScoreWeight) {
        this.termDistanceCandidateAnswerScoreWeight = termDistanceCandidateAnswerScoreWeight;
    }

    public double getTermDistanceMiniCandidateAnswerScoreWeight() {
        return termDistanceMiniCandidateAnswerScoreWeight;
    }

    public void setTermDistanceMiniCandidateAnswerScoreWeight(
            double termDistanceMiniCandidateAnswerScoreWeight) {
        this.termDistanceMiniCandidateAnswerScoreWeight = termDistanceMiniCandidateAnswerScoreWeight;
    }

    public double getTextualAlignmentCandidateAnswerScoreWeight() {
        return textualAlignmentCandidateAnswerScoreWeight;
    }

    public void setTextualAlignmentCandidateAnswerScoreWeight(
            double textualAlignmentCandidateAnswerScoreWeight) {
        this.textualAlignmentCandidateAnswerScoreWeight = textualAlignmentCandidateAnswerScoreWeight;
    }

    public double getMoreTextualAlignmentCandidateAnswerScoreWeight() {
        return moreTextualAlignmentCandidateAnswerScoreWeight;
    }

    public void setMoreTextualAlignmentCandidateAnswerScoreWeight(
            double moreTextualAlignmentCandidateAnswerScoreWeight) {
        this.moreTextualAlignmentCandidateAnswerScoreWeight = moreTextualAlignmentCandidateAnswerScoreWeight;
    }

    public double getRewindTextualAlignmentCandidateAnswerScoreWeight() {
        return rewindTextualAlignmentCandidateAnswerScoreWeight;
    }

    public void setRewindTextualAlignmentCandidateAnswerScoreWeight(
            double rewindTextualAlignmentCandidateAnswerScoreWeight) {
        this.rewindTextualAlignmentCandidateAnswerScoreWeight = rewindTextualAlignmentCandidateAnswerScoreWeight;
    }

    public double getHotCandidateAnswerScoreWeight() {
        return hotCandidateAnswerScoreWeight;
    }

    public void setHotCandidateAnswerScoreWeight(
            double hotCandidateAnswerScoreWeight) {
        this.hotCandidateAnswerScoreWeight = hotCandidateAnswerScoreWeight;
    }

    public double getTermMatchEvidenceScoreWeight() {
        return termMatchEvidenceScoreWeight;
    }

    public void setTermMatchEvidenceScoreWeight(double termMatchEvidenceScoreWeight) {
        this.termMatchEvidenceScoreWeight = termMatchEvidenceScoreWeight;
    }

    public double getBigramEvidenceScoreWeight() {
        return bigramEvidenceScoreWeight;
    }

    public void setBigramEvidenceScoreWeight(double bigramEvidenceScoreWeight) {
        this.bigramEvidenceScoreWeight = bigramEvidenceScoreWeight;
    }

    public double getSkipBigramEvidenceScoreWeight() {
        return skipBigramEvidenceScoreWeight;
    }

    public void setSkipBigramEvidenceScoreWeight(double skipBigramEvidenceScoreWeight) {
        this.skipBigramEvidenceScoreWeight = skipBigramEvidenceScoreWeight;
    }

    
}