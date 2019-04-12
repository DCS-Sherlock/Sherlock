package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

class FileUploadHelper {

	private static String[] archiveExs = {"zip", "tar", "tgz"};

	static void storeFile(EmbeddedDatabase database, BaseStorageFilesystem filesystem, IWorkspace workspace, String filename, byte[] fileContent, boolean archiveHasManySubmissions)
			throws WorkspaceUnsupportedException {

		if (!(workspace instanceof EntityWorkspace)) {
			throw new WorkspaceUnsupportedException("IWorkspace instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityWorkspace w = (EntityWorkspace) workspace;

		String ex = FilenameUtils.getExtension(filename);
		if (ex.equals("gz")) {
			filename = FilenameUtils.removeExtension(filename);
			ex = FilenameUtils.getExtension(filename);
			fileContent = handleGZip(fileContent);
		}

		if (Arrays.asList(archiveExs).contains(ex)) {
			storeArchive(database, filesystem, w, FilenameUtils.removeExtension(filename), ex, fileContent, archiveHasManySubmissions);
		}
		else {
			//storeIndividualFile(database, filesystem, w, filename, fileContent);
		}

		database.refreshObject(w);
	}

	private static byte[] handleGZip(byte[] fileContent) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(fileContent));
			final byte[] buffer = new byte[64];
			int n;
			while (-1 != (n = in.read(buffer))) {
				out.write(buffer, 0, n);
			}
			return out.toByteArray();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static void storeArchive(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityWorkspace workspace, String filename, String extension, byte[] fileContent, boolean archiveHasManySubmissions) {
		try {
			ArchiveInputStream archiveInputStream;
			ArchiveEntry archiveEntry;

			switch(extension) {
				case "zip":
					archiveInputStream = new ZipArchiveInputStream(new ByteArrayInputStream(fileContent));
					archiveEntry = archiveInputStream.getNextEntry();
					break;
				case "tar":
					archiveInputStream = new TarArchiveInputStream(new ByteArrayInputStream(fileContent));
					archiveEntry = archiveInputStream.getNextEntry();
					break;
				case "tgz":
					byte[] tarBytes = handleGZip(fileContent);
					if (tarBytes == null) {
						BaseStorage.logger.error("Error decompressing .tgz file");
						return;
					}

					archiveInputStream = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes));
					archiveEntry = archiveInputStream.getNextEntry();
					break;
				default:
					BaseStorage.logger.error("Unsupported archive format");
					return;
			}

			EntityArchive submission = (EntityArchive) BaseStorage.instance.createSubmission(workspace, filename);
			EntityArchive curArchive = submission;

			while (archiveEntry != null) {
				if (archiveEntry.isDirectory()) {
					String[] dirs = FilenameUtils.separatorsToUnix(archiveEntry.getName()).split("/");
					curArchive = submission;
					for (String dir : dirs) {
						EntityArchive nextArchive = curArchive.getChildren() == null ? null : curArchive.getChildren().stream().filter(x -> x.getName().equals(dir)).findAny().orElse(null);

						if (nextArchive == null) {
							nextArchive = new EntityArchive(dir, curArchive);
							database.storeObject(nextArchive);
						}
						curArchive = nextArchive;
					}
				}
				else {
					storeIndividualFile(database, filesystem, curArchive, archiveEntry.getName(), IOUtils.toByteArray(archiveInputStream));
				}
				archiveEntry = archiveInputStream.getNextEntry();
			}

			archiveInputStream.close();
		}
		catch (IOException | WorkspaceUnsupportedException e) {
			e.printStackTrace();
		}
	}

	private static void storeIndividualFile(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityArchive archive, String filename, byte[] fileContent) {
		int line = 0;
		int contentLine = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)));

		try {
			while (reader.ready()) {
				line++;
				if (!reader.readLine().equals("")) {
					contentLine++;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		EntityFile file =
				new EntityFile(archive, FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename), new Timestamp(System.currentTimeMillis()), fileContent.length, line, contentLine);
		if (!filesystem.storeFile(file, fileContent)) {
			return;
		}

		database.storeObject(file);
	}

	/*private static void storeZip(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityWorkspace workspace, byte[] fileContent, boolean archiveHasManySubmissions) {
		try {
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileContent));
			ZipEntry zipEntry = zis.getNextEntry();
			EntityArchive curArchive = submission;

			while (zipEntry != null) {
				if (zipEntry.isDirectory()) {
					String[] dirs = FilenameUtils.separatorsToUnix(zipEntry.getName()).split("/");
					curArchive = submission;
					for (String dir : dirs) {
						EntityArchive nextArchive = curArchive.getChildren() == null ? null : curArchive.getChildren().stream().filter(x -> x.getName().equals(dir)).findAny().orElse(null);

						if (nextArchive == null) {
							nextArchive = new EntityArchive(dir, curArchive);
							database.storeObject(nextArchive);
						}
						curArchive = nextArchive;
					}
				}
				else {
					storeIndividualFile(database, filesystem, curArchive, zipEntry.getName(), IOUtils.toByteArray(zis));
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}*/

}
