package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sherlock.fileSystem.DirectoryProcessor;
import sherlock.model.analysis.FileTypes;
import sherlock.model.analysis.SettingProfile;
import sherlock.model.analysis.Settings;
import sherlock.model.analysis.preprocessing.Preprocessor;

class TestPreprocessing {
	static Preprocessor p ;
	static Settings s;
	
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
		System.out.println("Here");
		
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
}
