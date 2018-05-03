/**
 * 
 */
package sherlock.model.analysis.detection;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.io.IOException;	
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sherlock.model.analysis.SettingProfile;

class NGramsStrategy implements DetectionStrategy {

	NGramsStrategy(File[] filesToCompare, SettingProfile sp){
		doDetection(filesToCompare, sp);
	}
	
	@Override
	public void doDetection(File[] filesToCompare, SettingProfile sp) {
		System.out.println("Detection Strategy: \t Samelines Detection");
		String outputDir = sp.getOutputDir();
		String description = sp.getDescription();
		
		String parent = filesToCompare[0].getParentFile().getParentFile().getParent();
		System.out.println("------Trying to make a report directory " + parent);
		String targetDirectory = parent+"\\Report\\" ;
		System.out.println("------Target directory " + targetDirectory);
		File target = new File (targetDirectory);
		if ( target.exists() && target.isDirectory() ) {
			System.out.println("The target exists");
		} else {
			target.mkdir();
		}
		targetDirectory = parent+"\\Report\\" +description;
		
		target = new File (targetDirectory);
		if ( target.exists() && target.isDirectory() ) {
			System.out.println("The target exists");
		} else {
			target.mkdir();
		}
		
		for (int i = 0; i < filesToCompare.length ; i++ ) {
			for (int j = i+1; j < filesToCompare.length ; j++ ) {
				
				ArrayList<Tuple<Ngram, Ngram>> matches = findMatches(filesToCompare[i], filesToCompare[j], 10, 2);
				String name1 = filesToCompare[i].getName();
				name1 = name1.replaceAll(" ", "_");
				name1 = name1.replaceAll(".java", "");
				String name2 = filesToCompare[j].getName();
				name2 = name2.replaceAll(" ", "_");
				name2 = name2.replaceAll(".java", "");
				File f = new File(targetDirectory+"\\"+name1+"__"+name2+".txt");
				System.out.println("*********" + targetDirectory+"\\"+name1+"__"+name2+".txt");
				try {
					f.createNewFile();
					System.out.println("In NGgramStrategy: File was created");
					
				} catch (IOException e) {
					System.out.println("In NGgramStrategy: File already exists");
				}
				writeToFile(f, matches, name1, name2);
				
				
			}
		}	
	}
	private void writeToFile(File f, ArrayList<Tuple<Ngram, Ngram>> list, String name1, String name2) {
		try {
			String intro = "Similarities between: " + name1 + " and " + name2;
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(intro);
			writer.newLine();
			writer.newLine();
			for (int i = 0; i < list.size(); i ++) {
				writer.write("File1");
				writer.newLine();
				String ngram1 = list.get(i).getKey().toString();
				writer.write(ngram1);
				writer.newLine();
				writer.write("File2");
				writer.newLine();
				String ngram2 = list.get(i).getValue().toString();
				writer.write(ngram2);
				writer.newLine();
				writer.newLine();
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println("FAILED TO WRITE TO FILE");
		}
	}
	private ArrayList<Tuple<Ngram, Ngram>> findMatches (File f1, File f2, int nSize, int anomalies){
			String s1 = readFile(f1.getAbsolutePath(), Charset.defaultCharset());
			String s2 = readFile(f2.getAbsolutePath(), Charset.defaultCharset());
			String file1Name = f1.getName();
			String file2Name = f2.getName();
			ArrayList<Tuple> l = generateList(s1);
			ArrayList<Tuple> l2 = generateList(s2);
			ArrayList<Ngram> n = generateNgram(l, nSize);
			ArrayList<Ngram> n2 = generateNgram(l2, nSize);
			ArrayList<Tuple<Ngram, Ngram>> matches = compareList(n, n2, anomalies);
			return matches;
		}	
	private String readFile(String path, Charset encoding){
			byte[] encoded = new byte[1];
			try{
				encoded = Files.readAllBytes(Paths.get(path));
			}catch (Exception e){
				System.out.println("File not found : " + e);
			}
			return new String(encoded, encoding);
		}
	
	private ArrayList<Tuple> generateList (String s){
			ArrayList<Tuple> fileBreakdown = new ArrayList<Tuple>();
			int currentLine = 1;
			String[] wordList = s.replace("\n", " \n ").split("[ \t]+");
			for (int i = 0; i < wordList.length; i++){
				String temp = wordList[i];
				if (wordList[i].contains("\n")){
					currentLine += numOfEOL(wordList[i]);
					continue;
				}
				Tuple<String, Integer> item = new Tuple<String, Integer>(temp, currentLine);
				fileBreakdown.add(item);
				
			}
			return fileBreakdown;
			
		}
		public static int numOfEOL (String s) {
			return (s.length()-s.replace("\n", "").length());
		}
		public static String unEscapeString(String s){
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<s.length(); i++)
				switch (s.charAt(i)){
					case '\n': sb.append("\\n"); break;
					case '\t': sb.append("\\t"); break;
					// ... rest of escape characters
					default: sb.append(s.charAt(i));
				}
			return sb.toString();
		}
		
