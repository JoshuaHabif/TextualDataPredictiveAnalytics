package clustering;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import preprocessing.Normalize;

public class Input {
	
	private FileInputStream fileInputStream;
	private ObjectInputStream objectInputStream;
	private String resourcePath;
	
	public Input(String resourcePath) throws IOException {
		setResourcePath(resourcePath);
		setFileInputStream();
		setObjectInputStream();
	}
	
	public void shutDown() throws IOException {
		getObjectInputStream().close();
	}
	
	public Object deserialize() throws ClassNotFoundException, IOException {
		return getObjectInputStream().readObject();
	}
	
	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}
	public void setFileInputStream() throws FileNotFoundException {
		this.fileInputStream = new FileInputStream(getResourcePath());;
	}
	public ObjectInputStream getObjectInputStream() {
		return objectInputStream;
	}
	public void setObjectInputStream() throws IOException {
		this.objectInputStream = new ObjectInputStream(getFileInputStream());
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	
	
	
}
