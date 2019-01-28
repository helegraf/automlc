package meka.classifiers.multilabel.meta.automekaggp.evaluation;

import java.io.IOException;

import org.epochx.gr.representation.GRCandidateProgram;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

import meka.classifiers.multilabel.meta.AutoMEKA_GGP;
import meka.classifiers.multilabel.meta.automekaggp.core.AbstractTranslateIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.MetaIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.ResultsEval;
import meka.classifiers.multilabel.meta.automekaggp.core.SimplifiedGATranslateIndividual;
import meka.classifiers.multilabel.meta.automekaggp.core.TranslateIndividual;
import weka.classifiers.AbstractClassifier;

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
		System.out.println(grammarName);
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
		System.out.println(algorithmForTest);
		String [] commands = algorithmForTest.split(" ");
		int index = 0;
		for (int i = 0; i < commands.length; i++) {
			if (commands [i].startsWith("meka.") && !commands[i].startsWith("meka.jar")) {
				index = i;
				break;
			}
		}
		
//		String classifierString = commands[index];
//		String[] options = (String[]) Arrays.copyOfRange(commands, index+1, commands.length);
//		AbstractClassifier.forName(classifierString, options);
		
		
		return MetaIndividual.EvaluateAlgorithmOnTest(algorithmForTest);
	}
}