		public static ArrayList<Ngram> generateNgram(ArrayList<Tuple> wordLineTup, int n){
			ArrayList<Ngram> ngramList = new ArrayList<Ngram>();
			for (int i = 0; i < (wordLineTup.size()-n+1); i++){
				String ngramContent = "";
				int start = (int) wordLineTup.get(i).getValue();
				int end = (int) wordLineTup.get(i+n-1).getValue();
				int endI = i+n-1;
				for (int j = i; j < i+n; j++){
					ngramContent = ngramContent + " " + wordLineTup.get(j).getKey();
				}
				Ngram ngram = new Ngram(ngramContent, start, end);
				ngramList.add(ngram);
			}
			
			return ngramList;
		}
		public static ArrayList<Tuple<Ngram, Ngram>> compareList(ArrayList<Ngram> l1, ArrayList<Ngram> l2, int anomalies){
			//return arraylist of tuples of ngrams
			ArrayList<Tuple<Ngram, Ngram>> matches = new ArrayList<Tuple<Ngram, Ngram>>();
			for (int i= 0; i < l1.size(); i++){
				for (int j = 0 ; j< l2.size(); j++){
					if (compareString(l1.get(i).getContent(), l2.get(j).getContent(), anomalies)){
						Tuple<Ngram, Ngram> temp = new Tuple<Ngram, Ngram>(l1.get(i), l2.get(j));
						matches.add(temp);
					}
				}
			}
			return matches;
		
		}
		public static Boolean compareString (String s1, String s2, int anomalies){
			String[] s1Split = s1.split(" ");
			String[] s2Split = s2.split(" ");
			int mismatches = 0;
			for (int i = 0; i < s1Split.length; i++){
				
				if (s1Split[i].equals(s2Split[i]) == false){
					mismatches++;
				}
				if (mismatches > anomalies){
					return false;
				}
			}
			return true;
		}
	}
	class Ngram{
		private String content;
		private int start;
		private int end;
		public Ngram(String c, int s, int e){
			this.content = c;
			this.start = s;
			this.end = e;
		}
		public int getStart(){
			return this.start;
		}
		public int getEnd(){
			return this.end;
		}
		public String getContent(){
			return this.content;
		}
		public void setStart(int s){
			this.start = s;
		}
		public void setEnd(int e){
			this.end = e;
		}
		public void setContent (String c){
			this.content = c;
		}
		@Override
		public String toString(){
			return " LOCATION: (" + String.valueOf(this.start)+
					","+String.valueOf(this.end) + ")" + "   CONTENT: " + this.content+ "\n";
	    }

	}
	class Tuple <K, V>{
		private K key;
		private V value;
		public Tuple(K  k, V v){
			this.key = k;
			this.value = v;
		}
		public K getKey(){
			return this.key;
		}
		public V getValue(){
			return this.value;
		}
		public void setKey(K k){
			this.key = k;
		}
		public void setValue(V v){
			this.value = v;
		}
		@Override
		public String toString(){
			return String.valueOf(this.key) + ", " + String.valueOf(this.value) + "\n";
	    }
}
