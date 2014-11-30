/* File  : NewsClassifier.java
 * Author: Riady Sastra Kusuma, Fauzan Hilmi Ramadhian, Christ Angga Saputra
 */

package newsclassifier;

import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.filters.unsupervised.attribute.StringToNominal;

/**
 *
 * @author USER
 */
public class NewsClassifier {

    private int mode; //1 untuk naive bayes, 2 untuk tree, 3 untuk KNN, 4 untuk ANN
    private Instances data;
    private Instances unLabeledData;
    private Classifier classifier;
	
    public NewsClassifier () throws Exception{
        data = null;
        mode = 1;
        classifier=null;
    }
    
    public void LoadDB() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
	InstanceQuery query= new InstanceQuery();
	query.setDatabaseURL("jdbc:mysql://localhost:3306/news_aggregator");
	query.setUsername("root");
	query.setPassword("");
	//query.setQuery("SELECT full_text,id_kelas FROM artikel NATURAL JOIN artikel_kategori_verified");
        query.setQuery("SELECT judul,full_text,label FROM artikel NATURAL JOIN artikel_kategori_verified NATURAL JOIN kategori");
	data = query.retrieveInstances();
    }
    
    public void StrToWV(String sFile) throws Exception {
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(data);
        /*filter.setIDFTransform(false);
        filter.setTFTransform(true);
        filter.setAttributeIndices("1-2");
        //attributenameprefix
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setInvertSelection(false);
        filter.setLowerCaseTokens(true);
        filter.setMinTermFreq(1);
        //filter.setNormalizeDocLength(true);
        filter.setOutputWordCounts(false);
        //filter.setPeriodicPruning(-1);
        //filter.setStemmer(null);
        filter.setStopwords(new File(sFile));*/
        //String[] opts = weka.core.Utils.splitOptions("-tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\" \\\\r \\\\t.,;:\\\\\\'\\\\\\\"()?!1234567890 `~!@#\\\\\\%^&*[]-_+={}\\\\\\\\/|?><  \\\\r\\\\t“\\\"\"");
        String[] opts = weka.core.Utils.splitOptions("-R 1-2 -W 3000 -prune-rate -1.0 -T -N 0 -L -S -stemmer weka.core.stemmers.NullStemmer -M 1 -O -stopwords \"C:\\\\Users\\\\USER\\\\Dropbox\\\\Works\\\\IF\\\\AI\\\\Tubes 2\\\\s.txt\" -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\"   \\\\t.,;:\\\\\\'\\\\\\\"()?!1234567890 `~!@#\\\\\\%^&*[]-_+={}\\\\\\\\/|?><   \\\\t“\\\"\"");
        filter.setOptions(opts);
        //belum pake delimiter!!
        filter.setWordsToKeep(3000);
       
        
        data = Filter.useFilter(data,filter);
        //return newData;
        //data = newData;
    }
    
    public void StrtoNom() throws Exception {
        StringToNominal filter = new StringToNominal();
        //NumericToNominal filter = new NumericToNominal();
        filter.setInputFormat(data);
        //filter.setOptions("-R 1");
        String[] opts = {"-R","first"};
        filter.setOptions(opts);
        //filter.setAttributeRange("first");
        data = Filter.useFilter(data, filter);
    }
    
    public void ClassAssigner() throws Exception {
        ClassAssigner filter =  new ClassAssigner();
	filter.setInputFormat(data);
	//filter3.setClassIndex("last");
        filter.setClassIndex("first");
	data = Filter.useFilter(data, filter);
    }
    public void CrossValidation(Classifier cls,int n) throws Exception
    {
        data.setClassIndex(0);
        Evaluation eval = new Evaluation(data);
        cls.buildClassifier(data);
        eval.crossValidateModel(cls, data, n, new Random(1));
        System.out.println(eval.toSummaryString("Results",false));
        //System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toMatrixString());
    }	
    
	public void classify() throws Exception{
            unLabeledData = DataSource.read("unlabeled.arff");
            unLabeledData.setClassIndex(unLabeledData.numAttributes()-1);
            Instances LabeledData = new Instances(unLabeledData);

            for(int i=0; i < unLabeledData.numInstances();++i){
               	double clsLabel = classifier.classifyInstance(unLabeledData.instance(i));
		LabeledData.instance(i).setClassValue(clsLabel);
        	}
	//System.out.println(LabeledData.toString());
	}
	
	
	public void save(String filename) throws Exception{
		SerializationHelper.write(filename, classifier);
	}
	
	public void load(String filename) throws Exception{
		classifier = (Classifier) SerializationHelper.read(filename);
	}
        
    public static void main(String[] args) {
	try{
       	 NewsClassifier nc = new NewsClassifier();
         //nc.readData();
         nc.LoadDB();
         nc.StrToWV("s.txt");
         //System.out.println(nc.data.toString());
         nc.StrtoNom();
         //nc.ClassAssigner();
         //System.out.println(nc.data.toString());
         //RandomForest cls = new RandomForest();
         SMO cls = new SMO();
         //NaiveBayesMultinomial cls = new(NaiveBayesMultinomial);
         nc.CrossValidation(cls, 10);
	}
	catch(Exception e){
            e.printStackTrace();
	}
    }
}

