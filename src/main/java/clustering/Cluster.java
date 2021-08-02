package clustering;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Joshua Habif
 * A Cluster object is a collection of vectors
 *
 */
public class Cluster implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Vector> vectors;
	private Vector centeroid;
	private String predictedLabel;
	
	public Cluster(Vector centeroid) {
		setCenteroid(centeroid);
		setVectors(new ArrayList<Vector>());
		addVector(centeroid);
	}
	
	public Cluster() {
		setVectors(new ArrayList<Vector>());
	}
	
	public void addVector(Vector vec) {
		if(getSize()==0) {
			setCenteroid(vec);
			getVectors().add(vec);
		}
		else {
			getVectors().add(vec);
		}
	}
	
	public ArrayList<Vector> getVectors() {
		return vectors;
	}
	
	public void setVectors(ArrayList<Vector> vectors) {
		this.vectors = vectors;
	}
	
	public Vector getCenteroid() {
		return centeroid;
	}
	
	public void setCenteroid(Vector centeroid) {
		this.centeroid = centeroid;
	}
	
	public int getSize() {
		return getVectors().size();
	}
	
	public String getPredictedLabel() {
		return predictedLabel;
	}

	public void setPredictedLabel(String predictedLabel) {
		this.predictedLabel = predictedLabel;
	}

	public String toString() {
		String s = "Predicted Label: " + getPredictedLabel() 
			+ "\n" + getCenteroid().toString() + "\n";
		for(Vector v : this.vectors) {
			s += v.toString() + "\n";
		}
		return s;
		
	}
	
}
