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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToNominal;

/**
 *
 * @author USER
 */
public class NewsClassifier {

    private int mode; //1 untuk naive bayes, 2 untuk dummy, 3 untuk dummy
    public Instances data;
    private Instances unLabeledData;
    public Classifier classifier;
	public FilteredClassifier fc;
	private final String namaclass;
        private String algo;
	
        //n adalah modenya
    public NewsClassifier (String s,int n) throws Exception{
        data = null;
        mode = n;
        classifier=null;
	fc = new FilteredClassifier();
	namaclass = s;
        if(mode==1){
            algo = "NaiveBayesMultiNominal";
        }
        else if(mode==2){
            algo = "NaiveBayesMultiNominal";
        }
        else if(mode==3){
            algo = "NaiveBayesMultiNominal";
        }
	File f = new File(algo+".model");
	if(!f.exists()){
		buildModel();
	}
	else{
		System.out.println("lagingeload");
		loadModel(algo+".model");
	}
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
    
    public Filter StrToWV(String sFile) throws Exception {
        StringToWordVector filter = new StringToWordVector();
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
       
        
        return filter;
        //return newData;
        //data = newData;
    }
    
    public Filter StrtoNom() throws Exception {
        StringToNominal filter = new StringToNominal();
        //NumericToNominal filter = new NumericToNominal();
        //filter.setOptions("-R 1");
        String[] opts = {"-R","last"};
        filter.setOptions(opts);
        //filter.setAttributeRange("first");
        return filter;
    }
    
    public Filter ClassAssigner() throws Exception {
        ClassAssigner filter =  new ClassAssigner();
	//filter3.setClassIndex("last");
        filter.setClassIndex("first");
		return filter;
    }
    public String CrossValidation(int n) throws Exception
    {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(fc, data, n, new Random(1));
        return eval.toSummaryString("Results",false);
	//System.out.println(eval.toMatrixString());
	//System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toClassDetailsString());
        //System.out.println(eval.toMatrixString());
    }	
    
	public void stringToARFF(String text, String full_text, String label,String output_file) throws Exception {
		FileWriter fw = new FileWriter(output_file);
		PrintWriter pw = new PrintWriter(fw);

		pw.println("@relation CrawlingQuery");
		pw.println();
		pw.println("@attribute judul string");
		pw.println("@attribute full_text string");
		pw.println("@attribute label {Pendidikan,Politik,'Hukum dan Kriminal','Sosial Budaya',Olahraga,'Teknologi dan Sains',Hiburan,'Bisnis dan Ekonomi',Kesehatan,'Bencana dan Kecelakaan'}");
		pw.println();
		pw.println("@data");
                full_text = full_text.replaceAll("[\\t\\n\\r]+"," ");
		pw.println("'" + text + "','" + full_text + "',"+label);

		pw.flush();
		pw.close();
		fw.close();
	}
	
	public List<String> classify(String input_file) throws Exception {
		
		unLabeledData = DataSource.read(input_file);
		//unLabeledData = StrtoNom(unLabeledData);
		
		unLabeledData.setClassIndex(unLabeledData.numAttributes()-1);
		Instances LabeledData = new Instances(unLabeledData);
		List<String> ls = new ArrayList<String>();

		for(int i=0; i < unLabeledData.numInstances(); ++i) {
			double clsLabel = fc.classifyInstance(unLabeledData.instance(i));
			LabeledData.instance(i).setClassValue(clsLabel);
			ls.add(LabeledData.instance(i).toString(2));
		}
//		System.out.println(LabeledData.toString());
		return ls;
	}
	
	 public void CSVtoARFF (String in,String out) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser parser = new CSVParser(new FileReader(in),format);
            
        List<String> listJudul = new ArrayList<String>();
        List<String> listFullText = new ArrayList<String>();
        List<String> listClass = new ArrayList<String>();
        for(CSVRecord record : parser) {
            listJudul.add(record.get("judul"));
            listFullText.add(record.get("full_text"));
            listClass.add(record.get(namaclass));
        }
            
        FileWriter fw = new FileWriter(out);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("@relation CrawlingQuery");
        pw.println();
        pw.println("@attribute judul string");
        pw.println("@attribute full_text string");
        pw.println("@attribute label {Pendidikan,Politik,'Hukum dan Kriminal','Sosial Budaya',Olahraga,'Teknologi dan Sains',Hiburan,'Bisnis dan Ekonomi',Kesehatan,'Bencana dan Kecelakaan'}");
        pw.println();
        pw.println("@data");
        for(int i=0; i<listJudul.size(); i++) {
        String temp = listFullText.get(i).replaceAll("[\\t\\n\\r]+"," ");
            pw.print("'"+listJudul.get(i)+"','"+temp);
            if(!listClass.get(i).equalsIgnoreCase("?")) pw.print("','");
            else pw.print("',");
            pw.print(listClass.get(i));
            if(!listClass.get(i).equalsIgnoreCase("?")) pw.print("'");
            pw.println();
        }
        pw.flush();
        pw.close();
        fw.close();
        parser.close();
    }
	
	public void save(String filename) throws Exception{
		SerializationHelper.write(filename, classifier);
	}
	
	public void load(String filename) throws Exception{
		classifier = (Classifier) SerializationHelper.read(filename);
	}
	
