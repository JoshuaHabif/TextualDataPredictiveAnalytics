package clustering;

import java.io.Serializable;

public class Vector implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	private double[] vector;
	private String vectorLabel;
	private double[] distances;
	
	public Vector(double[] vec, String vectorLabel) {
		setVector(vec);
		setVectorLabel(vectorLabel);
		setDistances(new double[Main.K]);
	}
	
	public double getDistanceToClusterCenteroid(int clusterID) {
		return getDistances()[clusterID];
	}
	
	public void setDistanceToClusterCenteroid(int clusterID, double distance) {
		getDistances()[clusterID] = distance;
	}
	
	public double get(int i) {
		return getVector()[i];
	}

	public double[] getVector() {
		return vector;
	}

	public void setVector(double[] vector) {
		this.vector = vector;
	}
	
	public int getSize() {
		return getVector().length;
	}
	
	public String getVectorLabel() {
		return vectorLabel;
	}

	public void setVectorLabel(String vectorLabel) {
		this.vectorLabel = vectorLabel;
	}

	public double[] getDistances() {
		return distances;
	}

	public void setDistances(double[] distances) {
		this.distances = distances;
	}
	
	public double min(int size) {
		double min = getDistances()[0];
		for(int i=1; i<size; i++) {
			if(getDistances()[i]<min) {
				min = getDistances()[i];
			}
		}
		return min;
	}

	
	public int closestCluster(int size) {
		double min = getDistances()[0];
		int index = 0;
		for(int i=1; i<size; i++) {
			if(getDistances()[i]<min) {
				min = getDistances()[i];
				index = i;
			}
		}
		return index;
	}

	public String toString() {
		
		String s="(" + getVectorLabel() + ",";
		for(int i=0; i<getDistances().length; i++) {
			s += String.format("%.3f",getDistanceToClusterCenteroid(i)) + ",";
		}
		return (s + ")");
	}
	
	public String toStringVerbose() {
		String s="(" + getVectorLabel() + ",";
		for(int i=0; i<getVector().length; i++) {
			s += String.format("%.4f",getVector()[i]) + ",";
		}
		return s + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		if(!(o instanceof Vector)) {
			return false;
		}
		Vector v = (Vector)o;
		return this.getVectorLabel().equals(v.getVectorLabel());
		
	}
	
	public static double distance(String measure, Vector v, Vector u) {
		if(v.getSize()!=u.getSize()) {return -1;}
		if(measure.equals("Euclidean")) {
			return Vector.euclideanDistance(v,u, v.getSize());
		}
		return Vector.cosineDistance(v,u,v.getSize());
	}
	
	private static double euclideanDistance(Vector v, Vector u, int dimensions) {
		double squareDiff = 0;
		for(int d=0; d<dimensions; d++) {
			squareDiff += Math.pow((v.get(d) - u.get(d)),2);
		}
		return Math.sqrt(squareDiff);
	}
	
	private static double cosineDistance(Vector v, Vector u, int dimensions) {
		double squaredSumV=0;
		double squaredSumU=0;
		for(int d=0; d<dimensions; d++) {
			squaredSumV += Math.pow(v.get(d),2);
			squaredSumU += Math.pow(u.get(d),2);
		}
		// since TFIDF ensures the vectors are in a positive space
		// we may define the distance as 1-similarity
		return 1-(dotProduct(v,u, dimensions)/(Math.sqrt(squaredSumV)*Math.sqrt(squaredSumU)));
	}
	
	private static double dotProduct(Vector v, Vector u, int dimensions) {
		double dotSum=0;
		for(int d=0; d<dimensions; d++) {
			dotSum += v.get(d)*u.get(d);
		}
		return dotSum;
	}
}
