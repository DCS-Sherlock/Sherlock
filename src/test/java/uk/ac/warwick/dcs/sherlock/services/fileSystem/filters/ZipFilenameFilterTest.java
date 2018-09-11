package uk.ac.warwick.dcs.sherlock.services.fileSystem.filters;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class ZipFilenameFilterTest {

	@Test
	void testAcceptZIP() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.ZIP";
		File fake = new File(folder.toString());
		ZipFilenameFilter filter = new ZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}
	void testAcceptzip() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.zip";
		File fake = new File(folder.toString());
		ZipFilenameFilter filter = new ZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}
	void testRejectZOP() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.zop";
		File fake = new File(folder.toString());
		ZipFilenameFilter filter = new ZipFilenameFilter();
		assertFalse(filter.accept(fake, filename));
	}
}
