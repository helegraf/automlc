package meka.classifiers.multilabel.meta.evaluation;

/**
 * Event that communicates the start of a new generation.
 * 
 * @author Helena Graf
 *
 */
public class StartingGenerationEvent {

	/**
	 * the generation that is started
	 */
	private int generation;

	/**
	 * Create a new starting generation event that communicates the start of a new
	 * generation.
	 * 
	 * @param generation
	 *            the index of the generation being started
	 */
	public StartingGenerationEvent(int generation) {
		this.generation = generation;
	}

	/**
	 * Get the current generation
	 * 
	 * @return the newly started generation
	 */
	public int getGeneration() {
		return generation;
	}
}
