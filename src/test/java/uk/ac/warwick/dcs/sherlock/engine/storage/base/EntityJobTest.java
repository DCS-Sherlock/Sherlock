package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.storage.BaseStorage;
import uk.ac.warwick.dcs.sherlock.engine.storage.EntityJob;
import uk.ac.warwick.dcs.sherlock.engine.storage.EntityWorkspace;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.VariableNameDetector;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityJobTest {
    SherlockEngine se;
    BaseStorage bs;
    EntityJob ej;
    EntityWorkspace ws;

    @BeforeEach
    void setUp() {
        se = new SherlockEngine(Side.CLIENT);
        SherlockRegistry.registerDetector(VariableNameDetector.class);
        SherlockRegistry.registerDetector(NGramDetector.class);
        bs = new BaseStorage();
        ws = (EntityWorkspace) bs.createWorkspace("test", "Java");
        ej = new EntityJob(ws);
    }

    @AfterEach
    void tearDown() {
        bs.getDatabase().removeObject(ws);
    }

    @Disabled("Only returns true")
    @Test
    void prepare() {
        boolean prepared = ej.prepare();
        assertTrue(prepared);
    }

    @Test
    void addDetector() {
        boolean success = ej.addDetector(VariableNameDetector.class);
        assertTrue(success);
    }

    @Test
    void failAddDetectorWhenPrepared() throws NoSuchFieldException, IllegalAccessException {
        Field f = ej.getClass().getDeclaredField("prepared");
        f.setAccessible(true);
        f.set(ej, true);
        boolean success = ej.addDetector(NGramDetector.class);
        assertFalse(success);
    }

    @Test
    void removeDetector() {
        ej.addDetector(NGramDetector.class);
        boolean success = ej.removeDetector(NGramDetector.class);
        assertTrue(success);
    }

    @Test
    void failRemoveDetectorWhenPrepared() throws IllegalAccessException, NoSuchFieldException {
        ej.addDetector(NGramDetector.class);
        Field f = ej.getClass().getDeclaredField("prepared");
        f.setAccessible(true);
        f.set(ej, true);
        boolean success = ej.removeDetector(NGramDetector.class);
        assertFalse(success);

    }

}