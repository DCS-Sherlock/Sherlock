package uk.ac.warwick.dcs.sherlock.engine;

import org.antlr.v4.runtime.Lexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.VariableNameDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.CommentExtractor;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractorJava;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegistryTest {
    SherlockEngine se;

    Registry r;

    @BeforeEach
    public void setUp() {
        r = new Registry();
        se = new SherlockEngine(Side.CLIENT);
    }

    @Test
    void getAdvancedPostProcessorForLanguage() {
        r.registerLanguage("Java", JavaLexer.class);
        r.registerAdvancedPreProcessorGroup(VariableExtractor.class);
        r.registerAdvancedPreProcessorImplementation("uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor", VariableExtractorJava.class);
        ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> tuple = r.getAdvancedPostProcessorForLanguage(VariableExtractor.class, "Java");
        assertEquals(tuple.getKey(), VariableExtractorJava.class);
        assertEquals(tuple.getValue(), JavaLexer.class);
    }

    @Test
    void getDetectorAdjustableParameters() {
        assertTrue(r.registerDetector(NGramDetector.class));
        List<AdjustableParameterObj> list = r.getDetectorAdjustableParameters(NGramDetector.class);
        assertTrue(list.size() > 0);
        assertNotNull(list.get(0).getName());
        assertEquals("N-Gram Size", list.get(0).getDisplayName());
    }

    @Test
    void getDetectorDisplayName() {
        IDetector detector = new VariableNameDetector();
        String detectorName = detector.getDisplayName();
        Class detectorClass = detector.getClass();
        r.registerDetector(detectorClass);
        String displayName = r.getDetectorDisplayName(detectorClass);
        assertEquals(detectorName, displayName);
    }

    @Disabled("TestDetector Not linked to language")
    @Test
    void getDetectorLanguages() {
        r.registerLanguage("Java", JavaLexer.class);
        r.registerDetector(VariableNameDetector.class);
        Set<String> languages = r.getDetectorLanguages(VariableNameDetector.class);
        assertNotNull(languages);
        assertTrue(languages.contains("Java"));
    }

    @Test
    void getDetectors() {
        r.registerDetector(VariableNameDetector.class);
        r.registerDetector(NGramDetector.class);
        Set<Class<? extends IDetector>> set = r.getDetectors();
        assertTrue(set.contains(VariableNameDetector.class));
        assertTrue(set.contains(NGramDetector.class));
    }

    @Disabled("Neither detectors use JavaLexer")
    @Test
    void getDetectorsUsingLanguage() {
        r.registerLanguage("Java", JavaLexer.class);
        r.registerDetector(VariableNameDetector.class);
        r.registerDetector(NGramDetector.class);
        Set<Class<? extends IDetector>> set = r.getDetectors("Java");
        //assertTrue(set.contains(VariableNameDetector.class));
        //assertTrue(set.contains(NGramDetector.class));
    }

    @Test
    void getPostProcessorAdjustableParameters() {
        r.registerPostProcessor(NGramPostProcessor.class, NGramRawResult.class);
        List<AdjustableParameterObj> list = r.getPostProcessorAdjustableParameters(NGramPostProcessor.class);
        assertTrue(list.size() > 0);
        assertNotNull(list.get(0).getName());
    }

    @Test
    void getPostProcessorAdjustableParametersFromDetector() {
        r.registerDetector(NGramDetector.class);
        r.registerPostProcessor(NGramPostProcessor.class, NGramRawResult.class);
        List<AdjustableParameterObj> list = r.getPostProcessorAdjustableParametersFromDetector(NGramDetector.class);
        assertTrue(list.size() > 0);
        assertNotNull(list.get(0).getName());
    }

    @Disabled("Not implemented yet")
    @Test
    void getPostProcessorInstance() {

    }

    @Test
    void registerAdvancedPreProcessorGroup() {
        assertTrue(r.registerAdvancedPreProcessorGroup(VariableExtractor.class));
    }

    @Test
    void registerAdvancedPreProcessorImplementation() {
        assertTrue(r.registerAdvancedPreProcessorGroup(VariableExtractor.class));
        assertTrue(r.registerAdvancedPreProcessorImplementation("uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor", VariableExtractorJava.class));
    }

    @Test
    void registerDetector() {
        assertTrue(r.registerDetector(VariableNameDetector.class));
        assertTrue(r.registerDetector(NGramDetector.class));
    }

    @Disabled("Should the register method return false if we try to double register")
    @Test
    void failRegisterDetector() {
        assertTrue(r.registerDetector(NGramDetector.class));
        assertFalse(r.registerDetector(NGramDetector.class));
    }

    @Test
    void registerGeneralPreProcessor() {
        r.registerLanguage("Java", JavaLexer.class);
        assertTrue(r.registerGeneralPreProcessor(CommentExtractor.class));
    }

    @Test
    void registerLanguage() {
        assertTrue(r.registerLanguage("java", JavaLexer.class));
        assertTrue(r.registerLanguage("Java", JavaLexer.class));
    }

    @Test
    void registerPostProcessor() {
        assertTrue(r.registerPostProcessor(SimpleObjectEqualityPostProcessor.class, SimpleObjectEqualityRawResult.class));
        assertTrue(r.registerPostProcessor(NGramPostProcessor.class, NGramRawResult.class));
    }
}