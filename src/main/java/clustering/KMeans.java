package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of K-Means from Mining Massive Data-sets
 * @author Joshua Habif
 *
 */
public class KMeans {
	
	private double[][] matrix;
	private HashMap<Integer, String> vectorMap;
	private ArrayList<Vector> vectors;
	private int k;
	private Clusters clusters;
	
	public KMeans(double[][] matrix, int k, Map<Integer, String> vectorMap) {
		setMatrix(matrix);
		setK(k);
		setClusters(new Clusters());
		setVectorMap(vectorMap);
		setVectors(new ArrayList<Vector>());
		vectorizeMatrix();
	}
	
	public void learn(String measure) {	
		// 1) choose k points that are likely to be in different clusters
		// make these points the centroids of their clusters
		intializeCenteroids(measure);
		// 2) for each remaining point p do
		int numIterations = getVectors().size();
		for(int i=0; i<numIterations; i++) {
			Vector p = getVectors().get(0);
			// recompute distances before calling closestCluster
			// in the next iteration recalculateCenteroids will be called
			// which assumes the distances to the centeroid have been computed
			// so this method must be called at each iteration.
			computeDistances(measure);
			// 3) find the centeroid to which p is closest
			Cluster c = getClusters().getCluster(p.closestCluster(getClusters().getSize()));
			// 4) add p to the cluster of that centroid
			c.addVector(p);
			// remove the added vector from vectors
			getVectors().remove(0);
			// 5) adjust the centeroid of that cluster to account for p
			recalculateCenteroids();
		}
		// 6) fix the centeroids of the clusters and reassign each
		// point, including the k initial points, to the k clusters.
		// Reassign all vector back to vector list
		recalculateCenteroids();
		for(Cluster c : getClusters()) {
			for(int i=0; i<c.getVectors().size(); i++) {
				Vector v = c.getVectors().remove(i);
				i--;
				getVectors().add(v);
			}
		}
		// recompute distances to centeroids
		computeDistances(measure);
		// reassign vectors to closest cluster
		for(int i=0; i<getVectors().size(); i++) {
			Vector p = getVectors().remove(i);
			i--;
			// 3) find the centeroid to which p is closest
			Cluster c = getClusters().getCluster(p.closestCluster(getClusters().getSize()));
			// 4) add p to the cluster of that centeroid
			c.addVector(p);
		}
	}
	
	public void recalculateCenteroids() {
		// before entering the loop the centeroid was an actual row vector
		// now the centeroid should be the mean of the vectors in the cluster
		// so we create a dummy vector
		for(Cluster c : getClusters()) {
			double[] meanVec = new double[c.getCenteroid().getSize()];
			for(Vector v : c.getVectors()) {
				for(int i=0; i<meanVec.length; i++) {
					// add v's i'th value
					meanVec[i] += v.get(i);
				}
			}
			// after adding all of v's values normalize
			for(int i=0; i<meanVec.length; i++) {
				meanVec[i] = meanVec[i]/c.getVectors().size();
			}
			c.setCenteroid(new Vector(meanVec, "Centeroid"));
		}	
	}
	
	public void intializeCenteroids(String measure) {
		// we want to pick vectors that have a good chance of lying in different clusters
		// we pick k points that are as far away from one another as possible
		// 1) pick the first point to be as far away from the mean of all vectors
		double[] meanVecVals = new double[getVectors().get(0).getVector().length];
		for(Vector curVec : getVectors()) {
			for(int i=0; i<meanVecVals.length; i++) {
				meanVecVals[i] += curVec.getVector()[i]/getVectors().size();
			}
		}
		Vector meanVec = new Vector(meanVecVals, "Mean Vector");
		Vector firstVec = getVectors().get(0);
		double maxDistance = Vector.distance(measure, meanVec, firstVec);
		for(int i=1; i<getVectors().size(); i++) {
			Vector curVec = getVectors().get(i);
			if(maxDistance < Vector.distance(measure, meanVec, curVec)) {
				maxDistance = Vector.distance(measure, meanVec, curVec);
				firstVec = curVec;
			}
		}
		// store the new cluster in clusters and make the centroid of the only existing cluster
		// the vector found in previous line
		newCluster(firstVec);
		getVectors().remove(firstVec);
		// 2) while there are fewer than k points do
		while(getClusters().getSize()<k) {
			// compute the distances before calling findFurthestVector
			computeDistances(measure);
			// 3) add the point whose minimum distance from the selected
			// as large as possible.
			newCluster(findFurthestVector());
		}
	}
	
