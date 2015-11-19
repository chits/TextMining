package com.chits.textmining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.*;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import com.google.common.io.Files;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** A simple corenlp example ripped directly from the Stanford CoreNLP website using text from wikinews. */
public class ExtractingKeywords {
	
	static List<String> stopWords=new ArrayList<>();

  public ExtractingKeywords() throws IOException { 
    File stopFile = new File( "/Users/Chitrali/Documents/Masters/SEM2/DataMining/TextMining/stop_words.txt");
    BufferedReader in=new BufferedReader(new FileReader(stopFile));
    while(in.readLine()!=null){
  	  stopWords.add(in.readLine());
    }
    in.close();
  }
  public void keywordExtract(String filePath,int fileindex,String inputPath,String outputPath){

	  // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
      Properties props = new Properties();
      props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
      props.put("ner.applyNumericClassifiers", "false");
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
      
      try 
      {
    	// read some text from the file..
          File inputFileObject = new File(inputPath + "/" + filePath);
          String text = Files.toString(inputFileObject, Charset.forName("UTF-8"));      
          // create an empty Annotation just with the given text
          Annotation document = new Annotation(text);
          
          // run all Annotators on this text
          pipeline.annotate(document);
          File fileOutput=new File("/Users/Chitrali/Documents/Masters/SEM2/DataMining/TextMining/xmlLemma/"+fileindex+".xml");
          File fileOut2=new File("/Users/Chitrali/Documents/Masters/SEM2/DataMining/TextMining/xmlLemma/"+fileindex+"lemma.txt");
          File fileKeyword=new File(outputPath+"/keyword"+fileindex+".txt");
          PrintWriter outxml= new PrintWriter(new BufferedWriter(new FileWriter(fileOutput,false))); 
          PrintStream outtxt = new PrintStream(new FileOutputStream(fileOut2,false));
          PrintWriter outkeyword=new PrintWriter(new BufferedWriter(new FileWriter(fileKeyword,false)));
          
          pipeline.xmlPrint(document, outxml);
          outxml.close();
          
          try
          {
        	  SAXParserFactory factory = SAXParserFactory.newInstance();
        	  SAXParser saxParser = factory.newSAXParser();
        	  DefaultHandler handler = new DefaultHandler() 
        	  {

		      	boolean lemma = false;
		      	public void startElement(String uri, String localName,String qName, 
		                  Attributes attributes) throws SAXException 
		      	{
		
			      	if (qName.equalsIgnoreCase("LEMMA")) 
			      	{
			      		lemma = true;
			      	}
		
		      	}
		      	public void endElement(String uri, String localName,
		      			String qName) throws SAXException 
		      	{
		
		      			
		
		      	}
		      	public void characters(char ch[], int start, int length) throws SAXException 
		      	{
		
		      		if (lemma) 
		      		{
		      			
		      			System.out.println(new String(ch, start, length));
		      			System.setOut(outtxt);
		      			lemma = false;
		      		}
		
		      	}
	      	};
	      	saxParser.parse(fileOutput, handler);

        }
        catch( Exception e)
        {
        	
        }
        finally
        {
        	outtxt.close();
        }
          BufferedReader in=new BufferedReader(new FileReader(fileOut2));
          String test = null;
          while( (test = in.readLine()) !=null)
          {
        	  if(test.matches("^[A-Za-z]+$") && !stopWords.contains(test))
        		  outkeyword.println(test);
          }
          in.close();
          outkeyword.close();
      } 
      catch (FileNotFoundException ex) 
      {
          ex.printStackTrace();
      } 
      catch (IOException ex) 
      {
          ex.printStackTrace();
      }
  }
}


