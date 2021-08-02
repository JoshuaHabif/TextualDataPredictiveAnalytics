package clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import preprocessing.Normalize;
import preprocessing.Output;

/**
 * LAST TESTED ON 06/28/2021 AT 3:09PM
 * @author Joshua Habif
 *
 */
public class Main {
	
	protected static final int K=3;
	private static String[] classes;
	private static ConfusionMatrix cm;
	private static Normalize n;
	private static Input in;
	private static KMeans kmeans;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// load the seraialized Normalize object into memory from resources folder
		in = new Input(args[0]+"normalize");
		
		// deserialize the object and store in n
		setNormalize();
		
		// initialize KMeans object
		kmeans = new KMeans(n.getTfidf(),K,n.getRowMap());
			
	
		// The KMeans algorithm used is an implementation to the one described in
		// Mining Massive Data Sets.
		kmeans.learn("Cosine");
		
		// compute class labels from n.folderFeature
		// classes is required by predictClassLabel, ConfusionMatrix
		retrieveClasses();
			
		// predict class labels for each cluster
		// based on maximal TFIDF of the topics stored in n.getDocFolderTopics()
		predictClassLabels();
		
		// prepare arguments for ConfusionMatrix constructor
		ArrayList<String> predictedClass = new ArrayList<String>();
		ArrayList<String> actualClass = new ArrayList<String>();
		for(Cluster c :  kmeans.getClusters()) {
			for(Vector v : c.getVectors()) {
				actualClass.add(n.getFolderFeature().get(v.getVectorLabel().split("/")[0]));
				predictedClass.add(c.getPredictedLabel());
			}
		}
		// instantiate ConfusionMatrix object
		cm = new ConfusionMatrix(predictedClass, actualClass, classes);
		cm.generateConfusionMatrix();
		// print confusion matrix
		Output outCM = new Output(args[0]+"confusion_matrix.txt");
		outCM.write(cm.toString());
		
		// calculate precision, recall, and f-measure
		double precision = cm.computePrecision();
		double recall = cm.computeRecall();
		double fscore = 2*(precision*recall)/(precision+recall);
		System.out.println("*******************");
		System.out.println("*  F-SCORE: " + String.format("%.2f",fscore) + "  *");
		System.out.println("*******************");
		
	
		outCM.write("Model Precision: " + precision);
		outCM.write("Model Recall: " + recall);
		outCM.write("Model F-Score: " + fscore);
		
		outCM.generateOutput();
		
		// calculate PCA on TFIDF
		// and save the matrix for plotting in .../resources
		visualizeResults(args);
		
		// seralize the Clusters object so that it can be used by the Classifying package
		Output clusters = new Output(args[0] + "clusters");
		clusters.serialize(kmeans.getClusters());
	}
	
	private static void visualizeResults(String[] a) throws IOException {
		double[][] matrix = new double[n.getTfidf().length+K][n.getTfidf()[0].length];
		for(int i=0; i<n.getTfidf().length; i++) {
			matrix[i] = n.getTfidf()[i];
		}
		for(int i=0; i<K; i++) {
			matrix[i+n.getTfidf().length] = kmeans.getClusters().getCenteroid(i).getVector();
		}
		PCA project = new PCA(matrix);
		project.projectMatrix(K);
		double[][] result = project.getResult();
		String s = "";
		for(int i=0; i<result[0].length; i++) {
			s+="PCA"+(i+1)+",";
		}
		s+="Text,Articles About,Predicted Topic\n";
		for(int i=0; i<result.length; i++) {
			for(int j=0; j<result[i].length; j++) {
				s += result[i][j] + ",";
			}
			if(i<result.length-K) {
				String[] docName = n.getRowMap().get(i+1).split("/");
				s+=  docName[0] + "-" + docName[1].split("0")[1].replaceAll(".txt", "") + "," +
						n.getFolderFeature().get(docName[0]).replaceAll("_", " ").toLowerCase() 
						+ "," + getPredictedClassByVecLabel(n.getRowMap().get(i+1)).replaceAll("_", " ").toLowerCase() + "\n";
			}
			else {
				s+="Centeroid,NA(Centeroid),NA(Centeroid)\n";
			}
		}
		// save the data matrix in resources
		Output oriScatterPlotData = new Output(a[0]+"pca_tfidf.csv");
		oriScatterPlotData.write(s);
		oriScatterPlotData.generateOutput();
	}
	
	private static void setNormalize() throws ClassNotFoundException, IOException {
		n = (Normalize)in.deserialize();
		in.shutDown();
	}
	
	private static void predictClassLabels() {
		for(Cluster c : kmeans.getClusters()) {
			c.setPredictedLabel(predictClassLabel(c));
		}
	}
	
	private static String predictClassLabel(Cluster c) {
		String curTopic = classes[0];
		String bestTopic = curTopic;
		double curTFIDFValue = c.getCenteroid().get(getFeatureIndex(curTopic)-1);
		double maxTopic = curTFIDFValue;
		for(int i=1; i<classes.length; i++) {
			curTopic = classes[i];
			curTFIDFValue = c.getCenteroid().get(getFeatureIndex(curTopic)-1);
			if(curTFIDFValue>maxTopic) {
				bestTopic = curTopic;
				maxTopic = curTFIDFValue;
			}
		}
		return bestTopic;
	}
	
	private static int getFeatureIndex(String feature) {
		java.util.Iterator<Entry<Integer, String>> it = n.getFeatureMap().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			if(pair.getValue().equals(feature)) {
				return (Integer) pair.getKey();
			}
		}
		return -1;
	}

	private static int numPredicted() {
		String[] c = new String[kmeans.getClusters().getSize()];
		for(int i=0; i<kmeans.getClusters().getSize(); i++) {
			c[i] = kmeans.getClusters().getCluster(i).getPredictedLabel();
		}
		return countDistinct(c);
	}
	
	private static int countDistinct(String[] arr) {
		int n = 1;
		for(int i=1; i<arr.length; i++) {
			int j=0;
			for(j=0; j<arr.length; j++) {
				if(arr[i].equals(arr[j])) {
					break;
				}
			}
			if(i==j) {n++;}
		}
		return n;
	}
	
	private static void retrieveClasses() {
		classes = new String[n.getFolderFeature().size()];
		int i=0;
		for(Entry<String, String> it : n.getFolderFeature().entrySet()) {
			classes[i] = it.getValue();
			i++;
		}
	}
	
	private static String getPredictedClassByVecLabel(String vecLabel) {
		for(Cluster c : kmeans.getClusters()) {
			for(Vector v : c.getVectors()) {
				if(v.getVectorLabel().equals(vecLabel)) {
					return c.getPredictedLabel();
				}
			}
		}
		return "could not find predicted label";
	}
}