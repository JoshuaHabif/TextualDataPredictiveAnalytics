
# Descriptive Modeling, Clustering, and Classification of Textual Data

  

## About the Author

My name is Joshua Habif and I'm a senior at New York University (CAS) pursuing an undergraduate degree in Mathematics and Computer Science. This project was developed as a part of a Predictive Analytics course I took in Summer 2021 with Professor Anasse Bari who introduced me to the exciting world of Data Science.

  

Feel free to contact me at jh5653 (at) nyu (dot) edu.

  

## Introduction

In this project I pre-process a collection of news articles using Natural Language Processing, group similar articles together using my own implementation of the popular K-Means++ algorithm, predict each article's topic based on the unsupervised learning results, and use the predicted labels to classify new articles using the K-Nearest-Neighbors algorithm. I provide an interactive visualization of the clusters, and performance analysis of the classification.

  

The aim of this project is three-fold:

  

### Pre-Processing

I wish to transform text articles into vectors whose dimensions represent self-contained concepts. Each concept can be thought of as an article property. For example, when reading an article about, say, commercial Airlines I observe industry specific terminology and jargon such as "airline safety," "pilot," and "engine failure." Naturally, these terms are interpreted and understood by a reader in the context of the airline industry. As an illustration, the term "engine failure" can be used to denote an automobile engine failure in one context, and airplane engine failure in another context. This presents a problem for computers which don't naturally *understand* meaning within context, partly because we have yet formalized context. With this in mind, the main goal of the first part of the project is to structure natural text into vectors whose dimensions represent self-contained ideas and entries represent the *strength* of each concept.

  

To this end, I scan each article. This scan reveals that articles about, say, mortgage rates, mention some variant of the word pair "mortgage" and "rates." Sometimes the pair appears as "mortgage rate," and other times it appears as "mortgage rate**s**." I conclude that these pairs are equivalent in the sense that they both signal that this article is discussing mortgage rates, so I apply stemming and lemmatization to be able to systematically identify these pairs as the same. My scan also reveals that some word permutations that are indicative of an article's topic aren't amenable to this kind of systematic analysis, so I use Name Entity Extraction to address these concepts. Finally, I search the tokenized text for n-grams.

  

Natural text includes words that aren't indicative of the text's topic such as stop-words, which include conjunctions, pronouns, and other parts of speech which generally *glue* together dependent clauses. Therefore, I remove those along with punctuation tokens.

  

At this stage, I've converted text documents into vectors that represent concepts and an associated strength. This strength is simply a count of how many times a concept appears in the document. Some concepts are quite strong in all vectors so they don't contribute to my clustering efforts, and documents of varying lengths have varying strengths of repetition. Therefore, I apply Term-Frequency-Inverse-Term-Frequency to normalize the strengths of the concepts and lower the strength of concepts that occur across many documents. The strength of the concept is lowered in proportion to the number of documents that contain it -- the more documents contain a concept, the larger its decrease in strength.

  

Having a 'normalized' set of vectors, I proceeded to cluster them.

  

### Clustering

I implemented the K-Means algorithm to cluster similar documents. My algorithm converges quickly due to a conscious choice of initial centroids. Namely, I take the set of vectors generated in the previous part, add them together, and divide by the size of that set to obtain the *average vector*. The first centroid is then chosen to be the vector whose minimum distance to the *average vector* is maximized. I choose centroid k-1 by finding the vector whose minimum distance from centroid k is maximized. Now that I have two centroids, I compute the distance between every vector in the set to the two centroids. Centroid k-2 is chosen to be the vector whose minimum distance to centroid k and k-1 is maximized. This process continues until k initial centroids are obtained.

  

Now that K-Means clustering has been performed, I predict each document's topic by choosing the concept whose strength is largest across all documents in a cluster. I construct a confusion matrix using my knowledge of what the topics are, and provide the accuracy, precision, recall, and F-measure.

  

I run clustering with a Euclidean and Cosine distance metric and conclude that Cosine performs better (F-score of 1 vs. 0.45). To visualize these results, I perform Principal Component Analysis and visualize the results using Plotly. For each metric, I provide two scatter plots where points represent documents and their color represent their topic. I also provide a confusion matrix, and scores for each metric.


