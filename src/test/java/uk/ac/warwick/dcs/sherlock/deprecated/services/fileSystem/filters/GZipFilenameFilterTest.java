package uk.ac.warwick.dcs.sherlock.deprecated.services.fileSystem.filters;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

class GZipFilenameFilterTest {

	@Test
	void testAcceptgz() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.gz";
		File fake = new File(folder.toString());
		GZipFilenameFilter filter = new GZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}

	@Test
	void testAcceptGZ() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.GZ";
		File fake = new File(folder.toString());
		GZipFilenameFilter filter = new GZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}

	@Test
	void testAccepttgz() throws  Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.tgz";
		File fake = new File(folder.toString());
		GZipFilenameFilter filter = new GZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}

	@Test
	void testAcceptTGZ() throws Exception {
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.TGZ";
		File fake = new File(folder.toString());
		GZipFilenameFilter filter = new GZipFilenameFilter();
		assertTrue(filter.accept(fake, filename));
	}

	@Test
	void testRejectElse() throws Exception{
		Path folder = Files.createTempFile("Fake", ".txt");
		String filename = "fake.G";
		File fake = new File(folder.toString());
		GZipFilenameFilter filter = new GZipFilenameFilter();
		assertFalse(filter.accept(fake, filename));
	}

}
