package preprocessing;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
//StanfordCoreNLP Resources
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;


/**
 *
 * @author Joshua Habif
 * 
 * LAST TESTED ON 06/27/2021 AT 3:09PM
 * 
 * The text files are assumes to have the following format "article0<#>.txt" and exactly 8 files are assumed
 * to be present in each sub-folder.
 * IMPORTANT: All resources are assumed to be located in the project's resources folder!
 * IMPORTANT: The resource folder is assumed to contain the "dataset_3" folder exactly as provided on NYU Brightspace
 * i.e., with the following structure: dataset_3/data/... with sub-folders C1, C4, and C7 with eight text files each.
 * 
 * COMMAND LINE ARGUMENT EXAMPLE: "/Users/joshuahabif/git/DescriptiveModelingClusteringTextualData/DescriptiveModelingClusteringTextualData/src/main/resources/"
 */
public class Preprocessing {
	
	private static final String[] SUBFOLDERS = {"C1", "C4", "C7"};
	// stores the number of docs in dir C1, C4, C7 respectively
	private static final int[] NUMDOC = {8,8,8};
	private static final String datasetRelativePath = "dataset_3/data/";
	private static final Charset encoding = StandardCharsets.US_ASCII;
	//Path to projects resource folder
	private static String resourcePath;
	private static Articles articles;
	private static Stopwords stopwords;
	private static Output out;
	private static CommonElement cmElement;
	private static FeatureTable fTable;
	private static Normalize tfidf;
	
	public static void main(String[] args) throws IOException {
		
		// set the path to the resources
		setPathVars(args);
		
		// initialize Output object to handle all diagnostic output
		out = new Output(resourcePath+"out.txt");
		
		// initialize CommonElement to be shared across all Article objects
		cmElement = new CommonElement();
		
		// load articles into memory
		storeArticles();
		
		// remove stopwords and apply tokenization (stemming and 
		// lemmatization) to each Article.
		// finally, extract the features using NER
		preprocessArticles();
		
		// initialize FeatureTable object to store the document
		// term matrix
		fTable = new FeatureTable(24, cmElement.getFeatures());

		// for each Article object adds the features and frequency to the matrix
		generateFMatrix();
		
		// normalize the document-term matrix
		normalize();
		
		// define the actual cluster labels by using the features
		// with greatest TFIDF score for each document folder
		tfidf.computeBestFeature(SUBFOLDERS, NUMDOC);
		
		// diagnostic
		//printArticlesAndTokens(true, true, true);
		
		// print/flush all output sent to out
		out.generateOutput();
		
		// Output a topics.txt file with the main topics
		// found for each document folder
		Output outTopics = new Output(resourcePath+"topics.txt");
		for(String s : SUBFOLDERS) {
			outTopics.write(s + "\t" + tfidf.getFolderFeature().get(s));
		}
		outTopics.generateOutput();
		
		// serialize the Normalize object so it can be used by classes in Clustering pkg
		Output sOut = new Output(resourcePath + "normalize");
		sOut.serialize(tfidf);
	}
	
	private static void setPathVars(String[] a) {
		resourcePath = a[0];
	}
	
	private static void storeArticles() {
		articles = new Articles();
		for(int i=0; i<3; i++) {
			for(int j=1; j<=8; j++) {	
				String fPath = SUBFOLDERS[i] + "/article0" + String.valueOf(j) + ".txt";
				try {
					Article curArticle = new Article(fPath, resourcePath+datasetRelativePath, resourcePath,encoding, cmElement);
					articles.addArticle(curArticle);
				} catch (IOException e) {
					System.err.println("Error while storing the file.");
					System.err.println("Did not store" + fPath + "in articles.");
				}	
			}
		}
	}
	
	private static void preprocessArticles() {
		stopwords = new Stopwords(resourcePath, encoding);	
		Iterator it = articles.getArticles().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			Article curArticle = (Article) pair.getValue();
			String key = (String) pair.getKey();
			stopwords.removeStopwords(curArticle);
		}
	}
	
	private static void generateFMatrix() {
		Iterator it = articles.getArticles().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			Article curArticle = (Article) pair.getValue();
			fTable.addRow(curArticle);
		}
	}
	
	private static void normalize() {
		// initialize Normalize obj using the String document term matrix
		tfidf = new Normalize(fTable.getMatrix());
		// store that string matrix as an int matrix 
		// so that we can perform int operations
		tfidf.transformToIntMatrix();
		// compute the number of terms needed by the TFIDF formula
		tfidf.computeNumTerms();
		// compute the document term freq needed by the TFIDF formula
		tfidf.computeTermDocumentFreq();
		// compute the TFIDF computation for each cell in the matrix
		tfidf.tfidf();
	}
		
	private static void printArticlesAndTokens(boolean printContent, 
					boolean printTokens, boolean printFeatures) throws IOException {
		Iterator it = articles.getArticles().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			Article curArticle = (Article) pair.getValue();
			String key = (String) pair.getKey();
			out.write("Key:" + key);
			if(printContent) {out.write(curArticle.getContent());}
			
			CoreDocument document = curArticle.getDocument();
			if(printTokens) {
				for(CoreLabel tok : document.tokens()) {
					out.write(String.format("%s\t%s\t%s",tok.word(), tok.lemma(), tok.ner()));
				}
				out.write("\n\nDetected Entities:");
				for(CoreEntityMention em : document.entityMentions()) {
					out.write(em.text().replaceAll("\n"," ")+"\t"+em.entityType());
				}
			}
			if(printFeatures) {
				out.write("\n\nFeatures:");
				out.write(curArticle.getFeatures().toString());
			}
			out.write("*******************************************");
		}
	}
	
	public static void printCommonElements() throws IOException {
		out.write(cmElement.toString());
	}

}
