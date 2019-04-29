package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NGramDetectorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Disabled("Needs model data items as input")
    @Test
    void buildWorkers() {
        NGramDetector detector = new NGramDetector();
        assertNotNull(detector.buildWorkers(null));
    }

    @Test
    void getAbstractPairwiseDetectorWorker() {
        NGramDetector detector = new NGramDetector();
        assertNotNull(detector.getAbstractPairwiseDetectorWorker(null, null));
    }

    @Test
    void getDisplayName() {
        IDetector detector = new NGramDetector();
        String name = detector.getDisplayName();
        assertEquals("N-Gram Detector", name);
    }

    @Test
    void getPreProcessors() {
        IDetector detector = new NGramDetector();
        List<PreProcessingStrategy> listOfPreProcessors = detector.getPreProcessors();
        assertAll(() -> assertEquals(1, listOfPreProcessors.size()), () -> assertEquals("no_whitespace", listOfPreProcessors.get(0).getName()));
    }
}