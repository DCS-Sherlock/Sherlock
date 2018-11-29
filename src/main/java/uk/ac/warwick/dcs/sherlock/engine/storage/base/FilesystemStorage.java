package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

public class FilesystemStorage{

	private static Logger logger = LoggerFactory.getLogger(FilesystemStorage.class);

	/**
	 * Stores a file on the filesystem
	 * @param file
	 * @param fileContent
	 * @return hash of the stored file, for verification
	 */
	boolean storeFile(DBFile file, byte[] fileContent) {
		file.setHash(DigestUtils.sha512Hex(fileContent));

		File fileToStore = this.getFileFromIdentifier(this.computeFileIdentifier(file));
		if (fileToStore.exists()) {
			logger.error("File storage collision, file not stored");
			return false;
		}

		try {
			if (SherlockEngine.configuration.getEncryptFiles()) {
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, this.getKey(file));
				AlgorithmParameters params = cipher.getParameters();

				file.setSecureParam(params.getParameterSpec(IvParameterSpec.class).getIV());
				FileUtils.writeByteArrayToFile(fileToStore, cipher.doFinal(fileContent));
			}
			else {
				FileUtils.writeByteArrayToFile(fileToStore,fileContent);
			}
		}
		catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidParameterSpecException | IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Error writing file", e);
			return false;
		}

		return true;
	}

	/**
	 * Loads a file from the filesystem
	 * @param file
	 * @return
	 */
	InputStream loadFile(DBFile file) {
		File fileToLoad = this.getFileFromIdentifier(this.computeFileIdentifier(file));
		if (!fileToLoad.exists()) {
			logger.error("File not in storage");
			return null;
		}

		InputStream fileContent = null;
		try {
			byte[] rawContent = FileUtils.readFileToByteArray(fileToLoad);

			if (file.getSecureParam() != null) {
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, this.getKey(file), new IvParameterSpec(file.getSecureParam()));
				//logger.info(new String(cipher.doFinal(rawContent), StandardCharsets.UTF_8));
				fileContent = new ByteArrayInputStream(cipher.doFinal(rawContent));
			}
			else {
				fileContent = new ByteArrayInputStream(rawContent);
			}
		}
		catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException e) {
			logger.error("Error reading file", e);
		}

		return fileContent;
	}

	private String computeFileIdentifier(DBFile file) {
		String str = file.getFilename() + file.getExtension() + file.getTimestamp().getTime();
		str = StringUtils.rightPad(str, 30, str);
		return DigestUtils.sha1Hex(str.substring(0, 30));
	}

	private File getFileFromIdentifier(String fileIdentifier) {
		String dirPath = SherlockEngine.configuration.getDataPath() + File.separator + "Store" + File.separator + fileIdentifier.substring(0, 2) + File.separator + fileIdentifier.substring(2,4);
		return new File(dirPath + File.separator + fileIdentifier);
	}

	private SecretKey getKey(DBFile file) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(file.getHash().toCharArray(), String.format("%08d", file.getTimestamp().getTime() % 100000000).getBytes(), 65536, 192);
			SecretKey tmp = factory.generateSecret(spec);
			return new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			logger.error("Error generating security key", e);
		}
		return null;
	}
}
