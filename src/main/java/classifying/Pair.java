package classifying;

public class Pair implements Comparable {
	
	private String vectorLabel;
	private double distanceToVector;
	
	public Pair(String vectorLabel, double distanceToVector) {
		setVectorLabel(vectorLabel);
		setDistanceToVector(distanceToVector);
	}
	
	public String getVectorLabel() {
		return vectorLabel;
	}
	public void setVectorLabel(String vectorLabel) {
		this.vectorLabel = vectorLabel;
	}
	public double getDistanceToVector() {
		return distanceToVector;
	}
	public void setDistanceToVector(double distanceToVector) {
		this.distanceToVector = distanceToVector;
	}
	
	public String toString() {
		return "(" + getVectorLabel() + ", " + getDistanceToVector() + ")";  
	}

	public int compareTo(Object o) {
		return Double.compare(this.getDistanceToVector(), ((Pair)o).getDistanceToVector());
	}
	

}