	public void updateFromArff(String filename) throws Exception{
		Instances dataUpdate = DataSource.read(filename);
		 Filter stn = StrtoNom();
		 stn.setInputFormat(dataUpdate);
	//	 System.out.println(nc.dataUpdate.toString());
		 dataUpdate = Filter.useFilter(dataUpdate, stn);
		// System.out.println(nc.dataUpdate.toString());
		 dataUpdate.setClassIndex(dataUpdate.numAttributes()-1);
		for(Instance i:dataUpdate){
			data.add(i);
		}
		fc.buildClassifier(data);
		saveModel(algo+".model");
	}
	
	public void buildModel() throws Exception{
		 data = LoadDB(data);
		 Filter stw = StrToWV("s.txt");
         //System.out.println(nc.data.toString());
         Filter stn = StrtoNom();
		 stn.setInputFormat(data);
	//	 System.out.println(nc.data.toString());
		 data = Filter.useFilter(data, stn);
		// System.out.println(nc.data.toString());
		 data.setClassIndex(data.numAttributes()-1);
		 
		 //Filter ca = ClassAssigner();
         //nc.ClassAssigner();
         //System.out.println(nc.data.toString());
         //RandomForest cls = new RandomForest();
			//SMO cls = new SMO();
                 if(mode==1){
                    classifier = new NaiveBayesMultinomial();
                 }
                 else if(mode==2){
                    classifier = new NaiveBayesMultinomial(); 
                 }
                 else if(mode==3){
                    classifier = new NaiveBayesMultinomial(); 
                 }
		 fc.setClassifier(classifier);
		// MultiFilter huba = new MultiFilter();
	//	 huba.setFilters(new Filter[]{stw, stn,ca});
		 fc.setFilter(stw);
         fc.buildClassifier(data);

                     saveModel(algo+".model");
	}
	
	public void saveModel(String filename) throws Exception{
		SerializationHelper.write(filename, fc);
	}
	
	public void loadModel(String filename) throws Exception{
		data = LoadDB(data);
		Filter stn = StrtoNom();
		 stn.setInputFormat(data);
	//	 System.out.println(nc.data.toString());
		 data = Filter.useFilter(data, stn);
		// System.out.println(nc.data.toString());
		 data.setClassIndex(data.numAttributes()-1);
		fc = (FilteredClassifier) SerializationHelper.read(filename);
	}
      
	public static String NaiveBayesClassification(String judul, String full_text, String ARFF) throws Exception {
		NewsClassifier nc = new NewsClassifier("class",1);
		nc.stringToARFF(judul, full_text, "?", ARFF);
		List<String> hasil = nc.classify(ARFF);		
		return hasil.get(0);
	}
        
    public static void main(String[] args) {
	try{
		NewsClassifier nc = new NewsClassifier("class",1);
		/*nc.CrossValidation(10);
		nc.stringToARFF("", "Jakarta - Dalam debat putaran keempat antara cawapres Jusuf Kalla (JK) dan Hatta Rajasa, JK menyatakan akan mengevaluasi sistem pelaksanaan UN. Pakar pendidikan Arief Rachman menilai tidak ada yang perlu diubah dari UN ataupun kurikulum pendidikan itu sendiri karena sudah memenuhi syarat." +
"" +
"\"Menurut saya nggak ada penataran guru-guru Kurikulum 2013 yang mengarah ke (aspek) kognitif, afektif dan psikomotor sudah ideal. Jangan diganti-ganti lagi. UN juga sudah benar karena UN sudah memperhitungkan kekuatan sekolah masing-masing (di tiap daerah),\" ujar Arief saat dihubungi, Senin (30/6/2014)." +
"" +
"Guru Besar di Universitas Negeri Jakarta ini menjelaskan ada alasan di balik perbedaan pandangan dengan mantan wakil presiden era Presiden SBY mengenai standar nilai UN. Arief mengaku tidak setuju dengan pendapat JK yang menginginkan syarat kelulusan UN menggunakan standar nilai mutlak." +
"" +
"\"Dulu saya rada musuhan dengan Pak JK (karena dia) membuat standar mutlak, padahal nggak boleh. Karena menguji anak itu harus dengan standar normal. Tidak benar pakai (syarat nilai) ujian mutlak. Itulah sebabnya Hatta menanyakan,\" lanjutnya." +
"" +
"Dalam debat, Minggu (29/6), JK menjelaskan, setiap tahun sistem UN mengalami pengubahan disesuaikan dengan keadaan di lapangan. Jenis soal yang awalnya hanya satu terpaksa diubah karena banyaknya siswa yang menyontek kemudian dibuat menjadi 20 jenis." +
"" +
"Menurutnya, UN tetap penting untuk melakukan pemetaan di bidang pendidikan. Apalagi Indonesia merupakan negara kepulauan yang rentan akan kesenjangan sosial jika tidak ada pemetaan." +
"" +
"\"Bagaimana kesenjangan di daerah-daerah akan kita hilangkan kalau tidak ada pemetaan, pemetaan tidak ada kalau tidak ada UN,\" tuturnya.", "Pendidikan", "update.arff");
		nc.updateFromArff("update.arff");
		nc.CrossValidation(10);*/
		/* nc.CSVtoARFF("dummy.csv", "dummy.arff");
		 System.out.println(nc.classify("dummy.arff"));*/
				 
	}
	catch(Exception e){
            e.printStackTrace();
	}
    }
}
