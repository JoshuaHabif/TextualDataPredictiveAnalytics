package preprocessing;

import java.util.HashSet;

public class CommonElement {
	
	private HashSet<String> features;
	
	public CommonElement() {
		setFeatures();
	}

	public HashSet<String> getFeatures() {
		return features;
	}

	public void setFeatures() {
		this.features = new HashSet<String>();
	}
	
	public String toString() {
		return getFeatures().toString();
	}
}
