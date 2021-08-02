package preprocessing;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


public class Stopwords {
	
	private String fileDirectory;
	private List<String> stopwords;
	private static final String fileName = "stop_words.txt";
	private static String content;
	private Charset encoding;
	
	public Stopwords(String fileDir, Charset encoding) {
		setFileDirectory(fileDir);
		setEncoding(encoding);
		setContent();
		setStopwords();
	}
	
	private void setContent() {
		Path p = FileSystems.getDefault().getPath(fileDirectory, this.fileName);
		try {
			this.content = Files.readString(p, getEncoding());
		}
		catch(Exception IOException) {
			System.err.println("Error while reading the file.");
		}
	}
	
	private void setFileDirectory(String fileDir) {
		this.fileDirectory = fileDir;
	}
	
	private void setEncoding(Charset enc) {
		this.encoding = enc;
	}
	
	public Charset getEncoding() {
		return this.encoding;
	}
	
	private String getContent() {
		return this.content;
	}
	
	private void setStopwords() {
		String[] delim = getContent().split("\n");
		List<String> wordList = Arrays.asList(delim);
		this.stopwords = wordList;
	}
	
	public List<String> getStopwords(){
		return this.stopwords;
	}
	
	public void removeStopwords(Article a) {
		String newContent = a.getContent();
		for(String sw : getStopwords()) {
			newContent = newContent.replaceAll(" (?i)"+sw+" ", " ");
		}
		a.setContent(newContent);
		a.lemmatization();
	}
}
