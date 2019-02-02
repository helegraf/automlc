package meka.classifiers.multilabel.meta.gaautomlc.evaluation;

import java.io.IOException;

import meka.classifiers.multilabel.meta.GAAutoMLC;
import meka.classifiers.multilabel.meta.gaautomlc.core.MetaIndividual;
import meka.classifiers.multilabel.meta.gaautomlc.core.ResultsEval;

/**
 * Class for evaluating GAAutoMLC after it has finished building.
 * 
 * @author Helena Graf
 *
 */
public class GAAutoMLCEvaluator {

	private GAAutoMLCEvaluator() {
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
	public static ResultsEval evaluateClassifier(GAAutoMLC classifier) throws IOException, Exception {
		MetaIndividual algorithm = classifier.getBestALgorithm();
		String[] classifierName = algorithm.getM_individualNameVector();
		String fullTrainingSet = classifier.getTrainingDirectory();
		String testSet = classifier.getTestingDirectory();
		return algorithm.EvaluateAlgorithmOnTest(classifierName, fullTrainingSet, testSet);
	}
}
