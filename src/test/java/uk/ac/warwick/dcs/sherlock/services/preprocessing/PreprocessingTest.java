package uk.ac.warwick.dcs.sherlock.services.preprocessing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.JavaFileFilter;
import uk.ac.warwick.dcs.sherlock.FileTypes;
import uk.ac.warwick.dcs.sherlock.SettingProfile;
import uk.ac.warwick.dcs.sherlock.Settings;
import uk.ac.warwick.dcs.sherlock.services.preprocessing.Preprocessor;

import org.apache.commons.io.FilenameUtils;

public class PreprocessingTest {
    private static Preprocessor p;
    private static Settings s;
    private static String originalDirectoryPath;
    private static File source_Dir;
    private static boolean mkdir_Success;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        source_Dir = new File("source");
        File origin_Directory = new File(source_Dir.getAbsolutePath(), "Preprocessing" + File.separator + "Original");
        mkdir_Success = origin_Directory.mkdirs();
        if (!mkdir_Success) {
            throw new Exception("Could not create folders");
        }
        originalDirectoryPath = origin_Directory.getAbsolutePath();
        File first_File = new File(origin_Directory.getAbsolutePath(), "temp.java");
        File second_File = new File(origin_Directory.getAbsolutePath(), "new.java");
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(first_File.getAbsolutePath()));
            writer1.write("//comment\npublic class {}");
            writer1.close();
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(second_File.getAbsolutePath()));
            writer2.write("//comment\npublic class {}");
            writer2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        s = new Settings();
        s.setSourceDirectory(source_Dir);
    }

    @BeforeEach
    void setUp() throws Exception {
        s.initialiseDefault();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        try {
            FileUtils.forceDelete(source_Dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testNoWS() {
        s.getNoWSProfile().setInUse(true);
        s.getSourceDirectory();
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachNoWSExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        assertArrayEquals(originalStrings, fileStrings);
    }


    @Test
    void testNoComments() {
        s.getNoCommentsProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoCommentsProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachNoComExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        assertArrayEquals(originalStrings, fileStrings);
    }


    @Test
    void testNoCommentsNoWS() {
        s.getNoCWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoCWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachNoComWSExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        assertArrayEquals(originalStrings, fileStrings);
    }

    @Test
    void testComments() {
        s.getCommentsProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getCommentsProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachComExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        assertArrayEquals(originalStrings, fileStrings);
    }

    @Test
    void testTokenised() {
        s.getTokenisedProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getTokenisedProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachTokenisedExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }

        assertArrayEquals(originalStrings, fileStrings);
    }

    @Test
    void testWSPattern() {
        s.getWSPatternProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getWSPatternProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();
        boolean isEmpty = false;
        for (File f : files) {
            if (f.length() == 0) {
                isEmpty = true;
            }
        }
        assertFalse(isEmpty);
    }

    @Test
    void testEachWSExists() {
        s.getNoWSProfile().setInUse(true);
        p = new Preprocessor(s);
        String outputDirectory = s.getNoWSProfile().getOutputDir();
        File od = new File(outputDirectory);
        File[] files = od.listFiles();

        File[] originalFiles = new File(originalDirectoryPath).listFiles(new JavaFileFilter());

        String[] originalStrings = new String[originalFiles.length];
        String[] fileStrings = new String[files.length];
        int i = 0;
        for (File f : originalFiles) {
            originalStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        i = 0;
        for (File f : files) {
            fileStrings[i++] = FilenameUtils.removeExtension(f.getName());
        }
        assertArrayEquals(originalStrings, fileStrings);
    }
}
