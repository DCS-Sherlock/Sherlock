package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.*;

public class BaseStorageFilesystem {

	private static Logger logger = LoggerFactory.getLogger(BaseStorageFilesystem.class);

	/**
	 * Loads a file from the filesystem
	 *
	 * @param file the file to load
	 *
	 * @return inputstream of the file content
	 */
	InputStream loadFile(EntityFile file) {
		return this.loadStorableIS(file, this.computeFileIdentifier(file));
	}

	/**
	 * Loads a file from the filesystem
	 *
	 * @param file the file to load
	 *
	 * @return string of the file content
	 */
	String loadFileAsString(EntityFile file) {
		return this.loadStorableStr(file, this.computeFileIdentifier(file));
	}

	/**
	 * Loads a tasks raw results from the filesystem
	 *
	 * @param task
	 */
	void loadTaskRawResults(EntityTask task) {
		try {
			InputStream in = this.loadStorableIS(task, this.computeTaskIdentifier(task));
			ObjectInputStream objectinputstream = new ObjectInputStream(in);
			List<AbstractModelTaskRawResult> rawResults = (List<AbstractModelTaskRawResult>) objectinputstream.readObject();
			task.setRawResultsNoStore(rawResults);
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String computeTaskIdentifier(EntityTask task) {
		String str = task.getJob().getPersistentId() + "." + task.getPersistentId() + "-" + task.getTimestamp().getTime();
		str = StringUtils.rightPad(str, 1024, str);
		return DigestUtils.sha512Hex(str.substring(0, 1024));
	}

	private String loadStorableStr(IStorable storable, String identfier) {
		byte[] b = this.loadStorable(storable, identfier);
		if (b == null) {
			return null;
		}
		return StringUtils.toEncodedString(b, StandardCharsets.UTF_8);
	}

	private InputStream loadStorableIS(IStorable storable, String identfier) {
		byte[] b = this.loadStorable(storable, identfier);
		if (b == null) {
			return null;
		}
		return new ByteArrayInputStream(b);
	}

	/**
	 * Main method to load a file from the filestore
	 */
	private byte[] loadStorable(IStorable storable, String identfier) {
		File fileToLoad = this.getFileFromIdentifier(identfier);
		if (!fileToLoad.exists()) {
			logger.error("File not in storage");
			return null;
		}

		try {
			byte[] rawContent = FileUtils.readFileToByteArray(fileToLoad);

			if (storable.getSecureParam() != null) {
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, this.getKey(storable), new IvParameterSpec(storable.getSecureParam()));
				rawContent = cipher.doFinal(rawContent);
			}

			if (!storable.getHash().equals(DigestUtils.sha512Hex(rawContent))) {
				logger.error("File loaded does not match stored hash, aborting");
				return null;
			}

			return rawContent;
		}
		catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException e) {
			logger.error("Error reading file", e);
		}

		return null;
	}

	private File getFileFromIdentifier(String fileIdentifier) {
		String path = SherlockEngine.configuration.getDataPath() + File.separator + "Store" + File.separator + this.computeLocator(fileIdentifier);
		return new File(path);
	}

	private String computeLocator(String fileIdentifier) {
		return fileIdentifier.substring(0, 2) + File.separator + fileIdentifier.substring(2, 4) + File.separator + fileIdentifier;
	}

	private SecretKey getKey(IStorable storable) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(storable.getHash().toCharArray(), String.format("%08d", storable.getTimestamp().getTime() % 100000000).getBytes(), 65536, 192);
			SecretKey tmp = factory.generateSecret(spec);
			return new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			logger.error("Error generating security key", e);
		}
		return null;
	}

	/**
	 * Stores a file on the filesystem
	 *
	 * @param file
	 * @param fileContent
	 *
	 * @return successful
	 */
	boolean storeFile(EntityFile file, byte[] fileContent) {
		try {
			return this.storeStorable(file, this.computeFileIdentifier(file), fileContent);
		}
		catch (NoSuchPaddingException | InvalidParameterSpecException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Stores a tasks raw results on the filesystem
	 *
	 * @param task
	 *
	 * @return
	 */
	boolean storeTaskRawResults(EntityTask task) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(task.getRawResults());
			oos.close();

			return this.storeStorable(task, this.computeTaskIdentifier(task), baos.toByteArray());
		}
		catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Main method to store data to a file in the filestore
	 */
	private boolean storeStorable(IStorable storable, String identifier, byte[] content)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidParameterSpecException, IOException, BadPaddingException, IllegalBlockSizeException {
		storable.setHash(DigestUtils.sha512Hex(content));

		File fileToStore = this.getFileFromIdentifier(identifier);
		if (fileToStore.exists()) {
			logger.error("File storage collision, file not stored");
			return false;
		}

		if (SherlockEngine.configuration.getEncryptFiles()) {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, this.getKey(storable));
			AlgorithmParameters params = cipher.getParameters();

			storable.setSecureParam(params.getParameterSpec(IvParameterSpec.class).getIV());
			FileUtils.writeByteArrayToFile(fileToStore, cipher.doFinal(content));
		}
		else {
			FileUtils.writeByteArrayToFile(fileToStore, content);
		}

		return true;
	}

	List<Object> validateFileStore(List<EntityFile> allFiles) {
		String parentDir = SherlockEngine.configuration.getDataPath() + File.separator + "Store";

		List<String> filesInStore;
		try {
			filesInStore = FileUtils.listFiles(new File(parentDir), null, true).parallelStream().map(x -> x.getAbsolutePath().substring(parentDir.length() + 1)).collect(Collectors.toList());
		}
		catch (Exception e) {
			return null;
		}

		List<Object> orphanRecords = new LinkedList<>();
		for (EntityFile f : allFiles) {
			String tmp = this.computeLocator(this.computeFileIdentifier(f));
			if (filesInStore.contains(tmp)) {
				filesInStore.remove(tmp);
			}
			else {
				orphanRecords.add(f);
			}
		}

		// Task check, disabled
		/*for (EntityTask t : allTasks) {
			String tmp = this.computeLocator(this.computeTaskIdentifier(t));
			if (filesInStore.contains(tmp)) {
				filesInStore.remove(tmp);
			}
			else {
				orphanRecords.add(t);
				logger.warn("Task in database has no results stored");
			}
		}*/

		if (orphanRecords.size() > 0) {
			logger.warn("File in database but not found in file store, removing...");
		}

		if (filesInStore.size() > 0) {
			logger.warn("Files in store which are not found in database, removing...");
			for (String s : filesInStore) {
				new File(SherlockEngine.configuration.getDataPath() + File.separator + "Store" + File.separator + s).delete();
			}
		}

		return orphanRecords;
	}

	private String computeFileIdentifier(EntityFile file) {
		String str = this.getArchiveName(file.getArchive()) + file.getFilename() + file.getExtension() + file.getTimestamp().getTime();
		str = StringUtils.rightPad(str, 1024, str);
		return DigestUtils.sha512Hex(str.substring(0, 1024));
	}

	private String getArchiveName(EntityArchive archive) {
		return archive != null ? archive.getFilename() + this.getArchiveName(archive.getParent()) : "";
	}

	interface IStorable {

		String getHash();

		void setHash(String hash);

		byte[] getSecureParam();

		void setSecureParam(byte[] secure);

		Timestamp getTimestamp();
	}
}
