package preprocessing;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
//import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


public class Article {
	
	private String content;
	private String fileName;
	private Charset encoding;
	private String fileDirectory;
	private Boolean removedStopwords;
	private List<Feature> features;
	private CommonElement commonElem;
	private String resourcePath;
	//StanfordCoreNLP Objects
	private Properties props;
	private StanfordCoreNLP pipeline;
	private CoreDocument document;
	private List<CoreLabel> tokens;

	public Article(String fileName,  String fileDir, String resourcePath ,Charset encoding, CommonElement cmElem) throws IOException {
		setFileName(fileName);
		setEncoding(encoding);
		setFileDirectory(fileDir);
		setCommonElem(cmElem);
		//Read the file and store it into a string
		setContent();
		setRemovedStopwords(false);
		setFeatures();
		setResourcePath(resourcePath);
	}
	
	public Article(String filePath, Charset encoding, String resourcePath, CommonElement cmElem) {
		setFileName(filePath);
		setEncoding(encoding);
		setCommonElem(cmElem);
		setContent();
		setRemovedStopwords(false);
		setFeatures();
		setResourcePath(resourcePath);
	}
	
	public void nGramExtraction() {
		HashMap<String, Integer> nGrams = new HashMap<String, Integer>();
		int sumFrequencies = 0;
		for(int i=0; i<getTokens().size()-1; i++) {
			// check if nGram was already counted
			String curPair = getTokens().get(i).lemma().toLowerCase()
					+"_"+ getTokens().get(i+1).lemma().toLowerCase();
			boolean didAdd = false;
			if(!nGrams.containsKey(curPair)) {
				nGrams.put(curPair, 0);
				// count the number of occurrences of curPair
				for(int j=0; j<getTokens().size()-1; j++) {
					String thisPair = getTokens().get(j).lemma().toLowerCase()
							+"_"+ getTokens().get(j+1).lemma().toLowerCase();
					if(curPair.equals(thisPair)) {
						nGrams.put(curPair, nGrams.get(curPair)+1);
						sumFrequencies++;
						j++;
						didAdd = true;
					}
				}
			}
			if(didAdd) {i++;}
		}
		// pick only the g-grams that occur with frequency of > 4.5%
		for(String key : nGrams.keySet()) {
			if(nGrams.get(key)*1.0/sumFrequencies>0.045) {
				getCommonElem().getFeatures().add(key);
				getFeatures().add(new Feature(key, nGrams.get(key)));
			}
		}
	}
	
	public void removePunctuationTokens() {
		List<CoreLabel> tokens = getTokens();
		int size = tokens.size();
		for(int i=0; i<size; i++) {
			if(tokens.get(i).word().matches("\\p{P}")) {
				tokens.remove(i);
				size--;
				i--;
			}
		}
	}

	public void removedStopwords() {
		//Apply Stemming and Lemmatization only after the stopwords were removed
		//Use StanfordCoreNLP
		if(getRemovedStopwords()) {
			setProps();
			setPipeline();
			setDocument();
			setTokens();
		}
	}
	
	public void addNERFeatures() {
		for(CoreEntityMention em : getDocument().entityMentions()) {
			getCommonElem().getFeatures().add(em.entityType());
			Feature curFeature = new Feature(em.entityType(), 1);
			if(!getFeatures().contains(curFeature)) {
				getFeatures().add(curFeature);
			}
			else {
				getFeatureByKey(curFeature).incrementFrequency();
			}
		}
	}
	
	public Feature getFeatureByKey(Feature f) {
		int size = getFeatures().size();
		for(int i=0; i<size; i++) {
			if(getFeatures().get(i).equals(f)) {
				return getFeatures().get(i);
			}
		}
		return null;
	}
	
	
	
	public CommonElement getCommonElem() {
		return commonElem;
	}

	public void setCommonElem(CommonElement commonElem) {
		this.commonElem = commonElem;
	}

	public List<Feature> getFeatures() {
		return this.features;
	}
	
	private void setFeatures() {
		this.features = new LinkedList<Feature>();
	}

	public List<CoreLabel> getTokens(){
		return this.tokens;
	}
	
	public void setTokens() {
		this.tokens = getDocument().tokens();
	}
	
	public void setRemovedStopwords(Boolean didRemove) {
		this.removedStopwords = didRemove;
	}
	
	public Boolean getRemovedStopwords() {
		return this.removedStopwords;
	}
	
	public void setDocument() {
		this.document = getPipeline().processToCoreDocument(getContent());
	}
	
	public CoreDocument getDocument() {
		return this.document;
	}
	
	public Properties getProps() {
		return this.props;
	}
	
	public void setProps() {
		this.props = new Properties();
		this.props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,regexner");
		// add additional rules, customize TokensRegexNER annotator
	    props.setProperty("ner.additional.regexner.mapping", getResourcePath()+"entities.rules");
	    props.setProperty("ner.additional.regexner.ignorecase", "true");
	    // whether or not to use NER-specific tokenization which merges tokens separated by hyphens
	    // setting this to true doesn't provide any difference in output so we don't use this
	    //props.setProperty("ner.useNERSpecificTokenization", "true");
	}
	
	public StanfordCoreNLP getPipeline() {
		return this.pipeline;
	}
	
	public void setPipeline() {
		this.pipeline = new StanfordCoreNLP(getProps());
	}

	public String getContent() {
		return content;
	}

	private void setContent() {
		Path p;
		if(fileDirectory != null)
			p = FileSystems.getDefault().getPath(fileDirectory, getFileName());
		else
			p = FileSystems.getDefault().getPath(getFileName());
		try {
			this.content = Files.readString(p, getEncoding());
		}
		catch(Exception IOException) {
			//System.err.println("Error while reading the file. Trying to read again.");
		}	
		try {
			this.content = new String(Files.readAllBytes(Paths.get(getFileName())));
		}
		catch(Exception IOException) {
			System.err.println("Error while reading the file. Check file encoding.");
		}
	}
	
	public void setContent(String newContent) {
		this.content = newContent;
	}
	
	public String toString() {
		return this.getFileName();
	}

	public String getFileName() {
		return fileName;
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Charset getEncoding() {
		return encoding;
	}

	private void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}
	
	public String getFileDirectory() {
		return fileDirectory;
	}

	private void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public void lemmatization() {
		setRemovedStopwords(true);
		removedStopwords();	
		removePunctuationTokens();
		addNERFeatures();
		nGramExtraction();
	}
	
}
