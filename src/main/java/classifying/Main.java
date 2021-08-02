package classifying;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import clustering.Clusters;
import clustering.ConfusionMatrix;
import clustering.Input;
import preprocessing.Normalize;
import preprocessing.Output;

public class Main {
	
	private static final String METRIC = "Cosine";
	private static final boolean ISFUZZY = false;
	private static final String DATASETNAME = "documents.txt";
	private static final String TOPICSFILE = "topics.txt";
	private static final String CLASSLABELS = "class_labels.txt";
	private static final int K = 5;
	private static Normalize normalize;
	private static Clusters clusters;
	private static HashMap<String, String> classifications;
	private static HashMap<String, String> folderTopics;
	private static HashMap<String, String> classLabels;
	
	private static KNN knn;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		// Load the seraialized Normalize object into memory from resources folder and then desearlize it.
		// We use the normalize seralized object created in pre-processing because it contains the TFIDF matrix.
		Input in = new Input(args[0]+"normalize");
		normalize = (Normalize) in.deserialize();
		in.shutDown();
		
		// Load the seraialized Clusters object into memory from resources folder
		in = new Input(args[0]+"clusters");
		clusters = (Clusters) in.deserialize();
		in.shutDown();
		
		knn = new KNN(args[0], normalize, clusters);
		
		String[] documents = getFileLines(args[0],DATASETNAME);
		
		String[] topics = null;
		if(!ISFUZZY){
			 topics = getFileLines(args[0], TOPICSFILE);
			folderTopics = new HashMap<String,String>();
			for(String line : topics) {
				String[] curLine = line.split("\t");
				folderTopics.put(curLine[1], curLine[0]);
			}
			String[] realLabels = getFileLines(args[0], CLASSLABELS);
			classLabels = new HashMap<String,String>();
			for(String line : realLabels) {
				String[] curLine = line.split(",");
				classLabels.put(curLine[0], curLine[1]);
			}
			classifications = new HashMap<String,String>();
		}

		for(int i=0; i<documents.length; i++) {
			String classLabel = knn.kNearestNeighbors(args[0] + documents[i], K, ISFUZZY, METRIC);
			System.out.println("Predicted Class Label of document " + documents[i] + " is: " + classLabel);
			if(!ISFUZZY) {
				classifications.put(documents[i], folderTopics.get(classLabel));
			}
		}
		
		if(!ISFUZZY) {
			ConfusionMatrix cm = constructConfusionMatrix(classLabels, classifications, documents, topics);
			cm.generateConfusionMatrix();
			double recall = cm.computeRecall();
			double precision = cm.computePrecision();
			double accuracy = cm.computeAccuracy();
			System.out.println(generateOutputToConsole(K, METRIC, ISFUZZY, recall, precision, accuracy));
			Output confusionMatrixOutput = new Output(args[0] + "classifcation_confusion_matrix.txt");
			confusionMatrixOutput.write(cm.toString());
			confusionMatrixOutput.generateOutput();
			// Uncomment to test all K values
			//gridSearch(documents, args, topics);
		}	
	}
	
	public static void gridSearch(String[] documents, String[] args, String[] topics) throws IOException {
		Output grid = new Output(args[0]+"classification_performance.csv");
		grid.write("k,precision,recall,accuracy");
		for(int k=1; k<24; k++) {
			for(int i=0; i<documents.length; i++) {
				String classLabel = knn.kNearestNeighbors(args[0] + documents[i], k, ISFUZZY, METRIC);
				classifications.put(documents[i], folderTopics.get(classLabel));
			}
			ConfusionMatrix cm = constructConfusionMatrix(classLabels, classifications, documents, topics);
			cm.generateConfusionMatrix();
			grid.write(k+","+cm.computePrecision()+","+cm.computeRecall()+","+cm.computeAccuracy());
		}
		grid.generateOutput();
	}
	public static ConfusionMatrix constructConfusionMatrix(HashMap<String,String> classLabels, 
			HashMap<String,String> classifications, String[] documents, String[] topics) {
		
		ArrayList<String> predictedClass = new ArrayList<String>();
		ArrayList<String> actualClass = new ArrayList<String>();
		for(int i=0; i<documents.length; i++) {
			predictedClass.add(classifications.get(documents[i]));
			actualClass.add(classLabels.get(documents[i]));
		}
		String[] classes = new String[topics.length];
		for(int i=0; i<topics.length; i++) {
			String[] curTopic = topics[i].split("\t");
			classes[i] = curTopic[0];
		}
		return (new ConfusionMatrix(predictedClass, actualClass, classes));
	}
	
	public static String[] getFileLines(String fileDirectory,String fileName) throws IOException {
		return (new String(Files.readAllBytes(Paths.get(fileDirectory+fileName)))).split("\n");
	}
	
	public static String generateOutputToConsole(int k, String metric, boolean isFuzzy, double recall, double precision, double accuracy) {
		String divider = "**********************************\n";
		String out = divider 
				+ "\tProgram Parameters:\n"
				+ "\tk = " + k + "\n"
				+ "\tMetric = " + metric + "\n"
				+ "\tisFuzzy = " + isFuzzy + "\n"
				+ "\n\tProgram Performance:\n"
				+ "\tRecall: " + String.format("%.2f",recall) + "\n"
				+ "\tPrecision: " +String.format("%.2f", precision) + "\n"
				+ "\tAccuracy: " + String.format("%.2f",accuracy) + "\n"
				+ divider;
		return out;
	}

}
