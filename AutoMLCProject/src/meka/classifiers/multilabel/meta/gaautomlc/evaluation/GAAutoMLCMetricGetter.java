package meka.classifiers.multilabel.meta.gaautomlc.evaluation;

import java.util.Arrays;
import java.util.List;

import meka.classifiers.multilabel.meta.gaautomlc.core.ResultsEval;

/**
 * Getter class for easier evaluation and comparison with MEKA evaluations.
 * 
 * @author Helena Graf
 *
 */
public class GAAutoMLCMetricGetter {

	private GAAutoMLCMetricGetter() {
	}

	/**
	 * Available metrics for multilabelclassifiers
	 */
	public static final List<String> multiLabelMetrics = Arrays.asList("L_Hamming", "L_LevenshteinDistance",
			"L_OneError", "L_JaccardDist", "L_RankLoss", "L_ZeroOne", "P_Accuracy", "P_AveragePrecision",
			"P_ExactMatch", "P_FmacroAvgD", "P_FmacroAvgL", "P_FmicroAvg", "P_Hamming", "P_Harmonic", "P_RecallMicro", "P_RecallMacro",
			"P_PrecisionMicro", "P_PrecisionMacro", "P_Fitness");

	public static final List<String> unavailableMetrics = Arrays.asList("L_LogLoss", "L_LogLossD", "L_LogLossL",
			"P_macroAUPRC", "P_marcoAUROC");

	public static final List<String> additionalMetrics = Arrays.asList("LabelCardinalityDifference",
			"LabelCardinalityPredicted", "EmptyLabelvectorsPredicted", "aurcMacroAveraged",
			"aurocMacroAveraged_Training");

	/**
	 * Get the value of the given metric from the results eval by name.
	 * 
	 * @param metricName
	 *            the name of the matric for which to retrieve the value
	 * @param results
	 *            the results from which to extract the value
	 * @return the value
	 */
	public static double getValueOfMetricForAutoMekaGGP(String metricName, ResultsEval results) {

		switch (metricName) {
		case "L_Hamming":
			return results.getHammingLoss_Evaluation();
		case "L_LevenshteinDistance":
			return results.getLevenshteinDistance_Evaluation();
		case "L_OneError":
			return results.getOneError_Evaluation();
		case "L_JaccardDist":
			return results.getJaccardDistance_Evaluation();
		case "L_RankLoss":
			return results.getRankLoss_Evaluation();
		case "L_ZeroOne":
			return results.getZeroOneLoss_Evaluation();
		case "P_Accuracy":
			return results.getAccuracy_Evaluation();
		case "P_AveragePrecision":
			return results.getAvgPrecision_Evaluation();
		case "P_ExactMatch":
			return results.getExactMatch_Evaluation();
		case "P_FmicroAvg":
			return results.getF1MicroAveraged_Evaluation();
		case "P_Hamming":
			return results.getHammingScore_Evaluation();
		case "P_Harmonic":
			return results.getHarmonicScore_Evaluation();
		case "P_PrecisionMacro":
			return results.getMacroPrecision_Evaluation();
		case "P_PrecisionMicro":
			return results.getMicroPrecision_Evaluation();
		case "P_RecallMacro":
			return results.getMacroRecall_Evaluation();
		case "P_RecallMicro":
			return results.getMicroRecall_Evaluation();
		case "P_FmacroAvgD":
			return results.getF1MacroAveragedExample_Evaluation();
		case "P_FmacroAvgL":
			return results.getF1MacroAveragedLabel_Evaluation();
		case "P_JaccardIndex":
			return results.getAccuracy_Evaluation();
		case "P_Fitness":
			return (results.getExactMatch_Evaluation() + (1 - results.getHammingLoss_Evaluation())
					+ results.getF1MacroAveragedLabel_Evaluation() + (1 - results.getRankLoss_Evaluation())) / 4.0;
		default:
			throw new IllegalArgumentException(metricName + " not a supported metric!");
		}
	}
}
