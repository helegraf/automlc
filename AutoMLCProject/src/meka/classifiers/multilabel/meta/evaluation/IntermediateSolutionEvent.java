package meka.classifiers.multilabel.meta.evaluation;

/**
 * Class that holds evaluation data for intermediate solutions.
 * 
 * @author Helena Graf
 *
 */
public class IntermediateSolutionEvent {

	// these variables hold the metric values or null if they could not be computed
	// for the solutione valuation this event represents.
	private String solution;
	private Double exactMatch;
	private Double hammingLoss;
	private Double rankLoss;
	private Double f1MacroAvgL;
	private Double fitness;

	/**
	 * Constructs a new intermediate solution event representing an evaluation of
	 * the given solution.
	 * 
	 * @param solution
	 *            the solution for which this solution event contains the measures.
	 */
	public IntermediateSolutionEvent(String solution) {
		this.solution = solution;
	}

	/**
	 * Get the value of the exact match metric for the solution evaluation this
	 * event represents or null if it could not be computed.
	 * 
	 * @return the exact match metric value
	 */
	public Double getExactMatch() {
		return exactMatch;
	}

	/**
	 * Set the value of the exact match metric for the solution evaluation this
	 * event represents.
	 * 
	 * @param exactMatch
	 *            the exact match metric value
	 */
	public void setExactMatch(Double exactMatch) {
		this.exactMatch = exactMatch;
	}

	/**
	 * Get the value of the hamming loss metric for the solution evaluation this
	 * event represents or null if it could not be computed.
	 * 
	 * @return the hamming loss metric value
	 */
	public Double getHammingLoss() {
		return hammingLoss;
	}

	/**
	 * Set the value of the hamming loss metric for the solution evaluation this
	 * event represents.
	 * 
	 * @param hammingLoss
	 *            the hamming loss metric value
	 */
	public void setHammingLoss(Double hammingLoss) {
		this.hammingLoss = hammingLoss;
	}

	/**
	 * Get the value of the rank loss metric for the solution evaluation this event
	 * represents or null if it could not be computed.
	 * 
	 * @return the rank loss metric value
	 */
	public Double getRankLoss() {
		return rankLoss;
	}

	/**
	 * Set the value of the rank loss metric for the solution evaluation this event
	 * represents.
	 * 
	 * @param rankLoss
	 *            the rank loss metric value
	 */
	public void setRankLoss(Double rankLoss) {
		this.rankLoss = rankLoss;
	}

	/**
	 * Get the value of the F1 macro averaged by label metric for the solution
	 * evaluation this event represents or null if it could not be computed.
	 * 
	 * @return the F1 macro averaged by label metric value
	 */
	public Double getF1MacroAvgL() {
		return f1MacroAvgL;
	}

	/**
	 * Set the value of the F1 macro averaged by label metric for the solution
	 * evaluation this event represents
	 * 
	 * @param f1MacroAvgL
	 *            the F1 macro averaged by label metric value
	 */
	public void setF1MacroAvgL(Double f1MacroAvgL) {
		this.f1MacroAvgL = f1MacroAvgL;
	}

	/**
	 * Get the value of the fitness metric for the solution evaluation this event
	 * represents or null if it could not be computed.
	 * 
	 * @return the fitness metric value
	 */
	public Double getFitness() {
		return fitness;
	}

	/**
	 * Set the value of the fitness metric for the solution evaluation this event
	 * represents.
	 * 
	 * @param fitness
	 *            the fitness metric value
	 */
	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * Get the solution for which the measures this solution event contains were made.
	 * 
	 * @return the solution as a command-line String that can be executed
	 */
	public String getSolution() {
		return solution;
	}

	@Override
	public String toString() {
		return "solution: " + solution + "\nexactMatch: " + exactMatch + ", hammingLoss: " + hammingLoss + ", rankLoss: " + rankLoss
				+ ", f1MarcoAvgL: " + f1MacroAvgL + ", fitness: " + fitness;
	}

}
