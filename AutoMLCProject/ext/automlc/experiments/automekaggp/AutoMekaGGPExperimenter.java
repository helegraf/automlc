package automlc.experiments.automekaggp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;
import org.epochx.gr.representation.GRCandidateProgram;

import automlc.experiments.ResultsDBConnection;
import jaicore.basic.SQLAdapter;
import jaicore.experiments.ExperimentDBEntry;
import jaicore.experiments.ExperimentRunner;
import jaicore.experiments.IExperimentIntermediateResultProcessor;
import jaicore.experiments.IExperimentSetConfig;
import jaicore.experiments.IExperimentSetEvaluator;
import jaicore.ml.evaluation.multilabel.MultilabelDatasetSplitter;
import meka.classifiers.multilabel.meta.AutoMEKA_GGP;
import meka.classifiers.multilabel.meta.automekaggp.core.ResultsEval;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPEvaluator;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPMetricGetter;
import meka.classifiers.multilabel.meta.evaluation.SolutionUploader;
import meka.classifiers.multilabel.meta.gaautomlc.evaluation.GAAutoMLCMetricGetter;
import meka.core.MLUtils;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;

/**
 * Experimenter for AutoMekaGGP
 * 
 * @author Helena Graf
 *
 */
public class AutoMekaGGPExperimenter implements IExperimentSetEvaluator {
	private static final AutoMekaGGPExperimenterConfig CONFIG = ConfigCache
			.getOrCreate(AutoMekaGGPExperimenterConfig.class);

	@Override
	public IExperimentSetConfig getConfig() {
		return CONFIG;
	}

	@Override
	public void evaluate(final ExperimentDBEntry experimentEntry, final SQLAdapter adapter,
			final IExperimentIntermediateResultProcessor processor) throws Exception {
		System.out.println("Experiment ID: " + experimentEntry.getId());
		System.out.println("Experiment Description: " + experimentEntry.getExperiment().getValuesOfKeyFields());

		Map<String, String> experimentDescription = experimentEntry.getExperiment().getValuesOfKeyFields();

		// Load dataset
		StringBuilder datasetPathSB = new StringBuilder();
		datasetPathSB.append(CONFIG.getDatasetFolder().getAbsolutePath());
		datasetPathSB.append(File.separator);
		datasetPathSB.append(experimentDescription.get("dataset"));
		datasetPathSB.append(".arff");
		// Instances data = new Instances(new FileReader(datasetPathSB.toString()));
		Instances data = readFile(datasetPathSB.toString());
		// Instances data =
		// readFile("datasets/classification/multi-label/emotion.arff");

		// Prepare the dataset to be ready for multi-label classification
		// MLUtils.prepareData(data);

		// Get train / test splits
		String splitDescription_traintest = experimentDescription.get("test_split_tech");
		String testFold = experimentDescription.get("test_fold");
		String testSeed = experimentDescription.get("seed");
		Instances train = MultilabelDatasetSplitter.getTrainSplit(data, splitDescription_traintest, testFold, testSeed);
		Instances test = MultilabelDatasetSplitter.getTestSplit(data, splitDescription_traintest, testFold, testSeed);

		// Prepare connection
		ResultsDBConnection connection = new ResultsDBConnection("intermediate_measurements", "final_measurements",
				"ordered_metric", experimentEntry.getId(), "AutoMekaGGP", adapter);

		System.out.println("Now test...");
		File trainingTempFile = File.createTempFile("training", ".arff");
		trainingTempFile.deleteOnExit();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(train);
		saver.setFile(trainingTempFile);
		saver.writeBatch();

		File testingTempFile = File.createTempFile("testing", ".arff");
		trainingTempFile.deleteOnExit();
		saver = new ArffSaver();
		saver.setInstances(test);
		saver.setFile(testingTempFile);
		saver.writeBatch();

		AutoMEKA_GGP autoMekaGGP = new AutoMEKA_GGP(
				new String[] { "-t", trainingTempFile.getAbsolutePath(), "-T", testingTempFile.getAbsolutePath() });
		autoMekaGGP.setAnytime(true);
		autoMekaGGP.registerListener(new SolutionUploader(connection, System.currentTimeMillis()));
		autoMekaGGP.setGeneraltimeLimit(Integer.parseInt(experimentDescription.get("timeout")));
		autoMekaGGP.setAlgorithmTimeLimit(Integer.parseInt(experimentDescription.get("node_timeout")) * 60);
		autoMekaGGP.setExperimentName("auto");
		autoMekaGGP.setSaveResults(false);
		this.evaluateMLClassifier(train, connection, autoMekaGGP);

		System.out.println("Done with evaluation. Send job result.");

		Map<String, Object> results = new HashMap<>();
		results.put("completed", true);
		results.put("classifier_string", ((GRCandidateProgram) autoMekaGGP.getBestAlgorithm()).toString());
		results.put("value", ((GRCandidateProgram) autoMekaGGP.getBestAlgorithm()).getFitnessValue());
		processor.processResults(results);

		System.out.println("Evaluation task completed.");
	}

	private void evaluateMLClassifier(final Instances train, final ResultsDBConnection connection,
			AutoMEKA_GGP classifier) throws Exception {
		System.out.println("Evaluate Classifier...");
		classifier.buildClassifier(train);
		ResultsEval results = AutoMekaGGPEvaluator.evaluateClassifier(classifier);
		System.out.println("Done evaluating Classifier.");

		System.out.println("Store results in DB...");
		HashMap<String, Double> metrics = new HashMap<>();
		AutoMekaGGPMetricGetter.multiLabelMetrics.forEach(metric -> {
			metrics.put(metric, AutoMekaGGPMetricGetter.getValueOfMetricForAutoMekaGGP(metric, results));

		});
		connection.addFinalMeasurements(metrics);
		System.out.println("Stored results in DB.");
	}

	public static void main(final String[] args) {
		ExperimentRunner runner = new ExperimentRunner(new AutoMekaGGPExperimenter());
		runner.randomlyConductExperiments(1, false);
	}

	public static Instances readFile(String name) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(name));
		ArffReader arff = new ArffReader(reader);
		Instances data = arff.getData();
		String[] options = MLUtils.getDatasetOptions(data);
		System.out.println(Arrays.asList(options));
		for (int i = 0; i < options.length - 1; i++) {
			if (options[i].equals("-c") || options[i].equals("-C")) {
				System.out.println("Set class index: " + Math.abs(Integer.parseInt(options[i + 1])));
				data.setClassIndex(Math.abs(Integer.parseInt(options[i + 1])));
				break;
			}
		}
		return data;
	}
}
