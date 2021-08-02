package preprocessing;

import java.util.HashSet;

public class FeatureTable {
	
	private int rows;
	private int columns;
	private String[][] matrix;
	private int rowIndex;
	
	public FeatureTable(int rows, HashSet<String> labels) {
		setRows(rows+1);
		setColumns(labels.size()+1);
		setMatrix(new String[getRows()][getColumns()+1], labels);
	}
	
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public String[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(String[][] matrix, HashSet<String> labels) {
		this.matrix = matrix;
		String[] header = new String[columns];
		header[0] = " ";
		int i=1;
		for(String s : labels) {
			header[i]=s;
			i++;
		}
		getMatrix()[0] = header;
		this.rowIndex=1;
	}
	
	public void addRow(Article a) {
		String[] row = new String[getColumns()+1];
		row[0] = a.getFileName();
		for(Feature f : a.getFeatures()) {
			row[findLabelIndex(f.getKey())] = f.getFreqAsStr();
		}
		getMatrix()[this.rowIndex] = row;
		this.rowIndex++;
	}
	
	public int findLabelIndex(String label) {
		for(int i=1; i<getMatrix()[0].length; i++) {
			if(getMatrix()[0][i].equals(label)) {
				return i;
			}
		}
		return -1;
	}
	
	public String toString() {
		String s="";
		for(int i=0; i<getRows(); i++) {
			for(int j=0; j<getColumns(); j++) {
				s+=getMatrix()[i][j] + ",";
			}
			s+="\n";
		}
		return s;
	}
	
}