### Euclidean

Model Precision: 0.30
Model Recall: 0.87
Model F-Score: 0.45

![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/clustering_sample_output/clusters_euclidean_original.png?raw=true)

![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/clustering_sample_output/clusters_euclidean.png?raw=true)

(Column labels represnet the predicted class, whereas rows represnt the actual class)
|  | Disease | Finance | Aeronautics Organization |
|--------------------------|---------|---------|--------------------------|
| **Disease** | 0 | 0 | 8  |
| **Finance** | 0 | 0 | 8  |
| **Aeronautics Organization** | 1 | 0 | 7  |

#### Cosine

Model Precision: 1.0
Model Recall: 1.0
Model F-Score: 1.0

![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/clustering_sample_output/clusters_cosine_original.png?raw=true)

![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/clustering_sample_output/clusters_cosine.png?raw=true)

(Column labels represnet the predicted class, whereas rows represnt the actual class)
|  | Disease | Finance | Aeronautics Organization |
|--------------------------|---------|---------|--------------------------|
| **Disease**  | 8 | 0 | 0  |
| **Finance**  | 0 | 8 | 0  |
| **Aeronautics Organization** | 0 | 0 | 8  |

  

  

  

### Classification

Equipped with the clusters and their predicted label from the previous part, I wish to determine the topic of a new article. To this end, I apply the same pre-processing procedure presented in part one to the new document, with a slight modification: instead of including the Inverse-Term-Frequency component in the computation, I retain the Term-Frequency component. The resulting vector represents concepts and their associated strength, normalized by the document size.

  

I then apply the K-Nearest-Neighbors algorithm to determine the k most similar documents to the new document, and return a topic determined by the topic of the majority its the K-Nearest-Neighbors.

  

Sometimes a new document might be, say, x% of class A, y% of class B, z% of class C, etc. Therefore, we also include the option run *Fuzzy*-KNN which returns  labels along with their associated percentage instead of a single label.

  

  

Finally, I use the class labels to produce a confusion matrix, and provide the accuracy, precision, and recall of  classification model.

  

To optimize these performance measures, I run the algorithm for every value of k (since 23 is small) and obtain the following results:

  

| K  | Precision | Recall | Accuracy |
|----|-----------|--------|----------|
| **1**  | 1.00  | 1.00 | 1.00 |
| **2**  | 1.00  | 1.00 | 1.00 |
| **3**  | 1.00  | 1.00 | 1.00 |
| 4  | 0.92  | 0.93 | 0.90 |
| **5**  | 1.00  | 1.00 | 1.00 |
| 6  | 0.89  | 0.93 | 0.90 |
| 7  | 0.89  | 0.93 | 0.90 |
| 8  | 0.89  | 0.93 | 0.90 |
| 9  | 0.89  | 0.93 | 0.90 |
| **10** | 1.00  | 1.00 | 1.00 |
| **11** | 1.00  | 1.00 | 1.00 |
| 12 | 0.89  | 0.93 | 0.90 |
| 13 | 0.89  | 0.93 | 0.90 |
| 14 | 0.89  | 0.93 | 0.90 |
| **15** | 1.00  | 1.00 | 1.00 |
| 16 | 0.89  | 0.93 | 0.90 |
| **17** | 1.00  | 1.00 | 1.00 |
| **18** | 1.00  | 1.00 | 1.00 |
| 19 | 0.81  | 0.87 | 0.80 |
| 20 | 0.75  | 0.76 | 0.70 |
| 21 | 0.81  | 1.00 | 0.70 |
| 22 | 0.44  | 0.80 | 0.40 |
| 23 | 0.44  | 0.80 | 0.40 |

  

Evidently, when k is 1, 2, 3, 5, 10, 11, 15, 17, or 18 the algorithm classifies the new documents correctly. Due to the small size of the testing (=10) and training set (=24) I guess that lower values of k should be chosen, and decide that an appropriate value of k is **5**.

  

