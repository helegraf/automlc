package meka.classifiers.multilabel.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import meka.classifiers.multilabel.meta.automekaggp.core.ResultsEval;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPEvaluator;
import meka.classifiers.multilabel.meta.automekaggp.evaluation.AutoMekaGGPMetricGetter;
import meka.core.MLUtils;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;

public class Main {

	public static void main(String[] args) throws Exception {

		Instances train = readFile("datasets/classification/multi-label/emotion.arff");

		// Create algo
		File tempFile = File.createTempFile("training", ".arff");
		tempFile.deleteOnExit();
		 ArffSaver saver = new ArffSaver();
		 saver.setInstances(train);
		 saver.setFile(tempFile);
		 saver.writeBatch();
		
		AutoMEKA_GGP autoMekaGGP = new AutoMEKA_GGP(new String[] {"-t", tempFile.getAbsolutePath(), "-T", tempFile.getAbsolutePath()});
		
		//autoMekaGGP.setAnytime(true);
		autoMekaGGP.setGeneraltimeLimit(1);
		autoMekaGGP.setAlgorithmTimeLimit(15);
		autoMekaGGP.setExperimentName("test");
		autoMekaGGP.setSaveResults(false);
		autoMekaGGP.setSavingDirectory("C:/Users/Helena Graf/Desktop");

		autoMekaGGP.buildClassifier(train);	
		
		System.out.println("Got here");
		
		ResultsEval results = AutoMekaGGPEvaluator.evaluateClassifier(autoMekaGGP);
		
		AutoMekaGGPMetricGetter.multiLabelMetrics.forEach(metric -> {
			System.out.println(metric + ": " + AutoMekaGGPMetricGetter.getValueOfMetricForAutoMekaGGP(metric, results));
		});
	}
	
	public static Instances readFile(String name) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(name));
		ArffReader arff = new ArffReader(reader);
		Instances data = arff.getData();
		String [] options = MLUtils.getDatasetOptions(data);
		System.out.println(Arrays.asList(options));
		for (int i = 0; i < options.length-1; i++) {
			if (options[i].equals("-c") || options[i].equals("-C")) {
				System.out.println("Set class index: " + Math.abs(Integer.parseInt(options[i+1])));
				data.setClassIndex(Math.abs(Integer.parseInt(options[i+1])));
				break;
			}
		}
		return data;
	}

}
