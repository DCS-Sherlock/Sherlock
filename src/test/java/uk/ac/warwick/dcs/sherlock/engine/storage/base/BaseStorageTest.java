package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.component.IJob;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.api.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import uk.ac.warwick.dcs.sherlock.engine.storage.BaseStorage;
import uk.ac.warwick.dcs.sherlock.engine.storage.EntityArchive;
import uk.ac.warwick.dcs.sherlock.engine.storage.EntityWorkspace;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BaseStorageTest {
    BaseStorage bs;
    static SherlockEngine se;
    String language = "Java";
    IWorkspace ws1, ws2, ws3;
    int initialSize;

    @BeforeAll
    static void setupAll() {
        se = new SherlockEngine(Side.CLIENT);
    }

    @BeforeEach
    void setUp() {
        bs = new BaseStorage();
        initialSize = bs.getWorkspaces().size();
        ws1 = bs.createWorkspace("Test1", language);
        ws2 = bs.createWorkspace("Test2", language);
        ws3 = bs.createWorkspace("Test3", language);
    }

    @AfterEach
    void tearDown() {
        bs.getDatabase().removeObject(ws1);
        bs.getDatabase().removeObject(ws2);
        bs.getDatabase().removeObject(ws3);
    }

    @Test
    void createWorkspace() {
        IWorkspace ws = bs.createWorkspace("Test", language);
        assertNotNull(ws);
        assertTrue(ws instanceof EntityWorkspace, "Workspace is not of type EntityWorkspace ");
        bs.getDatabase().removeObject(ws);
    }

    @Test
    void getWorkspacesFromIds() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(ws1.getPersistentId());
        ids.add(ws2.getPersistentId());
        List<IWorkspace> listWs = bs.getWorkspaces(ids);
        assertAll(
                () -> assertEquals(2, listWs.size(), "Did not retrieve just workspaces 1 and 2"),
                () -> assertTrue(listWs.contains(ws1), "Did not retrieve workspaces 1"),
                () -> assertTrue(listWs.contains(ws2), "Did not retrieve workspaces 2")
        );
    }

    @Test
    void getAllWorkspaces() {
        List<IWorkspace> listWs = bs.getWorkspaces();
        assertAll(
                () -> assertEquals(initialSize + 3, listWs.size(), "The three workspaces have not been addded successfully"),
                () -> assertTrue(listWs.contains(ws1), "Workspace 1 was not added successfully"),
                () -> assertTrue(listWs.contains(ws2), "Workspace 2 was not added successfully"),
                () -> assertTrue(listWs.contains(ws3), "Workspace 3 was not added successfully")
        );
    }

    //currently leaves the dot after the file name
    @Disabled("loading a file is not working on travis")
    @Test
    void storeFile() {
        MultipartFile file = null;
        try {
            String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\uk\\ac\\warwick\\dcs\\sherlock\\engine\\EventBus.java";
            InputStream i = new FileInputStream(filePath);
            file = new MockMultipartFile("TestFile", i);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        EntityArchive submission = new EntityArchive("Archive");
        submission.setSubmissionArchive((EntityWorkspace) ws1);
        bs.getDatabase().storeObject(submission);
        try {
            bs.storeFile(ws1, "testFile", file.getBytes());
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (WorkspaceUnsupportedException e) {
            throw new AssertionError(e);
        }
        long id = ws1.getFiles().get(0).getPersistentId();
        ISourceFile sf = bs.getSourceFile(id);
        assertNotNull(sf);
        assertEquals("testFile.", sf.getFileDisplayName());
        bs.getDatabase().removeObject(submission);
    }

    @Disabled("loading a file is not working on travis")
    @Test
    void storeFileThrowsWorkerException() {
        MultipartFile file = null;
        try {
            String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\uk\\ac\\warwick\\dcs\\sherlock\\engine\\EventBus.java";
            InputStream i = new FileInputStream(filePath);
            file = new MockMultipartFile("TestFile", i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IWorkspace ws = new TestWorkspace();
        final MultipartFile finalFile = file;
        assertThrows(WorkspaceUnsupportedException.class, () -> bs.storeFile(ws, "testFile", finalFile.getBytes()));
        bs.getDatabase().removeObject(ws);
    }
}

class TestWorkspace implements IWorkspace {

    @Override
    public IJob createJob() {
        return null;
    }

    @Override
    public List<ISourceFile> getFiles() {
        return null;
    }

    @Override
    public List<ISubmission> getSubmissions() {
        return null;
    }

    @Override
    public List<IJob> getJobs() {
        return null;
    }

    @Override
    public String getLanguage() {
        return null;
    }

    @Override
    public void setLanguage(String lang) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public long getPersistentId() {
        return 0;
    }

    @Override
    public void remove() {

    }
}

