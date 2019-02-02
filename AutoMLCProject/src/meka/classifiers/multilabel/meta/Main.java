package meka.classifiers.multilabel.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import com.google.common.eventbus.Subscribe;

import meka.classifiers.multilabel.meta.automekaggp.core.ResultsEval;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPEvaluator;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPMetricGetter;
import meka.classifiers.multilabel.meta.evaluation.IntermediateSolutionEvent;
import meka.classifiers.multilabel.meta.evaluation.SolutionListener;
import meka.classifiers.multilabel.meta.gaautomlc.evaluation.GAAutoMLCEvaluator;
import meka.classifiers.multilabel.meta.gaautomlc.evaluation.GAAutoMLCMetricGetter;
import meka.core.MLUtils;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;

public class Main {

	public static void main(String[] args) throws Exception {

		Instances train = readFile("datasets/classification/multi-label/emotion.arff");

		// Create the file for training (& testing in this case)
		File tempFile = File.createTempFile("training", ".arff");
		tempFile.deleteOnExit();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(train);
		saver.setFile(tempFile);
		saver.writeBatch();

		// Create and configure algorithm
		AutoMEKA_GGP autoMekaGGP = new AutoMEKA_GGP(
				new String[] { "-t", tempFile.getAbsolutePath(), "-T", tempFile.getAbsolutePath() });
		autoMekaGGP.setAnytime(true);
		autoMekaGGP.registerListener(new SolutionListener());
		autoMekaGGP.setGeneraltimeLimit(2);
		autoMekaGGP.setAlgorithmTimeLimit(30);
		autoMekaGGP.setExperimentName("auto");
		autoMekaGGP.setSaveResults(false);
		//autoMekaGGP.setSavingDirectory("/mnt/c/Users/Helena Graf/Desktop");

		// Train and test algorithm
		autoMekaGGP.buildClassifier(train);
		ResultsEval results = AutoMekaGGPEvaluator.evaluateClassifier(autoMekaGGP);

		// Print results
		System.out.println("Results of AutoMekaGGP");
		AutoMekaGGPMetricGetter.multiLabelMetrics.forEach(metric -> {
			System.out.println(metric + ": " + AutoMekaGGPMetricGetter.getValueOfMetricForAutoMekaGGP(metric, results));
		});
		
		// ------------------------- 2nd algo --------------------------
		
		// Create and configure 2nd algorithm
		GAAutoMLC gaAutoMLC = new GAAutoMLC(new String[] { "-t", tempFile.getAbsolutePath(), "-T", tempFile.getAbsolutePath() });
		gaAutoMLC.registerListener(new SolutionListener());
		gaAutoMLC.setAnytime(true);
		gaAutoMLC.setGeneraltimeLimit(2);
		gaAutoMLC.setTimeoutLimit(30);
		gaAutoMLC.setExperimentName("ga");
		gaAutoMLC.setSaveResults(false);
		gaAutoMLC.setXMLAlgorithmsFile(new File ("algorithms.xml"));
		//gaAutoMLC.setXMLAlgorithmsFile(new File ("C:/Users/Helena Graf/Desktop/algorithms.xml"));
		//gaAutoMLC.setSavingDirectory("C:/Users/Helena Graf/Desktop");
		//gaAutoMLC.setSavingDirectory("/mnt/c/Users/Helena Graf/Desktop");
		
		// Train and test algorithm
		gaAutoMLC.buildClassifier(train);
		meka.classifiers.multilabel.meta.gaautomlc.core.ResultsEval results2 = GAAutoMLCEvaluator.evaluateClassifier(gaAutoMLC);
		
		// Print results
		System.out.println("Results of AutoMekaGGP");
		AutoMekaGGPMetricGetter.multiLabelMetrics.forEach(metric -> {
			System.out.println(metric + ": " + GAAutoMLCMetricGetter.getValueOfMetricForAutoMekaGGP(metric, results2));
		});
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
