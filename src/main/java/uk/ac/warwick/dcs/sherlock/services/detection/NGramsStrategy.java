package uk.ac.warwick.dcs.sherlock.services.detection;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.io.IOException;	
import java.util.*;

import uk.ac.warwick.dcs.sherlock.SettingProfile;

class NGramsStrategy implements DetectionStrategy {

	NGramsStrategy(){}
	
	@Override
	public ArrayList<Edge> doDetection(File[] filesToCompare, SettingProfile sp) {
		System.out.println("Detection Strategy: \t Samelines Detection");
		String description = sp.getDescription();
		
		String parent = filesToCompare[0].getParentFile().getParentFile().getParent();
		System.out.println("------Trying to make a report directory " + parent);
		String targetDirectory = parent+"\\Report\\" +description;
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
        ArrayList<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < filesToCompare.length ; i++ ) {
			for (int j = i+1; j < filesToCompare.length ; j++ ) {

                ArrayList<Run> matches = findMatches(filesToCompare[i], filesToCompare[j], 10, 1);
				String name1 = filesToCompare[i].getName();
				name1 = name1.replaceAll(" ", "_");
				name1 = name1.replaceAll(".java", "");
				String name2 = filesToCompare[j].getName();
				name2 = name2.replaceAll(" ", "_");
				name2 = name2.replaceAll(".java", "");
				File f = new File(targetDirectory+"\\"+name1+"__"+name2+".txt");
				try {
					f.createNewFile();
					System.out.println("In NGgramStrategy: File was created");
					
				} catch (IOException e) {
					System.out.println("In NGgramStrategy: File already exists");
				}
				writeToFile(f, matches, name1, name2);
                Edge edge = generateEdge(matches, name1, name2);
                edges.add(edge);
			}

		}
		return edges;

	}
    public Edge generateEdge(ArrayList<Run> matches, String filename1, String filename2){
        int totalRunLength = 0;
        int largestRun = 0;

        for (int i =0; i<matches.size(); i++){
            int currentRunLength = matches.get(i).getRunLength();
            System.out.println(currentRunLength);
            if (largestRun<currentRunLength){
                largestRun = currentRunLength;
            }
            totalRunLength += currentRunLength;
        }
        Edge edge = new Edge(filename1, filename2, totalRunLength*largestRun);
	    return edge;
    }
	private void writeToFile(File f, ArrayList<Run> list, String name1, String name2) {
		try {
			String intro = "Similarities between: " + name1 + " and " + name2;
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(intro);
			writer.newLine();
			writer.newLine();
			for (int i = 0; i < list.size(); i ++) {
				Run listElement = list.get(i);
				writer.write("File1");
				writer.newLine();
				String ngram1 = listElement.getFile1().getContent().toString();
				writer.write(ngram1);
				writer.newLine();
				writer.write("File2");
				writer.newLine();
				String ngram2 = listElement.getFile2().getContent().toString();
				writer.write(ngram2);
				writer.newLine();
				writer.newLine();
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println("FAILED TO WRITE TO FILE");
		}
	}
	private ArrayList<Run> findMatches (File f1, File f2, int nSize, int anomalies){
			String s1 = readFile(f1.getAbsolutePath(), Charset.defaultCharset());
			String s2 = readFile(f2.getAbsolutePath(), Charset.defaultCharset());
			ArrayList<Tuple<String, Integer>> l1 = generateList(s1);
			ArrayList<Tuple<String, Integer>> l2 = generateList(s2);
			ArrayList<Run> runList = getRuns(l1, l2, nSize, anomalies);
			return runList;
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
	
	private ArrayList<Tuple<String, Integer>> generateList (String s){
			ArrayList<Tuple<String, Integer>> fileBreakdown = new ArrayList<Tuple<String, Integer>>();
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
		public int numOfEOL (String s) {
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
		public ArrayList<Run> getRuns(ArrayList<Tuple<String, Integer>> file1List, ArrayList<Tuple<String, Integer>> file2List, int minNgramLength, int anomalies){
			ArrayList<Run> runList = new ArrayList<Run>();
			for (int i = 0; i < file1List.size(); i++){
				int differences = 0;
				for (int j = 0; j < file2List.size(); j++){
					if (file1List.get(i).getKey().equals(file2List.get(j).getKey())){
						
						int counter = 0;
						//find the length of sequence of similar words
						while(differences <= anomalies && i+counter+1 < file1List.size() && j+counter+1 < file2List.size()){
							counter++;
							//stop when differences exceed the number of allows anomalies
							if (!file1List.get(i+counter).getKey().equals(file2List.get(j+counter).getKey())){
								differences++;
							}
						}
						//check the run is long enough
						if (counter< minNgramLength){
							continue;
						}
						//check if the run is already included in another run
						if (isEncompassed(runList, i, i+counter-1, j, j+counter-1)){
//	 						System.out.println("encompassed is called");
							continue;
						}
						System.out.println("Ready to generate run");
						//generate the run object
						Run r = generateRun(file1List, file2List, counter-1, i, j);
						runList.add(r);
					}
				}
				
			}
			return runList;
		}
		
		public Run generateRun(ArrayList<Tuple<String, Integer>> file1List, ArrayList<Tuple<String, Integer>> file2List, int counter, int i, int j){
			Tuple<Integer, Integer> file1Indicies = new Tuple<Integer, Integer>(i, i+counter);
			Tuple<Integer, Integer> file2Indicies = new Tuple<Integer, Integer>(j, j+counter);
			String content1 = "";
			int start1 = file1List.get(i).getValue();
			int end1 = start1;
			for (Tuple<String,Integer> t : file1List.subList(i, i+counter)){
				content1 += t.getKey() +" ";
				end1 = t.getValue();
			}
			String content2 = "";
			int start2 = file2List.get(j).getValue();
			int end2 = start2;
			for (Tuple<String,Integer> t : file2List.subList(j, j+counter)){
				content2 += t.getKey() +" ";
				end2 = t.getValue();
			}
			Ngram n1 = new Ngram(content1, start1, end1);
			Ngram n2 = new Ngram(content2, start2, end2);
			return new Run(n1, n2, file1Indicies, file2Indicies, counter);
		}
		
		//function to determine if 2 ranges are encompassed by some run that is already in the list
		public boolean isEncompassed (ArrayList<Run> runList, int f1start, int f1end, int f2start, int f2end){
			for(int i = 0; i < runList.size(); i++){
				if ((f1start>=runList.get(i).getFile1Indicies().getKey()) && 
							(f1end <= runList.get(i).getFile1Indicies().getValue())){
					if ((f2start>=runList.get(i).getFile2Indicies().getKey()) && 
							(f2end <= runList.get(i).getFile2Indicies().getValue())){
						return true;
					}
				}
			}
			return false;
		} 
}

