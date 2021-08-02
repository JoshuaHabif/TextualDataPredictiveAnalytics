package clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Clusters implements Iterable<Cluster>, Serializable{
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Cluster> clusters;

	public Clusters() {
		setClusters(new ArrayList<Cluster>());
	}
	
	public Vector getCenteroid(int clusterID) {
		return getCluster(clusterID).getCenteroid();
	}
	
	public void addCluster(Cluster c) {
		getClusters().add(c);
	}
	
	public Cluster getCluster(int clusterID) {
		return getClusters().get(clusterID);
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public int getSize() {
		return getClusters().size();
	}
	
	// return the total number of vectors stored in all the clusters as an integer
	public int totalVec() {
		int sum=0;
		for(int i=0; i<getSize(); i++) {
			sum += getCluster(i).getSize();
		}
		return sum;
	}
	
	public String toString() {
		String s="";
		for(int i=0; i<getSize(); i++) {
			s += "ClusterID " + i + ":\n" +getCluster(i).toString() +"\n";
		}
		return s;
	}

	public Iterator<Cluster> iterator() {
		return getClusters().iterator();
	}
}
