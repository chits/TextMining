package com.chits.textmining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class DriverClass {
	static final String inputPath = "/Users/Chitrali/Documents/Masters/SEM2/DataMining/TextMining/Input";
    static final String outputPath = "/Users/Chitrali/Documents/Masters/SEM2/DataMining/TextMining/Keywords";
    static BufferedReader in;
    static PrintWriter out;
    static List<String> bestWords50=new ArrayList<>();
    static Map<String,Set<String>> dataSet = new HashMap<String,Set<String>>();
    static int supportCount;
    static float minConfidence;
   
    

	public static void main(String[] args) throws IOException{
		
		Scanner userInput = new Scanner(System.in);
		System.out.println("********************************************\n");
		System.out.println("************TEXT MINING SYSTEM**************\n\n");
		System.out.println("Data Set Used: Reuters 21578 Collection\n"+
		"Algorithms Used:\n1. Text mining algorithm : for keyword association mining\n"+
				"2. Tf-idf algorithm : for identifying and extracting keywords\n"+
		"3. Apriori algorithm : for finding association rules\n");
		System.out.println("\nEnter the Minimum Support Count value (In Integer):");
		supportCount = userInput.nextInt();
		System.out.println("\nEnter the minimum confidence value (In Decimal):");
		minConfidence = userInput.nextFloat();
		
		System.out.println("\n*************************************************");
		System.out.println("***************User Entered Values***************");
		System.out.println("\nMinimum SupportCount:" + supportCount + " \nMinimum Confidence:" + minConfidence);

		
		ExtractingKeywords keyword=new ExtractingKeywords();
		
		 File[] files=new File(inputPath).listFiles();
		    int fileIndex=0;
		    for (File file : files) 
		    {
		        if (file.isFile() && file.getName().contains(".txt")) ;
		        {
		           keyword.keywordExtract(file.getName(), fileIndex,inputPath,outputPath);
		           fileIndex++;
		        }
		    }  
		   TfIdf tf=new TfIdf(outputPath);
		   tf.buildAllDocuments();
		   File allwordFile = new File(outputPath + "/allwords.txt");
		   File top50 = new File(outputPath + "/bestwords.txt");
		   Map<String, Double> map = new HashMap<String, Double>();
		  in=new BufferedReader(new FileReader(allwordFile));
	       String test = null;
	       while( (test = in.readLine()) !=null)
	       {
	    	   String[] obj = test.split(" ");
	    	   map.put(obj[0], Double.parseDouble(obj[1]));
	       }
	       in.close();
	       Set<Entry<String, Double>> set = map.entrySet();
	       List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
	        Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
	        {
	            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );
	       out=new PrintWriter(new BufferedWriter(new FileWriter(top50,false)));
	        int i = 0;
	        for(Map.Entry<String, Double> entry:list){
	        	if(i < 100)
	        	{
	        		out.println(entry.getKey());
	        	}
	        	else
	        		break;
	        	i++;
	        }
	        out.close();
	        in=new BufferedReader(new FileReader(top50));
		    while(in.readLine()!=null){
		  	  bestWords50.add(in.readLine());
		    }
		    in.close();
		    
		    File[] keyFiles=new File(outputPath).listFiles();
		    int filenumber=0;
		    for (File file : keyFiles) 
		    {
		        if (file.isFile()&& file.getName().contains("keyword")) 
		        {Set<String> words=new HashSet<String>();
		        	in=new BufferedReader(new FileReader(file));
		            String line = null;
		            while( (line = in.readLine()) !=null)
		            {
		          	  if(bestWords50.contains(line)){
		          		  words.add(line);
		          	  }
		          		
		            }
		            dataSet.put(file.getName(), words);
		            in.close();
		           filenumber++;
		        }
		    } 
		    File dataset = new File(outputPath + "/dataset.txt");
		    out=new PrintWriter(new BufferedWriter(new FileWriter(dataset,false)));
		    for(String s:dataSet.keySet()){
		    	if(!dataSet.get(s).isEmpty())
		    	out.println(s+" "+dataSet.get(s));
		    }
		    out.close();
		    
		    Apriori apr = new Apriori(dataSet,supportCount,minConfidence);
		    apr.InitProcess();
		    
	}
}
