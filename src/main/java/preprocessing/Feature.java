package preprocessing;

public class Feature {
	
	private String key;
	private int frequency;
	private double termFrequency;
	
	public Feature(String key, int freq) {
		setKey(key);
		setFrequency(freq);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		if(!(o instanceof Feature)) {
			return false;
		}
		Feature f = (Feature)o;
		return this.getKey().equals(f.getKey());
		
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void incrementFrequency() {
		this.frequency++;
	}
	
	public String getFreqAsStr() {
		return getFrequency() + "";
	}
	
	public double getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(double termFrequency) {
		this.termFrequency = termFrequency;
	}

	public String toString() {
		return "("+getKey() + "," + getFrequency()+ "," + getTermFrequency()+")";
	}
	
	

}