	public Vector findLikelyCenteroid() {
		double sumMinDistances = 0;
		// for each p in vectors compute d(x), the distance between p
		// and the nearest cluster.
		double[] minDistances = new double[getVectors().size()];
		for(int i=0; i<getVectors().size(); i++) {
			Vector v = getVectors().get(i);
			double minDistance = v.getDistanceToClusterCenteroid(0);
			for(int k=1; k<getClusters().getSize(); k++) {
				double curDistance = v.getDistanceToClusterCenteroid(k);
				if(curDistance < minDistance) {
					minDistance = curDistance;
				}
			}
			minDistances[i] = minDistance;
			sumMinDistances += minDistance;
		}
		// normalize the minDistance array by the sum of all the distances
		for(int i=0; i<minDistances.length; i++) {
			minDistances[i] = minDistances[i]/sumMinDistances;
		}
		// choose one p in vectors at random as a new cluster's centeroid using
		// weighted probability distribution where a point x is chosen with probability
		// proportional to its normalized distance.
		return getVectors().get(weightedProbability(minDistances));
	}
	
	
	// assumes the vector distance array is correct
	public Vector findFurthestVector() {
		Vector maxDistanceVec = null;
		for(Vector v : getVectors()) {
			if(maxDistanceVec == null) {
				maxDistanceVec = v;
				continue;
			}
			if(v.min(getClusters().getSize())>maxDistanceVec.min(getClusters().getSize())) {
				maxDistanceVec = v;
			}
		}
		return maxDistanceVec;
	}
	
	public void computeDistances(String measure) {
		for(Vector v : getVectors()) {
			for(int i=0; i<getClusters().getSize(); i++) {
				computeDistanceToCenteroid(getClusters().getCenteroid(i), v, i, measure);
			}
		}
	}
	
	public void computeDistanceToCenteroid(Vector centeroid, Vector v, int centeroidClusterID, String measure) {
		v.setDistanceToClusterCenteroid(centeroidClusterID, Vector.distance(measure, centeroid, v));
	}
	
	public void newCluster(Vector centeroid) {
		// add the new vector to a new cluster and make that
		// vector the centeroid of that cluster
		getClusters().addCluster(new Cluster(centeroid));
		// remove the this vector from vectors
		vectors.remove(centeroid);
	}
	
	public void vectorizeMatrix() {
		for(int i=0; i<getMatrix().length; i++) {
			// set default clusterID as -1
			vectors.add(new Vector(getMatrix()[i], findRowLabel(i)));
		}
	}
	
	// returns k partitions of the three clusters
	// found by  kmeans in a double[][] representation.
	// method is needed by PCA for plotting
	public ArrayList<double[][]> getClusterPartitions(){
		ArrayList<double[][]> p = new ArrayList<double[][]>();
		// the number of columns is always equal to the number of features
		int numCols = getClusters().getCluster(0).getVectors().get(0).getVector().length;
		for(Cluster c : getClusters()) {
			// determine the number of rows/vectors in the cluster
			double[][] curCluster = new double[c.getVectors().size()][numCols];
			for(int i=0; i<c.getVectors().size(); i++) {
				curCluster[i] = c.getVectors().get(i).getVector();
			}
			// add that curCluster to list
			p.add(curCluster);
		}
		return p;
	}
	
	public String findRowLabel(int rowNumber) {
		return getVectorMap().get(rowNumber+1);
	}
	
	public double[][] getMatrix() {
		return matrix;
	}
	
	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	
	public int getK() {
		return k;
	}
	
	public void setK(int k) {
		this.k = k;
	}

	public Clusters getClusters() {
		return clusters;
	}

	public void setClusters(Clusters clusters) {
		this.clusters = clusters;
	}

	public HashMap<Integer, String> getVectorMap() {
		return vectorMap;
	}

	public void setVectorMap(Map<Integer, String> vectorMap) {
		this.vectorMap = (HashMap<Integer, String>)vectorMap;
	}
	
	public ArrayList<Vector> getVectors() {
		return vectors;
	}

	public void setVectors(ArrayList<Vector> vectors) {
		this.vectors = vectors;
	}
	
	public String toString() {
		String s = "";
		for(Vector v : getVectors()) {
			s += v.toString() + "\n";
		}
		return s;
	}
	
	public static int weightedProbability(double[] probabilities) {	
		double rnd = (new Random()).nextDouble();
		for(int i=0; i<probabilities.length; i++) {
			if(rnd < probabilities[i]) {
				return i;
			}
			rnd -= probabilities[i];
		}
		return -1;
	}
}