For k=5, I obtain the following confusion matrix, which of course is diagonal. 

  
(Column labels represnet the predicted class, whereas rows represnt the actual class)

|  | C1 | C4 | C7 |
|----|----|----|----|
| **C1** | 5  | 0  | 0  |
| **C4** | 0  | 3  | 0  |
| **C7** | 0  | 0  | 2  |


  

Where C1 stands for Airline Safety, C4 for Hoof and Mouth Disease, and C7 for Mortgage Rates.

  

The performance metrics along with the console output produced at runtime are provided below:

  

  

```console

SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

Predicted Class Label of document unknown/unknown01.txt is: AERONAUTICS_ORGANIZATION
Predicted Class Label of document unknown/unknown03.txt is: AERONAUTICS_ORGANIZATION
Predicted Class Label of document unknown/unknown05.txt is: DISEASE
Predicted Class Label of document unknown/unknown07.txt is: FINANCE
Predicted Class Label of document unknown/unknown09.txt is: DISEASE
Predicted Class Label of document unknown/unknown02.txt is: AERONAUTICS_ORGANIZATION
Predicted Class Label of document unknown/unknown04.txt is: AERONAUTICS_ORGANIZATION
Predicted Class Label of document unknown/unknown06.txt is: DISEASE
Predicted Class Label of document unknown/unknown08.txt is: FINANCE
Predicted Class Label of document unknown/unknown10.txt is: AERONAUTICS_ORGANIZATION

**********************************

Program Parameters:
k = 5
Metric = Cosine
isFuzzy = false

Program Performance:
Recall: 1.00
Precision: 1.00
Accuracy: 1.00

**********************************

```

For Fuzzy-KNN, I don't define performance analysis:

```console
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

Predicted Class Label of document unknown/unknown01.txt is: 100.00% about AERONAUTICS_ORGANIZATION;
Predicted Class Label of document unknown/unknown03.txt is: 40.00% about AERONAUTICS_ORGANIZATION; 20.00% about FINANCE; 40.00% about DISEASE;
Predicted Class Label of document unknown/unknown05.txt is: 100.00% about DISEASE;
Predicted Class Label of document unknown/unknown07.txt is: 100.00% about FINANCE;
Predicted Class Label of document unknown/unknown09.txt is: 100.00% about DISEASE;
Predicted Class Label of document unknown/unknown02.txt is: 100.00% about AERONAUTICS_ORGANIZATION;
Predicted Class Label of document unknown/unknown04.txt is: 100.00% about AERONAUTICS_ORGANIZATION;
Predicted Class Label of document unknown/unknown06.txt is: 100.00% about DISEASE;
Predicted Class Label of document unknown/unknown08.txt is: 100.00% about FINANCE;
Predicted Class Label of document unknown/unknown10.txt is: 80.00% about AERONAUTICS_ORGANIZATION; 20.00% about DISEASE;

```

  

## Definitions

I use the following definitions:


- Precision = fraction of instances where the model correctly declared i out of all the instances where it declared i. I take the mean of all precisions to get a final precision value.

  

- Recall = fraction of instances where the model correctly declared i out of all of the cases where the true state of the instance is i.

  

## Visualization

  

For visualization I use PCA on the TFIDF matrix to project the articles onto the three principal componenets.

I use Python's Plotly library to visualize the results and export the interactive graphs as HTML files to the resources folder. 

  
If you'd like to visualize the performance of the program, run Main.java in clustering and use the resultant file pca_tfidf.csv and use the Python file plot.py to generate two HTML files representing the original clusters and the programs' clusters.

  

The plots are interactive HTML scatter plots -- open them using any popular browser.

  

## Dependencies

  

This program uses two main libraries which are specified in the pom.xml file.

  

- Stanford Core NLP (version 4.2.1; models-english and models-english-kbp): This library is used to tokenize the text in the preprocessing package and is listed as a dependency in pom.xml.

  

- Apache Commons Math (version 3.6.1): This library is used to perform the PCA in the clustering package and is listed as a dependency in pom.xml.

  

  

## Running this Program:

  

