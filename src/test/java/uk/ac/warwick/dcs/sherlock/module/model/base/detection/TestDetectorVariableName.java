package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestDetectorVariableName {

	@Test
	void getAbstractPairwiseDetectorWorker() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		VariableNameDetector t = new VariableNameDetector();
		assertNotNull(t.getAbstractPairwiseDetectorWorker(null, null));
	}

	@Test
	void getDisplayName() {
		VariableNameDetector t = new VariableNameDetector();
		assertEquals("Variable Name Detector", t.getDisplayName());
	}

	@Test
	void getLexer() {
		VariableNameDetector t = new VariableNameDetector();
		//assertTrue(Lexer.class.isAssignableFrom(t.getLexer("Java")));
	}

	@Test
	void getPreProcessors() {
		VariableNameDetector t = new VariableNameDetector();
		List<PreProcessingStrategy> listOfPreProcessors = t.getPreProcessors();
		assertAll(() -> assertEquals(1, listOfPreProcessors.size()), () -> assertEquals("variables", listOfPreProcessors.get(0).getName()));
	}

	@Test
	void getSupportedLanguages() {
		VariableNameDetector t = new VariableNameDetector();
		//String[] supportedLanguages = t.getSupportedLanguages();
		//assertAll(() -> assertEquals(1, supportedLanguages.size()), () -> assertEquals(Language.JAVA, supportedLanguages.get(0)), () -> assertNotEquals(Language.HASKELL, supportedLanguages.get(0)));
		//redo this, sorry :(
	}
}