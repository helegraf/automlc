package meka.classifiers.multilabel.meta.automekaggp.evaluation;

import org.epochx.gr.representation.GRCandidateProgram;

import meka.classifiers.multilabel.meta.AutoMEKA_GGP;
import meka.classifiers.multilabel.meta.automekaggp.core.AbstractTranslateIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.MetaIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.ResultsEval;
import meka.classifiers.multilabel.meta.automekaggp.core.SimplifiedGATranslateIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.TranslateIndividual;

/**
 * Class for evaluating AutoMekaGGP after it has finished building.
 * 
 * @author Helena Graf
 *
 */
public class AutoMekaGGPEvaluator {

	private AutoMekaGGPEvaluator() {
	}

	/**
	 * Get evaluation results for the classifier. Train and test are set in the
	 * classifier. It is assumed that the classifier has been trained before.
	 * 
	 * @param classifier
	 *            the classifier for which to get evaluation results
	 * @return the evaluation results
	 * @throws Exception 
	 */
	public static ResultsEval evaluateClassifier(AutoMEKA_GGP classifier) throws Exception {
		String grammarName = ((GRCandidateProgram) classifier.getBestAlgorithm()).toString();
		AbstractTranslateIndividual translInd = null;
		String grammarMode = classifier.getGrammarMode();
		long seed = classifier.getSeed();
		if (grammarMode.equals("Full") || grammarMode.equals("SimpBO")) {
			translInd = new TranslateIndividual(grammarName, seed);
		} else if (grammarMode.equals("SimpGA")) {
			translInd = new SimplifiedGATranslateIndividual(grammarName, seed);
		} else {
			translInd = new TranslateIndividual(grammarName, seed);
		}

		String algorithmForTest = translInd.translate2Command(classifier.getTrainingDirectory(),
				classifier.getTestingDirectory(), Integer.MAX_VALUE, true);
		
		return MetaIndividual.EvaluateAlgorithmOnTest(algorithmForTest);
	}
}
