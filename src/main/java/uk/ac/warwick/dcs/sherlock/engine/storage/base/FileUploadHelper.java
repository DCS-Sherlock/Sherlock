package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import uk.ac.warwick.dcs.sherlock.api.common.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.component.IWorkspace;
import uk.ac.warwick.dcs.sherlock.engine.exception.WorkspaceUnsupportedException;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

class FileUploadHelper {

	private static String[] archiveExs = { "zip", "tar", "tgz" };

	private static EntityArchive createSubmission(EntityWorkspace workspace, String filename, List<ITuple<ISubmission, ISubmission>> ret) {
		try {
			ISubmission submission = BaseStorage.instance.getSubmissionFromName(workspace, filename);
			if (submission == null) {
				return (EntityArchive) BaseStorage.instance.createSubmission(workspace, filename);
			}
			else {
				ISubmission pending = BaseStorage.instance.createPendingSubmission(workspace, filename);
				ret.add(new Tuple<>(submission, pending));
				return (EntityArchive) pending;
			}
		}
		catch (WorkspaceUnsupportedException e) {
			e.printStackTrace();
		}
		return null;
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

	private static void storeArchive(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityWorkspace workspace, String filename, String extension, byte[] fileContent,
			boolean archiveHasManySubmissions, List<ITuple<ISubmission, ISubmission>> ret) {
		try {
			ArchiveInputStream archiveInputStream;
			ArchiveEntry archiveEntry;

			switch (extension) {
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

			EntityArchive submission = null;
			EntityArchive curArchive;
			Map<String, EntityArchive> multiSubmissionMap = null;

			if (archiveHasManySubmissions) {
				multiSubmissionMap = new HashMap<>();
			}
			else {
				submission = createSubmission(workspace, filename, ret);

				if (submission == null) {
					return;
				}
			}

			while (archiveEntry != null) {
				if (!archiveEntry.isDirectory()) {
					String[] parts = FilenameUtils.separatorsToUnix(archiveEntry.getName()).split("/");

					if (parts.length > (archiveHasManySubmissions ? 1 : 0)) {
						if (archiveHasManySubmissions) {
							curArchive = multiSubmissionMap.getOrDefault(parts[0], createSubmission(workspace, parts[0], ret));
							parts = Arrays.copyOfRange(parts, 1, parts.length - 1);
						}
						else {
							curArchive = submission;
						}

						for (String part : parts) {
							EntityArchive nextArchive = curArchive.getChildren_() == null ? null : curArchive.getChildren_().stream().filter(x -> x.getName().equals(part)).findAny().orElse(null);

							if (nextArchive == null) {
								nextArchive = new EntityArchive(part, curArchive);
								database.storeObject(nextArchive);
							}
							curArchive = nextArchive;
						}

						storeIndividualFile(database, filesystem, curArchive, archiveEntry.getName(), IOUtils.toByteArray(archiveInputStream));
					}
				}
				archiveEntry = archiveInputStream.getNextEntry();
			}

			archiveInputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static List<ITuple<ISubmission, ISubmission>> storeFile(EmbeddedDatabase database, BaseStorageFilesystem filesystem, IWorkspace workspace, String filename, byte[] fileContent,
			boolean archiveHasManySubmissions) throws WorkspaceUnsupportedException {

		if (!(workspace instanceof EntityWorkspace)) {
			throw new WorkspaceUnsupportedException("IWorkspace instanced passed is not supported by this IStorageWrapper implementation, only use one implementation at a time");
		}
		EntityWorkspace w = (EntityWorkspace) workspace;

		List<ITuple<ISubmission, ISubmission>> ret = new LinkedList<>();

		String ex = FilenameUtils.getExtension(filename);
		if (ex.equals("gz")) {
			filename = FilenameUtils.removeExtension(filename);
			ex = FilenameUtils.getExtension(filename);
			fileContent = handleGZip(fileContent);
		}

		if (Arrays.asList(archiveExs).contains(ex)) {
			storeArchive(database, filesystem, w, FilenameUtils.removeExtension(filename), ex, fileContent, archiveHasManySubmissions, ret);
		}
		else {
			EntityArchive s = createSubmission(w, FilenameUtils.getBaseName(filename), ret);
			storeIndividualFile(database, filesystem, s, FilenameUtils.getBaseName(filename), ex, fileContent);
		}

		database.refreshObject(w);

		return ret;
	}

	private static void storeIndividualFile(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityArchive archive, String filename, byte[] fileContent) {
		storeIndividualFile(database, filesystem, archive, filename, FilenameUtils.getExtension(filename), fileContent);
	}

	private static void storeIndividualFile(EmbeddedDatabase database, BaseStorageFilesystem filesystem, EntityArchive archive, String filename, String extension, byte[] fileContent) {
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

		EntityFile file = new EntityFile(archive, FilenameUtils.getBaseName(filename), extension, new Timestamp(System.currentTimeMillis()), fileContent.length, line, contentLine);
		if (!filesystem.storeFile(file, fileContent)) {
			return;
		}

		database.storeObject(file);
	}
}
