package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sherlock.fileSystem.DirectoryProcessor;
import sherlock.model.analysis.Settings;
import sherlock.model.analysis.detection.DetectionHandler;
import sherlock.model.analysis.preprocessing.Preprocessor;

class TestDetectionHandler {
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
		s.getNoWSProfile().setInUse( true );
		s.getNoCommentsProfile().setInUse( true );
		s.getNoCWSProfile().setInUse( true );
		s.getCommentsProfile().setInUse( true );
		s.getTokenisedProfile().setInUse( true );
		s.getWSPatternProfile().setInUse( true );
		
		Preprocessor p = new Preprocessor(s);
		DetectionHandler dh = new DetectionHandler(s);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	
	@Test
	void testNoWS() {
		String outputDirectory = s.getNoWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}
	
	@Test
	void testNoComments() {
		String outputDirectory = s.getNoCommentsProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}
	
	@Test
	void testNoCommentsNoWS() {
		String outputDirectory = s.getNoCWSProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}
	
	@Test
	void testComments() {
		String outputDirectory = s.getCommentsProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}
	
	@Test
	void testTokenised() {
		String outputDirectory = s.getTokenisedProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}

	@Test
	void testWSPattern() {
		String outputDirectory = s.getWSPatternProfile().getOutputDir();
		File od = new File(outputDirectory);
		
		fail("Not yet implemented");
	}

}
