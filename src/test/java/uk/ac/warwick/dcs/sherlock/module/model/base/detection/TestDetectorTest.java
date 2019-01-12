package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestDetectorTest {

	@Test
	void getAbstractPairwiseDetectorWorker() {
		TestDetector t = new TestDetector();
		assertNotNull(t.getAbstractPairwiseDetectorWorker());
	}

	@Test
	void getDisplayName() {
		TestDetector t = new TestDetector();
		assertEquals("Test Detector", t.getDisplayName());
	}

	@Test
	void getLexer() {
		TestDetector t = new TestDetector();
		assertTrue(Lexer.class.isAssignableFrom(t.getLexer(Language.JAVA)));
	}

	@Test
	void getPreProcessors() {
		TestDetector t = new TestDetector();
		List<IPreProcessingStrategy> listOfPreProcessors = t.getPreProcessors();
		assertAll(() -> assertEquals(1, listOfPreProcessors.size()), () -> assertEquals("variables", listOfPreProcessors.get(0).getName()));
	}

	@Test
	void getSupportedLanguages() {
		TestDetector t = new TestDetector();
		Language[] supportedLanguages = t.getSupportedLanguages();
		//assertAll(() -> assertEquals(1, supportedLanguages.size()), () -> assertEquals(Language.JAVA, supportedLanguages.get(0)), () -> assertNotEquals(Language.HASKELL, supportedLanguages.get(0)));
		//redo this, sorry :(
	}
}