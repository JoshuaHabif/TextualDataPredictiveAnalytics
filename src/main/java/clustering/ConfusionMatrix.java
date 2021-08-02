package clustering;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class ConfusionMatrix {
	
	private ArrayList<String> predictedClass;
	private ArrayList<String> actualClass;
	private int[][] confusionMatrix;
	private String[] classes;
	
	public ConfusionMatrix(ArrayList<String> predictedClass, ArrayList<String>actualClass, String[] classes) {
		setPredictedClass(predictedClass);
		setActualClass(actualClass);
		setClasses(classes);
		setConfusionMatrix(new int[classes.length][classes.length]);
	}
	
	// recall = average of fraction of instances where we correctly declared i 
	// out of all of the cases where the true state of the instance is i.
	public double computeRecall() {
		double meanRecall = 0;
		int deno = 0;
		for(int i=0; i<getConfusionMatrix().length; i++) {
			int rowSum = 0;
			int diagonalCell = 0;
			for(int j=0; j<getConfusionMatrix()[i].length; j++) {
				rowSum += getConfusionMatrix()[i][j];
				if(i==j && getConfusionMatrix()[i][j] != 0) {
					deno++;
					diagonalCell = getConfusionMatrix()[i][j];
				}
			}
			if(rowSum != 0 ) {
				meanRecall += diagonalCell*1.0/rowSum;
			}
		}
		return meanRecall/deno;
	}
	
	// precision = average of fraction of instances where we correctly declared i out
	// of all the instances where we declared i.
	public double computePrecision() {
		double meanPrecision = 0;
		int deno = 0;
		for(int i=0; i<getConfusionMatrix()[0].length; i++) {
			int diagonalCell=0;
			int colSum = 0;
			for(int j=0; j<getConfusionMatrix().length; j++) {
				colSum += getConfusionMatrix()[j][i];
				if(i==j && getConfusionMatrix()[j][i] != 0) {
					deno++;
					diagonalCell = getConfusionMatrix()[j][i];
				}
			}
			if(colSum != 0) {
				meanPrecision += diagonalCell*1.0/colSum;
			}
		}
		return meanPrecision/deno;
	}
	
	public double computeAccuracy() {
		int diagonalSum = 0;
		for(int i=0; i<getConfusionMatrix()[0].length; i++) {
			diagonalSum += getConfusionMatrix()[i][i];
		}
		return 1.0*diagonalSum/getPredictedClass().size();
	}
	
	public void generateConfusionMatrix() {
		// go column by column
		for(int i=0; i<getConfusionMatrix().length; i++) {
			for(int j=0; j<getConfusionMatrix().length; j++) {
				getConfusionMatrix()[i][j] = count(getClasses()[j], getClasses()[i]);
			}
		}
	}
	
	// answers the question how many predicted s's are actually v's
	// where s may be v or not equal to v
	private int count(String s, String v) {
		int sum=0;
		for(int i=0; i<getPredictedClass().size(); i++) {
			if((getPredictedClass().get(i).equals(s) && getActualClass().get(i).equals(v))) {
				sum++;
			}
		}
		return sum;
	}

	public ArrayList<String> getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(ArrayList<String> predictedClass) {
		this.predictedClass = predictedClass;
	}

	public ArrayList<String> getActualClass() {
		return actualClass;
	}

	public void setActualClass(ArrayList<String> actualClass) {
		this.actualClass = actualClass;
	}

	public int[][] getConfusionMatrix() {
		return confusionMatrix;
	}

	public void setConfusionMatrix(int[][] confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}
	
	public String[] getClasses() {
		return classes;
	}

	public void setClasses(String[] classes) {
		this.classes = classes;
	}

	public String toString() {
		String s = "Columns are Predicted Class and Rows are Actual Class\n,";
		// print column labels
		for(int i=0; i<getClasses().length; i++) {
			s += getClasses()[i] + ",";
		}
		s += "\n";
		for(int i=0; i<getConfusionMatrix().length; i++) {
			s += getClasses()[i] + ",";
			for(int j=0; j<getConfusionMatrix().length; j++) {
				s += getConfusionMatrix()[i][j] + ",";
			}
			s += "\n";
		}
		
		return s;
	}
}
