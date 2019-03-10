package automlc.experiments.gaautomlc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;

import automlc.experiments.ResultsDBConnection;
import jaicore.basic.SQLAdapter;
import jaicore.experiments.ExperimentDBEntry;
import jaicore.experiments.ExperimentRunner;
import jaicore.experiments.IExperimentIntermediateResultProcessor;
import jaicore.experiments.IExperimentSetConfig;
import jaicore.experiments.IExperimentSetEvaluator;
import jaicore.ml.evaluation.multilabel.MultilabelDatasetSplitter;
import meka.classifiers.multilabel.meta.GAAutoMLC;
import meka.classifiers.multilabel.meta.evaluation.SolutionUploader;
import meka.classifiers.multilabel.meta.gaautomlc.core.ResultsEval;
import meka.classifiers.multilabel.meta.gaautomlc.evaluation.GAAutoMLCEvaluator;
import meka.classifiers.multilabel.meta.gaautomlc.evaluation.GAAutoMLCMetricGetter;
import meka.core.MLUtils;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ArffLoader.ArffReader;

/**
 * Experimenter for GAAutoMLC
 * 
 * @author Helena Graf
 *
 */
public class GAAutoMLCExperimenter implements IExperimentSetEvaluator {
	private static final GAAutoMLCExperimenterConfig CONFIG = ConfigCache
			.getOrCreate(GAAutoMLCExperimenterConfig.class);

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
		//Instances data = new Instances(new FileReader(datasetPathSB.toString()));
		Instances data = readFile (datasetPathSB.toString());
		//Instances data = readFile("datasets/classification/multi-label/emotion.arff");

		// Prepare the dataset to be ready for multi-label classification
		//MLUtils.prepareData(data);
		
		StringBuilder xmlPathSB = new StringBuilder();
		xmlPathSB.append(CONFIG.getDatasetFolder().getAbsolutePath());
		xmlPathSB.append(File.separator);
		xmlPathSB.append(experimentDescription.get("dataset"));
		xmlPathSB.append(".xml");

		// Get train / test splits
		String splitDescription_traintest = experimentDescription.get("test_split_tech");
		String testFold = experimentDescription.get("test_fold");
		String testSeed = experimentDescription.get("seed");
		Instances train = MultilabelDatasetSplitter.getTrainSplit(data, splitDescription_traintest, testFold, testSeed);
		Instances test = MultilabelDatasetSplitter.getTestSplit(data, splitDescription_traintest, testFold, testSeed);

		// Prepare connection
		ResultsDBConnection connection = new ResultsDBConnection("intermediate_measurements", "final_measurements",
				"ordered_metric", experimentEntry.getId(), "GAAutoMLC", adapter);

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

		GAAutoMLC gaAutoMLC = new GAAutoMLC(
				new String[] { "-t", trainingTempFile.getAbsolutePath(), "-T", testingTempFile.getAbsolutePath() }, xmlPathSB.toString());
		gaAutoMLC.setAnytime(true);
		gaAutoMLC.setGeneraltimeLimit(Integer.parseInt(experimentDescription.get("timeout")));
		gaAutoMLC.setTimeoutLimit(Integer.parseInt(experimentDescription.get("node_timeout"))*60);
//		gaAutoMLC.setGeneraltimeLimit(10);
//		gaAutoMLC.setTimeoutLimit(60);
		gaAutoMLC.setExperimentName(String.valueOf(experimentEntry.getId()));
		gaAutoMLC.setSaveResults(false);
		gaAutoMLC.setNumberOfThreads(8);
		gaAutoMLC.setXMLAlgorithmsFile(new File("algorithms.xml"));
		gaAutoMLC.registerListener(new SolutionUploader(connection, System.currentTimeMillis()));
		this.evaluateMLClassifier(train, connection, gaAutoMLC);

		System.out.println("Done with evaluation. Send job result.");

		Map<String, Object> results = new HashMap<>();
		results.put("completed", true);
		results.put("classifier_string", gaAutoMLC.getBestALgorithm().getM_individualInString());
		results.put("value", gaAutoMLC.getBestALgorithm().setEvaluation());
		processor.processResults(results);

		System.out.println("Evaluation task completed.");
	}

	private void evaluateMLClassifier(final Instances train, final ResultsDBConnection connection, GAAutoMLC classifier)
			throws Exception {
		System.out.println("Evaluate Classifier...");
		classifier.buildClassifier(train);
		ResultsEval results = GAAutoMLCEvaluator.evaluateClassifier(classifier);
		System.out.println("Done evaluating Classifier.");

		System.out.println("Store results in DB...");
		HashMap<String, Double> metrics = new HashMap<>();
		GAAutoMLCMetricGetter.multiLabelMetrics.forEach(metric -> {
			metrics.put(metric, GAAutoMLCMetricGetter.getValueOfMetricForAutoMekaGGP(metric, results));
			
		});
		connection.addFinalMeasurements(metrics);
		System.out.println("Stored results in DB.");
	}

	public static void main(final String[] args) {
		ExperimentRunner runner = new ExperimentRunner(new GAAutoMLCExperimenter());
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
