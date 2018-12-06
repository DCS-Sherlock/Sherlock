package uk.ac.warwick.dcs.sherlock.api.util;

public interface ISourceFile {


	/*try (BufferedReader r = Files.newBufferedReader(path, encoding)) {
		r.lines().forEach(System.out::println);
	}*/


	/* How do we want to handle file reading, we should really store in mem to prevent constantly having to read files from the disk and perform the preprocessing. Ideally we woudld
	 * do this only once and cache the files we are working on, however will we have enough memory to do this when we get large projects, how should we handle this???
	 */

	/* TODO: Temporary implementation, requires update */
	String getFilename();



	// extend to provide file content

}
