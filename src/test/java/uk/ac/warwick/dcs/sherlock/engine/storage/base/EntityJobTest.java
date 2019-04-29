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

import static org.junit.jupiter.api.Assertions.*;

class EntityJobTest {
    SherlockEngine se;
    BaseStorage bs;
    EntityJob ej;

    @BeforeEach
    void setUp() {
        se = new SherlockEngine(Side.CLIENT);
        SherlockRegistry.registerDetector(VariableNameDetector.class);
        SherlockRegistry.registerDetector(NGramDetector.class);
        bs = new BaseStorage();
        EntityWorkspace ws = (EntityWorkspace) bs.createWorkspace("test", "Java");
        ej = new EntityJob(ws);
    }

    @AfterEach
    void tearDown() {
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
    void failAddDetectorWhenPrepared() {
        try {
            Field f = ej.getClass().getDeclaredField("prepared");
            f.setAccessible(true);
            f.set(ej, true);
            boolean success = ej.addDetector(NGramDetector.class);
            assertFalse(success);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    void removeDetector() {
        ej.addDetector(NGramDetector.class);
        boolean success = ej.removeDetector(NGramDetector.class);
        assertTrue(success);
    }

    @Test
    void failRemoveDetectorWhenPrepared() {
        ej.addDetector(NGramDetector.class);
        try {
            Field f = ej.getClass().getDeclaredField("prepared");
            f.setAccessible(true);
            f.set(ej, true);
            boolean success = ej.removeDetector(NGramDetector.class);
            assertFalse(success);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}