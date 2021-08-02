package preprocessing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Joshua Habif
 * An implementation of TF-IDF (term frequency-inverse document frequency)
 * Designed to be used by Preprocessing.java
 */
public class Normalize implements Serializable {
	
	// last modified on 06/23/2021
	private static final long serialVersionUID = 1001;
	
	private String[][] matrix;
	private int[][] imatrix;
	private double[][] tfidf;
	// stores the row indecies of the tfidf partition
	private Map<String, int[]> tfidfPartition;
	// stores the folder and associated best feature
	private Map<String,String> folderFeature;
	private Map<Integer, String> rowMap;
	private Map<Integer, String> featureMap;
	private Map<Integer, Integer> numTerms;
	private Map<Integer, Integer> termDocFreq;
	private List<Pair> normSumVec;
	
	public Normalize(String[][] matrix) {
		setMatrix(matrix);
		setRowMap();
		setFeatureMap();
		setImatrix();
		setNumTerms();
		setTermDocFreq();
	}
	
	public void computeBestFeature(String[] partition, int[] partitionSize) {
		partitionTFIDFmatrix(partition, partitionSize);
		setFolderFeature(new HashMap<String,String>());
		// feature map has numberoffeatures + 1 elements in it
		int numColumns = getFeatureMap().size()-1;
		for(int i=0; i<partition.length; i++) {
			String p = partition[i];
			double[] pVector = new double[numColumns];
			int[] pRows = getTfidfPartition().get(p);
			// sum by columns
			for(int j=0; j<numColumns; j++) {
				// sum all cells at column j
				double cell=0;
				for(int k=0; k<pRows.length; k++) {
					cell += getTfidf()[pRows[k]][j];
				}
				pVector[j] = cell;
			}
			// now we have the vector so we find the max value and associated index
			double maxVal = pVector[0];
			int index = 0;
			for(int k=0; k<pVector.length; k++) {
				if(pVector[k]>maxVal) {
					maxVal = pVector[k];
					index = k;
				}
			}
			// find the feature associated with the index of max value and put p and that feature in topicmap 
			getFolderFeature().put(p, getFeatureMap().get(index+1));
		}
	}
	
	private void partitionTFIDFmatrix(String[] partition, int[] partitionSize) {
		setTFIDFPartition(new HashMap<String, int[]>());
		// for each label in partition add the corresponding submatrix
		for(int i=0; i<partition.length; i++) {
			// compute submatrix
			int[] subm = new int[partitionSize[i]];
			int index = 0;
			for(int j=0; j<rowMap.size(); j++) {
				if(rowMap.get(j).split("/")[0].equals(partition[i])) {
					subm[index] = j-1;
					index++;
				}
			}
			getTfidfPartition().put(partition[i], subm);
		}
		// diagnostic: print partitions
		/*for(String p : partition) {
			System.out.println("Partition: " + p);
			int[] temp = tfidfPartition.get(p);
			String s = "";
			for(int row : temp) {
				// print tfidf row
				for(int i=0; i<tfidf[0].length; i++) {
					s+=tfidf[row][i] +",";
				}
				s+="\n";
			}
			System.out.print(s);
		}*/
	}
	
	public void tfidf() {
		setTfidf();
		for(int i=0; i<getImatrix().length; i++) {
			for(int j=0; j<getImatrix()[0].length; j++) {
				getTfidf()[i][j] = termFrequency(i,j)*inverseTermFrequency(j);
			}
		}
	}
	
	public double termFrequency(int i, int j) {
		return (getImatrix()[i][j]*1.0)/getNumTerms().get(i);
	}
	
	public double inverseTermFrequency(int j) {
		return Math.log((getImatrix().length*1.0)/getTermDocFreq().get(j));
	}
	
	public void computeNumTerms() {
		for(int i=0; i<getImatrix().length; i++) {
			int sum=0;
			for(int j=0; j<getImatrix()[0].length; j++) {
				sum += getImatrix()[i][j];
			}
			getNumTerms().put(i, sum);
		}
	}
	
	public void computeTermDocumentFreq() {
		for(int i=0; i<getImatrix()[0].length; i++) {
			int sum = 0;
			for(int j=0; j<getImatrix().length; j++) {
				if(getImatrix()[j][i]>0) {sum++;}
			}
			getTermDocFreq().put(i, sum);
		}
	}
	
	public void transformToIntMatrix() {
		// store rowMap
		for(int i=0; i<getMatrix().length; i++) {
			getRowMap().put(i, getMatrix()[i][0]);
		}
		// store feature map
		for(int i=0; i<getMatrix()[0].length; i++) {
			getFeatureMap().put(i, getMatrix()[0][i]);
		}
		for(int i=0; i<getMatrix().length-1; i++) {
			for(int j=0; j<getMatrix()[0].length-1; j++) {
				if(getMatrix()[i+1][j+1] != null) {
					getImatrix()[i][j] = Integer.parseInt(getMatrix()[i+1][j+1]);
				}
				else {
					getImatrix()[i][j] = 0;
				}
			}
		}
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(String[][] matrix) {
		this.matrix = matrix;
	}

	public Map<Integer, String> getRowMap() {
		return rowMap;
	}

	public void setRowMap() {
		this.rowMap = new HashMap<Integer, String>();
	}

	public Map<Integer, String> getFeatureMap() {
		return featureMap;
	}

	public void setFeatureMap() {
		this.featureMap = new HashMap<Integer, String>();
	}

	public int[][] getImatrix() {
		return imatrix;
	}
	
	public void setImatrix() {
		this.imatrix = new int[getMatrix().length-1][getMatrix()[0].length-1];
	}

	public Map<Integer, Integer> getNumTerms() {
		return numTerms;
	}

	public void setNumTerms() {
		this.numTerms = new HashMap<Integer, Integer>();
	}

	public Map<Integer, Integer> getTermDocFreq() {
		return termDocFreq;
	}

	public void setTermDocFreq() {
		this.termDocFreq = new HashMap<Integer, Integer>();
	}

	public double[][] getTfidf() {
		return tfidf;
	}

	public void setTfidf() {
		this.tfidf = new double[getImatrix().length][getImatrix()[0].length];
	}
	
	public List<Pair> getNormSumVec() {
		return normSumVec;
	}

	public void setNormSumVec() {
		this.normSumVec = new ArrayList<Pair>();
	}
	
	public Map<String, int[]> getTfidfPartition() {
		return tfidfPartition;
	}

	public void setTFIDFPartition(HashMap<String, int[]> tfidfPartition) {
		this.tfidfPartition = tfidfPartition;
	}

	public Map<String, String> getFolderFeature() {
		return folderFeature;
	}

	public void setFolderFeature(HashMap<String, String> folderFeature) {
		this.folderFeature = folderFeature;
	}

	public String toString() {
		String s="";
		for(int i=0; i<getTfidf().length; i++) {
			for(int j=0; j<getTfidf()[0].length; j++) {
				s+=getTfidf()[i][j] + ", ";
			}
			s+="\n";
		}
		return s;
	}
	
}
