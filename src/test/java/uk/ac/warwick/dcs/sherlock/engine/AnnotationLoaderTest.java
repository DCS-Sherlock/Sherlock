package uk.ac.warwick.dcs.sherlock.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.util.Side;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationLoaderTest {
    SherlockEngine se = new SherlockEngine(Side.CLIENT);
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Disabled
    @Test
    void registerModules() {
        se.initialise();
        //AnnotationLoader al = new AnnotationLoader();
        //al.registerModules();

    }
}