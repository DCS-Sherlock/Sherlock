/**
 * 
 */
package uk.ac.warwick.dcs.sherlock.services.detection;

/**
 * Holds the start and end lines for both files which match
 * 
 * @author Aliyah
 */
class Run {
	private Ngram f1;
	private Ngram f2;
	private Tuple<Integer, Integer> f1Indicies;
	private Tuple<Integer, Integer> f2Indicies;
	private int runLength;
	public Run(Ngram file1, Ngram file2, Tuple<Integer, Integer> t1, Tuple<Integer, Integer> t2, int l){
		this.f1 = file1;
		this.f2 = file2;
		this.f1Indicies = t1;
		this.f2Indicies = t2;
		this.runLength = l;
	}
	public int getRunLength(){
		return this.runLength;
	}
	public void setRunLength(int l){
		this.runLength = l;
	}
	public Ngram getFile1(){
		return this.f1.clone();
	}
	public Ngram getFile2(){
		return this.f2.clone();
	}
	@SuppressWarnings("unchecked")
	public Tuple<Integer, Integer> getFile1Indicies(){ return f1Indicies.clone();	}
	@SuppressWarnings("unchecked")
	public Tuple<Integer, Integer> getFile2Indicies(){
		return this.f2Indicies.clone();
	}

	public void setFile1(Ngram file1){
		this.f1 = file1.clone();
	}
	public void setFile2(Ngram file2){
		this.f2 = file2.clone();
	}
	@SuppressWarnings("unchecked")
	public void setFile1Indicies(Tuple<Integer, Integer> t1){
		this.f1Indicies = t1.clone();
	}
	@SuppressWarnings("unchecked")
	public void setFile2Indicies(Tuple<Integer, Integer> t2){
		this.f2Indicies = t2.clone();
	}
	@Override
	public String toString(){
		return "Run length: " + this.runLength + " \n \tFile1: " + "\n\t\t"+this.f1.toString() + "\n\tFile2: " + "\n\t\t"+this.f2.toString() + "\n f1Indicies : " + this.f1Indicies + "  f2Indicies: " + this.f2Indicies;
	} 
}