package preprocessing;

import java.io.Serializable;

public class Pair implements Serializable, Comparable<Pair> {
	
	// last modified on 06/23/2021
	private static final long serialVersionUID = 1001;
	
	private int key;
	private double value;
	
	public Pair(int key, double value) {
		setKey(key);
		setValue(value);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toString() {
		return "(" + getKey() + "," + getValue() + ")";
	}

	public int compareTo(Pair o) {
		if (this == o) {return 0;}
		if (this.getValue() < o.getValue()) {return -1;}
		if (this.getValue() == o.getValue()) {return 0;}
		return 1;
	}

}
