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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    
    public Instances LoadDB(Instances dataSource) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
	InstanceQuery query= new InstanceQuery();
	query.setDatabaseURL("jdbc:mysql://localhost:3306/news_aggregator");
	query.setUsername("root");
	query.setPassword("");
	//query.setQuery("SELECT full_text,id_kelas FROM artikel NATURAL JOIN artikel_kategori_verified");
        query.setQuery("SELECT judul,full_text,label FROM artikel NATURAL JOIN artikel_kategori_verified NATURAL JOIN kategori");
		return query.retrieveInstances();
    }
    
    public Instances StrToWV(Instances dataSource, String sFile) throws Exception {
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(dataSource);
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
        String[] opts = weka.core.Utils.splitOptions("-R 1-2 -W 3000 -prune-rate -1.0 -T -N 0 -L -S -stemmer weka.core.stemmers.NullStemmer -M 1 -O -stopwords \"" + sFile + "\" -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\"   \\\\t.,;:\\\\\\'\\\\\\\"()?!1234567890 `~!@#\\\\\\%^&*[]-_+={}\\\\\\\\/|?><   \\\\t“\\\"\"");
        filter.setOptions(opts);
        //belum pake delimiter!!
        filter.setWordsToKeep(10000);
       
        
        return Filter.useFilter(dataSource,filter);
        //return newData;
        //data = newData;
    }
    
    public Instances StrtoNom(Instances dataSource) throws Exception {
        StringToNominal filter = new StringToNominal();
        //NumericToNominal filter = new NumericToNominal();
        filter.setInputFormat(dataSource);
        //filter.setOptions("-R 1");
        String[] opts = {"-R","first"};
        filter.setOptions(opts);
        //filter.setAttributeRange("first");
        return Filter.useFilter(dataSource, filter);
    }
    
    public void ClassAssigner() throws Exception {
        ClassAssigner filter =  new ClassAssigner();
	filter.setInputFormat(data);
	//filter3.setClassIndex("last");
        filter.setClassIndex("first");
	data = Filter.useFilter(data, filter);
    }
    public void CrossValidation(Instances dataSource, Classifier cls,int n) throws Exception
    {
        dataSource.setClassIndex(0);
        Evaluation eval = new Evaluation(dataSource);
        cls.buildClassifier(dataSource);
		classifier = cls;
        eval.crossValidateModel(cls, dataSource, n, new Random(1));
        System.out.println(eval.toSummaryString("Results",false));
		System.out.println(eval.toMatrixString());
		System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toMatrixString());
    }	
    
	public void stringToARFF(String text, String full_text, String output_file) throws Exception {
		FileWriter fw = new FileWriter(output_file);
		PrintWriter pw = new PrintWriter(fw);

		pw.println("@relation CrawlingQuery");
		pw.println();
		pw.println("@attribute judul string");
		pw.println("@attribute full_text string");
		pw.println("@attribute label {Pendidikan,Politik,'Hukum dan Kriminal','Sosial Budaya',Olahraga,'Teknologi dan Sains',Hiburan,'Bisnis dan Ekonomi',Kesehatan,'Bencana dan Kecelakaan'}");
		pw.println();
		pw.println("@data");
		pw.println("'" + text + "','" + full_text + "',?");

		pw.flush();
		pw.close();
		fw.close();
	}
	
	public String classify(Instances dataSource, String input_file) throws Exception {
		unLabeledData = DataSource.read(input_file);
		unLabeledData = StrToWV(unLabeledData, "s.txt");
		//unLabeledData = StrtoNom(unLabeledData);
		System.out.println(unLabeledData.toString());
		unLabeledData.setClassIndex(0);
		System.out.println(unLabeledData.toString());
		Instances LabeledData = new Instances(unLabeledData);
		

		for(int i=0; i < unLabeledData.numInstances(); ++i) {
			double clsLabel = classifier.classifyInstance(unLabeledData.instance(i));
			LabeledData.instance(i).setClassValue(clsLabel);
			System.out.println();
		}
		
		return LabeledData.instance(0).toString(0);
	}
	
	public void save(String filename) throws Exception{
		SerializationHelper.write(filename, classifier);
	}
	
	public void load(String filename) throws Exception{
		classifier = (Classifier) SerializationHelper.read(filename);
	}
        
    public static void main(String[] args) {
	try{
		Instances dataSource = null;
		NewsClassifier nc = new NewsClassifier();
         //nc.readData();
        dataSource = nc.LoadDB(dataSource);
         dataSource = nc.StrToWV(dataSource, "s.txt");
         //System.out.println(nc.data.toString());
         dataSource = nc.StrtoNom(dataSource);
         //nc.ClassAssigner();
         //System.out.println(nc.data.toString());
         //RandomForest cls = new RandomForest();
			//SMO cls = new SMO();
         NaiveBayesMultinomial cls = new NaiveBayesMultinomial();
         nc.CrossValidation(dataSource, cls, 10);
		 
		 nc.stringToARFF("", "REPUBLIKA.CO.ID, JAKARTA -- Microsoft mengumumkan bakal menjual konsol game terbarunya, Xbox One dengan harga resmi 499 dolar Amerika Serikat atau sekitar Rp 4,9 jutaan. " +
"Seperti dilansir The Verge, Selasa (11/6), Xbox One akan resmi disebar ke 21 pasar di seluruh dunia pada November 2013." +
"Dengan harga segitu, pengguna akan mendapatkan konsol Xbox One, gamepad wireless, Kinect terbaru, dan 14 hari masa uji coba Xbox Live Gold." +
"Konsol Xbox One dibekali CPU 8 core dan GPU SoC serta HDD 500GB dengan RAM 8GB, SoC (system on a chip). Konsol ini juga bisa digunakan untuk memutar kepingan Blu-ray.", "crawling.ARFF");
		 
		 System.out.println(nc.classify(dataSource, "crawling.ARFF"));
	}
	catch(Exception e){
            e.printStackTrace();
	}
    }
}
