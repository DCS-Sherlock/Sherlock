/**
 * JUnit class to test the Directory Processor Class
 */
package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileFilter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sherlock.fileSystem.DirectoryProcessor;
import sherlock.fileSystem.filters.*;

/**
 * @author Aliyah
 *
 */
class TestDirectoryProcessor {
	String testDirectory = System.getProperty("user.home") + File.separator + "test_Input";
	File test_Dir = new File(testDirectory);
	String filterableDirectory = System.getProperty("user.home") + File.separator + "Sherlock" +  File.separator + "test_Input1" + File.separator + "Preprocessing" + File.separator + "Original" ;
	File filterable_Dir = new File(filterableDirectory);
	String extractTo = "test_Input1";
	/**
	 * @throws java.lang.Exception
	 * Executed before running any of the tests 
	 * Create a directory to add the files to
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 * Executed after running all of the tests
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 * Executed before each of the tests; can be called multiple times
	 */
	@BeforeEach
	void setUp() throws Exception {
		new File(testDirectory);
	}

	/**
	 * @throws java.lang.Exception
	 * Executed after each of the tests; can be called multiple times
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSelectDirectory() {
		// Extract test_Dir to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo);
		File[] sourceFiles = test_Dir.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		String target = System.getProperty("user.home") + File.separator + "Sherlock" +  File.separator + extractTo + "/Preprocessing/Original/";
		System.out.println("target: "+ target);
		File[] targetFiles = new File(target).listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		
		String[] sourceStrings = new String[sourceFiles.length] ;
		String[] targetStrings = new String[targetFiles.length] ;
		int i = 0;
		for (File f : sourceFiles ) {
			sourceStrings[i] = f.getName();
			System.out.println("Name: "+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : targetFiles ) {
			targetStrings[i] = f.getName();
			System.out.println("Name: "+ f.getName());
			i++;
		}
	
		// Check the extractTo file is non-empty
		assertArrayEquals(sourceStrings, targetStrings);
	}
	
	@Test
	void testUnzipFiles() {
		// Extract test_Dir containing a Zip File to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo);
		
		assertTrue(!extractTo.isEmpty());
	}
	
	@Test
	void testUnZipGFiles() {
		// Extract test_Dir containing a GZip File to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo);
		
		assertTrue(!extractTo.isEmpty());
	}
	
	
	/************************************************************************************************
	 * Test File Filters
	 * 
	 * Ensure after applying a file filter, only files of the filtered type exist in the directory
	 *************************************************************************************************/
	
	/**
	 * Test for files Accepted by the Sherlock Application
	 */
	@Test
	void testAcceptedFileFilter() {
		// Extract test_Dir containing a GZip File to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new AcceptedFileFilter() ); 
		assertTrue(dp.getInputFiles().length == 5);
	}
	
	/**
	 * Test for directories
	 */
	@Test
	void testDirectoryFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new DirectoryFilter() );
		
		assertTrue(dp.getInputFiles().length == 0);
	}
	
	/**
	 * Test for GZip files
	 */
	@Test
	void testGZipFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( test_Dir, new GZipFilenameFilter() );
		
		assertTrue(dp.getInputFiles().length == 1);
	}
	
	/**
	 * Test for Java files
	 */
	@Test
	void testJavaFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new JavaFileFilter() );
		
		assertTrue(dp.getInputFiles().length == 2);
	}
	
	/**
	 * Test for plain text files
	 */
	@Test
	void testPlainTextFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new PlainTextFilter() );
		
		assertTrue(dp.getInputFiles().length == 3);
	}
	
	/**
	 * Test for all source code languages accepted by the Sherlock Application
	 */
	@Test
	void testSourceCodeFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new SourceCodeFilter() );
		
		assertTrue(dp.getInputFiles().length == 2);
	}
	
	/**
	 * Test for Zip files
	 */
	@Test
	void testZipFileFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( test_Dir, new ZipFilenameFilter() );
		
		assertTrue(dp.getInputFiles().length == 1);
	}
}
