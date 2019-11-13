package SENTIMENT_STUDY.SENTIMENT_STUDY;


import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class StanfordNLP {
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{

		  private final static IntWritable one = new IntWritable(1);
		  private Text word = new Text();

		 public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String data = null;
	       // int x;
	    	try {
	    	BufferedReader in= new BufferedReader(new FileReader("InputData.txt"));
	    	String line=null;
	    	
	    	while((line=in.readLine())!=null) {
	    		//System.out.println(line);
	    		
	    		  //getStanfordSentimentRate(line);
	    		 Properties props = new Properties();
	 	        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
	 	        
	 	        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	 	        //StanfordCoreNLP
	 	        int totalRate = 0;
	 	        String[] linesArr = line.split("\\.");
	 	        for (int i = 0; i < linesArr.length; i++) {
	 	            if (linesArr[i] != null) {
	 	                Annotation annotation = pipeline.process(linesArr[i]);
	 	                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
	 	                    Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
	 	                    int score = RNNCoreAnnotations.getPredictedClass(tree);
	 	                    totalRate = totalRate + (score - 2);
	 	                    //IntWritable TotalR=new IntWritable();
	 	                    //TotalR.set(totalRate);
	 	                    String emotion=null;
	 	                    if(totalRate==1) {
	 	                    	emotion="HAPPY";
	 	                    }
	 	                    else if(totalRate==-1) {
	 	                    	emotion="SADDNESS/NEUTRAL/HATE";   //  neural
	 	                    }
	 	                    else if(totalRate==0) {
	 	                    	emotion="SURPRISE/LOVE";  //
	 	                    }
	 	                   else if(totalRate==-2) {
	 	                    	emotion="DISGUST";
	 	                    }
	 	                    
	 	                  else if(totalRate==2) {
	 	                    	emotion="SURPRISE";
	 	                    }
	 	                    
	 	                 else if(totalRate==-3) {
	 	                    	emotion="TRUST";
	 	                    }
	 	                    
	 	                else if(totalRate==3) {
	                    	emotion="ANTICIPATION";
	                    }
	 	                else {
	 	                	emotion= "NO EMOTION";
	 	                }
	 	                    

	 	                    
	 	                    //System.out.println(totalRate);
	 	                    data= line+"     "+ emotion; //KEEP ONLY LINE
	 	                    System.out.println(data);
	 	                    
	 	                  context.write(new Text(data),new IntWritable(1)); //Put TotalR
	 	              //  String number= Integer.toString(totalRate);
	 	               // String data1=null;
	 	                //data1=line+"   "+number;
	 	                  // context.write(new Text(data1),new IntWritable(1));

	 	                }
	 	                
	 	                
	 	            }
	 	           //context.write(new Text(data),new IntWritable(1));
	 		    	
	 	        }
	 	        
 	
	    	}
	    	}
	    	catch(IOException e){
	    		System.out.println("File is not read properly");
	    	}
	    	
	    	
	       // context.write(new Text(data),new IntWritable(1));
	 
 
 
 }
 
 }

 public static class IntSumReducer
 extends Reducer<Text,IntWritable,Text,IntWritable> { //PUT TEXT INSTEAD OF LAST INTWRITABLE
 private IntWritable result = new IntWritable();

 public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
 int sum = 0;
 //int sum1=0;
 for (IntWritable val : values) {
 sum += val.get();
 }
 
 /*for(IntWritable val :values) {
	 sum1+=val.get();
	 
 }*/
 
 
 result.set(sum-sum);
 context.write(key, result); //PUT new Text(emotion) INSTEAD OF REUSLT
 }
 }

 public static void main(String[] args) throws Exception {
	 
	 
	 Properties props = System.getProperties();
     props.setProperty("mail.store.protocol", "imaps");
     try {
             Session session = Session.getDefaultInstance(props, null);
             Store store = session.getStore("imaps");
             store.connect("imap.gmail.com", " manuchaudhary.ei@gmail.com", "sarikamanu");      //Enter here your email id and password
             System.out.println(store);
             Folder inbox = store.getFolder("Inbox");
             inbox.open(Folder.READ_ONLY);
             
             BufferedReader optionReader = new BufferedReader(new InputStreamReader(System.in));
           
             try {
             
             	ConfigurationBuilder cb = new ConfigurationBuilder();
             	cb.setDebugEnabled(true)
             	.setOAuthConsumerKey("yq6jK8NBREb5xPF9EamoiFXOJ")
             	.setOAuthConsumerSecret("bvJi7KOLBupMcESTz23st8PhT9db3bnRVQCTGjckNUNnl58iJk")
             	.setOAuthAccessToken("807767634297098240-5GsHtpG4fwr1nr0wyoHLI53A0JsGtLT")
             	.setOAuthAccessTokenSecret("TPMelxAhTJ0Af1pR3B96GMpV319FgaNWQTrg8Vi64DtU8");
             	TwitterFactory tf = new TwitterFactory(cb.build());
             	Twitter twitter1 = tf.getInstance();
             	List<Status> statuses;
             	String user;
             		if (args.length == 1) {
             			user = args[0];
             			statuses = twitter1.getUserTimeline("ATT");
             		} else {
             			user = twitter1.verifyCredentials().getScreenName();
             			statuses = twitter1.getUserTimeline();
             		}
             		//System.out.println("Showing @" + user + "'s user timeline.");
             		for (Status status : statuses) {
             			System.out.println(status.getText());
             		}
             }catch(Exception e) {
            	 System.out.println("Couldnot connect Tweeter");
             }


            
             try {
            
            showUnreadMails(inbox);
                
                 optionReader.close();
             } catch (IOException e) {
                 System.out.println(e);
             }
             
     }  catch (MessagingException e) {
         System.out.println(e.toString());
         System.exit(2);
     }
 /*    @SuppressWarnings("unused")
	Twitter twitter = new TwitterFactory().getInstance();
          try {
          ConfigurationBuilder cb = new ConfigurationBuilder();
          cb.setDebugEnabled(true)
            .setOAuthConsumerKey("yq6jK8NBREb5xPF9EamoiFXOJ")
            .setOAuthConsumerSecret("bvJi7KOLBupMcESTz23st8PhT9db3bnRVQCTGjckNUNnl58iJk")
            .setOAuthAccessToken("807767634297098240-5GsHtpG4fwr1nr0wyoHLI53A0JsGtLT")
            .setOAuthAccessTokenSecret("TPMelxAhTJ0Af1pR3B96GMpV319FgaNWQTrg8Vi64DtU8");
          TwitterFactory tf = new TwitterFactory(cb.build());
          Twitter twitter1 = tf.getInstance();
          List<Status> statuses;
          String user;
          if (args.length == 1) {
              user = args[0];
              statuses = twitter1.getUserTimeline("ATT");
          } else {
              user = twitter1.verifyCredentials().getScreenName();
              statuses = twitter1.getUserTimeline();
          }
       
          for (Status status : statuses) {
        //      System.out.println(status.getText());
          }
      } catch (TwitterException te) {
          te.printStackTrace();
          System.out.println("Failed to get timeline: " + te.getMessage());
          System.exit(-1);
      }
      */


 Configuration conf = new Configuration();
 //DistributedCache.addFileToClassPath(new Path(args[0]) , conf);
	
 Job job = Job.getInstance(conf, "word count");
 
 job.setJarByClass(StanfordNLP.class);
 job.setMapperClass(TokenizerMapper.class);
 job.setCombinerClass(IntSumReducer.class);
 job.setReducerClass(IntSumReducer.class);
 job.setOutputKeyClass(Text.class);
 job.setOutputValueClass(IntWritable.class);
 
 FileInputFormat.addInputPath(job, new Path(args[0]));
 FileOutputFormat.setOutputPath(job, new Path(args[1]));
 System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
 
 
 static public int showUnreadMails(Folder inbox) throws IOException{    
	 
     try {
    	 BufferedWriter writer = null;
 		 writer = new BufferedWriter(new FileWriter("InputData.txt"));
       //  writer.write("Hello world!");

         FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
         Message msg[] = inbox.search(ft);
         for(int i=0;i<10;i++) {
             try {
             	
                 writer.write(msg[i].getSubject().toString());
                 writer.newLine();

             	}
             	
              catch (Exception e) {
                 // TODO Auto-generated catch block
                 System.out.println("No Information");
             }
            

     }
         	 writer.close();

        
     } catch (MessagingException e) {
         System.out.println(e.toString());
     }
        
     return 0;
 }
 
 
 

 
 
}