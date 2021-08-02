package preprocessing;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Output {
	
	private String outputPath;
	// Creates a FileOutputStream
	private FileOutputStream file;
	// Creates a BufferedOutputStream
	private BufferedOutputStream buffer;
	
	public Output(String outputPath) throws FileNotFoundException {
		setOutputPath(outputPath);
		setFile(outputPath);
		setBuffer();
	}
	
	private void setOutputPath(String path) {
		this.outputPath = path;
	}
	
	private String getOutputPath() {
		return this.outputPath;
	}
	
	private void setFile(String outputPath) throws FileNotFoundException {
		this.file = file = new FileOutputStream(outputPath);
	}
	
	private void setBuffer() {
		this.buffer = new BufferedOutputStream(file);
	}
	
	private BufferedOutputStream getBuffer() {
		return this.buffer;
	}
	
	public void write(String str) throws IOException {
		str = str + "\n";
		//Write string to output stream
		getBuffer().write(str.getBytes());
	}
	
	public void generateOutput() throws IOException {
		getBuffer().flush();
		System.out.println("Data flushed to file! " + getOutputPath());
		getBuffer().close();
	}
	
	public void serialize(Object obj) throws IOException {
		FileOutputStream f = new FileOutputStream(getOutputPath());
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(obj);
		o.flush();
		o.close();
		System.out.println("Searlized to " + getOutputPath());
	}
	
}
