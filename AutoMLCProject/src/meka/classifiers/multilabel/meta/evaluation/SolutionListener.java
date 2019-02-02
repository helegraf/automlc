package meka.classifiers.multilabel.meta.evaluation;

import com.google.common.eventbus.Subscribe;

/**
 * Fot intermediate solution evaluation.
 * 
 * @author Helena Graf
 *
 */
public class SolutionListener {

	/**
	 * Prints the contents of a received intermediate solution event.
	 * 
	 * @param e
	 *            the received event
	 */
	@Subscribe
	public void receiveIntermediateSolutionEvent(IntermediateSolutionEvent e) {
		System.out.println(e);
	}
}
