package uk.ac.warwick.dcs.sherlock.services.fileSystem;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.*;

/**
 * @author Aliyah
 *
 */
class DirectoryProcessorTest {
	private static File base_Dir;
	private static File test_Dir;
	private static File filterable_Dir;
	private static boolean mkdir_Success;
	private File extractTo;
	/**
	 * @throws java.lang.Exception
	 * Executed before running any of the tests
	 * Create a directory to add the files to
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		base_Dir = new File("base");
		mkdir_Success = base_Dir.mkdir();
		if (!mkdir_Success){
			throw new Exception("Cannot create directory: " + base_Dir.getAbsolutePath());
		}
		filterable_Dir = new File(base_Dir.getAbsolutePath(),"filter");
		mkdir_Success = filterable_Dir.mkdir();
		if (!mkdir_Success){
			throw new Exception("Cannot create directory: " + filterable_Dir.getAbsolutePath());
		}
		test_Dir = new File(base_Dir.getAbsolutePath(),"test");
		mkdir_Success = test_Dir.mkdir();
		if (!mkdir_Success){
			throw new Exception("Cannot create directory: " + test_Dir.getAbsolutePath());
		}
		File tempFileJava = new File(filterable_Dir, "temp.java");
		File tempFileTxt = new File(filterable_Dir, "temp.txt");
		File tempzip = new File(filterable_Dir, "temp.zip");
		tempzip.createNewFile();
		File tempgzip = new File(filterable_Dir, "temp.gz");
		tempgzip.createNewFile();
		File gzip =  new File(test_Dir, "temp.gz");
		try{
			BufferedWriter writer1 = new BufferedWriter(new FileWriter(tempFileJava.getAbsolutePath()));
			writer1.write("public class {}");
			writer1.close();
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(tempFileTxt.getAbsolutePath()));
			writer2.write("a text file");
			writer2.close();
			new GZIPOutputStream(new FileOutputStream((gzip))).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 * Executed after running all of the tests
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		try{
			FileUtils.forceDelete(base_Dir);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 * Executed before each of the tests; can be called multiple times
	 */
	@BeforeEach
	void setUp() throws Exception {
	}
	/**
	 * @throws java.lang.Exception
	 * Executed after each of the tests; can be called multiple times
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

//	@Test
//	void testSelectDirectory() {
//		// Extract test_Dir to directory stored in extractTo variable
//		DirectoryProcessor dp = new DirectoryProcessor(filterable_Dir, extractTo);
//		File[] sourceFiles = test_Dir.listFiles(new FileFilter() {
//		    @Override
//		    public boolean accept(File file) {
//		        return !file.isHidden();
//		    }
//		});
//		System.out.println("*********" + sourceFiles.length);
//		System.out.println("testdir is: " + test_Dir);
//		String target = System.getProperty("user.home") + File.separator + "Sherlock" +  File.separator + extractTo + "/Preprocessing/Original/";
//		System.out.println("target: "+ target);
//		File[] targetFiles = new File(target).listFiles(new FileFilter() {
//		    @Override
//		    public boolean accept(File file) {
//		        return !file.isHidden();
//		    }
//		});
//
//		String[] sourceStrings = new String[sourceFiles.length] ;
//		String[] targetStrings = new String[targetFiles.length] ;
//		int i = 0;
//		for (File f : sourceFiles ) {
//			sourceStrings[i] = f.getName();
//			//System.out.println("Name: "+ f.getName());
//			i++;
//		}
//
//		i = 0;
//		for (File f : targetFiles ) {
//			targetStrings[i] = f.getName();
//			//System.out.println("Name: "+ f.getName());
//			i++;
//		}
//		assertArrayEquals(sourceStrings, targetStrings);
//	}

	@Disabled
	void testUnzipFiles() throws Exception {
		// Extract test_Dir containing a Zip File to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo.getAbsolutePath());
		assertTrue(extractTo.length() > 0);
	}
	/*
	@Disabled
	void testUnZipGFiles() {
		// Extract test_Dir containing a GZip File to directory stored in extractTo variable
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo);
		assertTrue(!extractTo.isEmpty());
	}
	*/


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
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new AcceptedFileFilter() );
		assertEquals(2, dp.getInputFiles().length);
	}

	/**
	 * Test for directories
	 */
	@Test
	void testDirectoryFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new DirectoryFilter() );
		assertEquals(0, dp.getInputFiles().length);
	}

	/**
	 * Test for GZip files
	 */
	@Test
	void testGZipFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir, new GZipFilenameFilter() );
		assertEquals(1, dp.getInputFiles().length);
	}

	/**
	 * Test for Java files
	 */
	@Test
	void testJavaFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new JavaFileFilter() );
		assertEquals(1, dp.getInputFiles().length);
	}

	/**
	 * Test for plain text files
	 */
	@Test
	void testPlainTextFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir , new PlainTextFilter() );
		assertEquals(1, dp.getInputFiles().length);
	}
	/**
	 * Test for Zip files
	 */
	@Test
	void testZipFileFilter() {
		DirectoryProcessor dp = new DirectoryProcessor( filterable_Dir, new ZipFilenameFilter() );
		assertEquals(1, dp.getInputFiles().length);
	}
}
