package classifying;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.Cluster;
import clustering.Clusters;
import clustering.Vector;
import preprocessing.Article;
import preprocessing.CommonElement;
import preprocessing.Feature;
import preprocessing.Normalize;
import preprocessing.Stopwords;

public class KNN {
	
	private final Charset encoding = StandardCharsets.US_ASCII;
	private String resourcePath;
	private Normalize normalize;
	private Clusters clusters;
	private Map<String, String> vectorClass;
	
	public KNN(String resourcePath, Normalize normalize, Clusters clusters) {
		setResourcePath(resourcePath);
		setNormalize(normalize);
		setClusters(clusters);
		setVectorClass(clusters);
	}
	
	public String kNearestNeighbors(String filepath, int k, boolean isFuzzy, String metric) throws IOException {
		Vector doc = trainKNN(filepath);
		ArrayList<Pair> neighbors = findNeighbors(doc, metric);
		// The first k elements in neighbors is the k closest neighbors to the document.
		// We wish to determine wish to determine the class of the majority of the neighbors
		// and classify the new document as that class.
		Map<String, Integer> kClasses = new HashMap<String, Integer>();
		String classLabel = null;
		for(int i=0; i<k; i++) {
			classLabel = getVectorClass(neighbors.get(i).getVectorLabel());
			if(!kClasses.containsKey(classLabel)) {
				kClasses.put(classLabel, 1);
			}
			else {
				int oldVal = kClasses.get(classLabel);
				kClasses.put(classLabel, oldVal+1);
			}	
		}
		
		// Fuzzy KNN -- return the percentage with which the document
		// belongs to each of the categories
		if(isFuzzy) {
			String percentages = "";
			for(String key : kClasses.keySet()) {
				percentages += String.format("%.2f", 100.0*kClasses.get(key)/k) + "% about " + key + "; ";
			}
			return percentages;
		}
		else {
			String maxClass = classLabel;
			int maxFreq = kClasses.get(classLabel);
			for(String key : kClasses.keySet()) {
				int curFreq = kClasses.get(key);
				if(curFreq > maxFreq) {
					maxClass = key;
					maxFreq = curFreq;
				}
			}
			return maxClass;
		}	
	}
	
	public Vector trainKNN(String filePath) throws IOException {
		Vector documentVector = preprocess(filePath);
		return documentVector;
	}
	
	public ArrayList<Pair> findNeighbors(Vector doc, String measure) {
		ArrayList<Pair> distances = new ArrayList<Pair>(); 
		for(int i=0; i<getClusters().size(); i++) {
			Cluster curCluster = getClusters().get(i);
			for(int j=0; j<curCluster.getSize(); j++) {
				distances.add(new Pair(curCluster.getVectors().get(j).getVectorLabel(),
						Vector.distance(measure, doc, curCluster.getVectors().get(j))));
			}
		}
		Collections.sort(distances);
		return distances;
	}
	

	private Vector preprocess(String filePath) throws IOException {
		// store the new document in an Article object
		Article curArticle = new Article(filePath, encoding, getResourcePath(), (new CommonElement()));
		// create a Stopwords objects that loads the stopwords from resources
		Stopwords stopwords = new Stopwords(resourcePath, encoding);
		// Remove the stopwords from the new article
		stopwords.removeStopwords(curArticle);
		
		// Normalize the vector using term frequency. Inverse term 
		// frequency is not used because wer'e processing a single document.
		normalize(curArticle.getFeatures());
	
		List<Feature> featureVector = curArticle.getFeatures();

		double[] tfidfVec = new double[getNormalize().getTfidf()[0].length];
		
		for(Feature f : featureVector) {
			for(Integer i : getNormalize().getFeatureMap().keySet()) {
				if(f.getKey().equalsIgnoreCase(getNormalize().getFeatureMap().get(i))) {
					tfidfVec[i-1] = f.getTermFrequency();
				}
			}
		}
		// Return a tfidf vector representation of the new document.
		return new Vector(tfidfVec,curArticle.getFileName());

	}
	
	private void normalize(List<Feature> features) {
		// count the number of features in the vector of features
		int numFeatures = 0;
		for(Feature feature : features) {
			numFeatures += feature.getFrequency();
		}
		// compute the term frequency
		for(Feature feature : features) {
			feature.setTermFrequency(1.0*feature.getFrequency()/numFeatures);
		}
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public Normalize getNormalize() {
		return normalize;
	}

	public void setNormalize(Normalize n) {
		this.normalize = n;
	}

	public ArrayList<Cluster> getClusters() {
		return this.clusters.getClusters();
	}

	public void setClusters(Clusters clusters) {
		this.clusters = clusters;
	}

	public String getVectorClass(String vectorLabel) {
		return this.vectorClass.get(vectorLabel);
	}

	public void setVectorClass(Clusters clusters) {
		// Compile a list of each vector and its class
		this.vectorClass = new HashMap<String, String>();
		for(Cluster c : clusters.getClusters()) {
			for(Vector v : c.getVectors()) {
				this.vectorClass.put(v.getVectorLabel(), c.getPredictedLabel());
			}
		}
	}
	
}