1) Open up Eclipse.

  

2) From the top bar menu choose File -> Import -> Maven -> Existing Maven Projects -> Next -> Browse (choose the project's directory: ".../Deliverables/DescriptiveModelingClusteringTextualData") -> Finish.



3) Maven should now be building the project according to the dependencies listed on pom.xml. Wait until this process finishes.

  

4) You should now see "hw-one" in the package explorer window. Select this project and right click select "Run As" -> "Run Configurations". Under "Main": enter the Project name "hw-one" and the Main class "preprocessing.Preprocessing". Under Arguments enter the Program arguments: the program argument for preprocessing.Preprocessing is the resources path. e.g., "/home/joshua/Downloads/DescriptiveModelingClusteringTextualData/src/main/resources/".

    ![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/Project%20Setup%20Screenshots/step_4_1.png?raw=true)
      ![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/Project%20Setup%20Screenshots/step_4_2.png?raw=true)

  

5) In Run Configurations select Run. The program will generate a file called "normalize" and save it to the Resources directory you provided in step 4. You should also see a message printed to the console notifying you that normalize was saved to the Resources directory.

    ![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/Project%20Setup%20Screenshots/step_5.png?raw=true)

6) I now run the second part of the program: Select "hw-one" in the Package Explorer, right click select "Run As" -> "Run Configurations": Under Main the Project name is the same ("hw-one") and the Main class is "clustering.Main". Under Arguments the program's argument is the same Resources folder you provided for preprocessing.Preprocessing. Click "Run".

    ![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/Project%20Setup%20Screenshots/step_6_1.png?raw=true)
  ![alt text](https://raw.githubusercontent.com/JoshuaHabif/TextualDataPredictiveAnalytics/main/src/main/resources/Project%20Setup%20Screenshots/step_6_2.png?raw=true)
7) You should now see the F-Score printed to the console along with the output files saved in Resources.

  

* clustering.Main generated a confusion_matrix.txt file where you can find the confusion matrix and the Precision, Recall, and F-Score of the model.

  

* clustering.Main generated pca_tfidf.csv which was used to produce the scatter plots provided using Plotly and Python. To reproduce these plots run the plot.py script using the path to pca_tfidf.csv.

* clustering.Main generated clusters which are a serialized version of the clusters that are used by the classifying package.

  

* preprocessing.Preprocessing generated topics.txt which is a list of folder topics computed according to each folders highest feature's TFIDF score.

  

8) Now I can run the third part of the program which classifies new documents. Right click the project, and select run configurations. Under run configurations, specify "classifying.Main" is the main class, and provide the "Resources" folder path as a command line argument. Click "Run." You should now see the results printed to console, along with performance metrics. A confusion matrix has also been saved to the resources folder. If you wish to change the parameters, and run Fuzzy KNN, go to classifying.Main and change the final static class variables.

  

## Input Format and Required Resources

### Requirements for the Pre-Processing  part of the program:

The articles are assumed to be saved in .txt format in three directories C1, C4, and C7. Each directory contains eight articles with the following naming convention "article0#.txt." e.g., "article08.txt" is saved in directory C1.

  

In addition, the three directories are assumed to be saved in the following directory ".../src/main/resources/dataset_3/data/".

  

For the program to work the user must also make sure that the following resources are included in ".../src/main/resources":

  

- "stop_words.txt": this file includes a list of common stopwords and is used by the preprocessing package to filter and extract stopwords from the articles.

  

- "basic_ner": this file includes a list of NER entities that are identified by StanfordCoreNLP. I manually compiled this list.

  

- "basic_ner.rules": this file contains additional NER rules used by StanfordCoreNLP to correctly identify entities in the texts.

  

### Requirements for the Classification part of the program

The articles to be classified are assumed to be in .txt format, and my program supports common encoding types. The files are assumed to be saved in the resources folder -- ".../src/main/resources/unknown/" -- in the following format: "unknown01.txt".

  

A text file called "documents.txt" is assumed to be present in the resources folder, ".../src/main/resources/documents.txt". This file contains a list of relative paths of the text files to be classified.
