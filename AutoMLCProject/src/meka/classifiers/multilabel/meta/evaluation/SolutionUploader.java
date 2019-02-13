package meka.classifiers.multilabel.meta.evaluation;

import java.sql.SQLException;
import java.util.HashMap;

import com.google.common.eventbus.Subscribe;

import automlc.experiments.ResultsDBConnection;

/**
 * Uploads received solution events.
 * 
 * @author Helena Graf
 *
 */
public class SolutionUploader {

	/**
	 * Connection which to use to upload intermediate solution events
	 */
	private ResultsDBConnection connection;

	/**
	 * The time at which the experiment was started
	 */
	private long startTime;

	/**
	 * The current generation of the genetic algorithm
	 */
	private int currentGeneration = -1;

	/**
	 * Construct a new Solution upload that uploads received
	 * {@link IntermediateSolutionEvent}s using the given connection.
	 * 
	 * @param connection
	 *            the connection to use to upload events
	 * @param startTime
	 *            the time at which the experiment was started
	 */
	public SolutionUploader(ResultsDBConnection connection, long startTime) {
		this.connection = connection;
		this.startTime = startTime;
	}

	@Subscribe
	public void receiveIntermediateSolutionEvent(IntermediateSolutionEvent e) {
		HashMap<String, Double> metricsWithValues = new HashMap<>();
		// currently only want to track 1 metric (the optimized one)
		// metricsWithValues.put("P_ExactMatch", e.getExactMatch());
		// metricsWithValues.put("L_Hamming", e.getHammingLoss());
		// metricsWithValues.put("L_RankLoss", e.getRankLoss());
		// metricsWithValues.put("P_FmacroAvgL", e.getF1MacroAvgL());
		metricsWithValues.put("P_Fitness", e.getFitness());

		try {
			connection.addIntermediateMeasurement(e.getSolution(), e.getFitness(),
					System.currentTimeMillis() - startTime, currentGeneration);
		} catch (SQLException e1) {
			System.err.println("Could not upload intermediate solution:\n" + e + "\ndue to exception\n" + e1);
		}
	}

	@Subscribe
	public void receiveStartingGenerationEvent(StartingGenerationEvent e) {
		currentGeneration = e.getGeneration();
	}
}
