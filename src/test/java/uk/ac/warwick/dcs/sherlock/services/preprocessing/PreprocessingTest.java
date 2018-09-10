package uk.ac.warwick.dcs.sherlock.services.preprocessing;
import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileFilter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.JavaFileFilter;
import uk.ac.warwick.dcs.sherlock.FileTypes;
import uk.ac.warwick.dcs.sherlock.SettingProfile;
import uk.ac.warwick.dcs.sherlock.Settings;
import uk.ac.warwick.dcs.sherlock.services.preprocessing.Preprocessor;

import org.apache.commons.io.FilenameUtils;

class PreprocessingTest {
	static Preprocessor p ;
	static Settings s;
	static String originalDirectory ;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String testDirectory = System.getProperty("user.home") + File.separator + "test_Input";
		File test_Dir = new File(testDirectory);
		String extractTo = "test_Input";
		DirectoryProcessor dp = new DirectoryProcessor(test_Dir, extractTo);
		
		String sourceDirectory = System.getProperty("user.home") + File.separator + "Sherlock" +  File.separator + "test_Input";
		s = new Settings();
		s.setSourceDirectory(new File(sourceDirectory));
		s.initialiseDefault();
		
		originalDirectory = System.getProperty("user.home") + File.separator + "Sherlock" +  File.separator + "test_Input" + File.separator + "Preprocessing" +File.separator + "Original" ;
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		s.initialiseDefault();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testNoWS() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachNoWSExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}
	
	
	@Test
	void testNoComments() {
		s.getNoCommentsProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoCommentsProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachNoComExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}
	
	
	@Test
	void testNoCommentsNoWS() {
		
		s.getNoCWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoCWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachNoComWSExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}
	
	@Test
	void testComments() {
		s.getCommentsProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getCommentsProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachComExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}
	
	@Test
	void testTokenised() {
		s.getTokenisedProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getTokenisedProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachTokenisedExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}

	@Test
	void testWSPattern() {
		s.getWSPatternProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getWSPatternProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		boolean isEmpty = false ;
		for ( File f : files ) {
			if (f.length() == 0 ) {
				isEmpty = true;
			}
		}
		assertFalse(isEmpty);
	}
	
	@Test
	void testEachWSExists() {
		s.getNoWSProfile().setInUse( true );
		p = new Preprocessor(s);
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		File[] files = od.listFiles();
		
		File[] originalFiles = new File(originalDirectory).listFiles(new JavaFileFilter());
		
		String[] originalStrings = new String[originalFiles.length] ;
		String[] fileStrings = new String[files.length] ;
		int i = 0;
		for (File f : originalFiles ) {
			originalStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		i = 0;
		for (File f : files ) {
			fileStrings[i] = FilenameUtils.removeExtension(f.getName());
			System.out.println("Name: \n \n"+ f.getName());
			i++;
		}
		
		assertArrayEquals(originalStrings, fileStrings);
	}
}
