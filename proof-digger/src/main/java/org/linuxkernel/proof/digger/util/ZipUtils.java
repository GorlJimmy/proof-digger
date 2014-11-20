package org.linuxkernel.proof.digger.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ZipUtils.class);

	private ZipUtils() {
	}

	public static void unZip(String jar, String subDir, String loc,
			boolean force) {
		try {
			File base = new File(loc);
			if (!base.exists()) {
				base.mkdirs();
			}

			@SuppressWarnings("resource")
			ZipFile zip = new ZipFile(new File(jar));
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.startsWith(subDir)) {
					continue;
				}
				name = name.replace(subDir, "").trim();
				if (name.length() < 2) {
					LOG.debug(name + "to short");
					continue;
				}
				if (entry.isDirectory()) {
					File dir = new File(base, name);
					if (!dir.exists()) {
						dir.mkdirs();
						LOG.debug("create dirtory");
					} else {
						LOG.debug("the dirtory exsits");
					}
					LOG.debug(name + " is dirtory");
				} else {
					File file = new File(base, name);
					if (file.exists() && force) {
						file.delete();
					}
					if (!file.exists()) {
						InputStream in = zip.getInputStream(entry);
						Files.copy(in, file.toPath());
						LOG.debug("create file");
					} else {
						LOG.debug("the dirtory exsits");
					}
					LOG.debug(name + " is not dirtory");
				}
			}
		} catch (ZipException ex) {
			LOG.error("unZip failed: ", ex);
		} catch (IOException ex) {
			LOG.error("unZip failed: ", ex);
		}
	}

	public static void createZip(String sourcePath, String zipPath) {
		FileOutputStream fileOutputStream = null;
		ZipOutputStream zipOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(zipPath);
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			writeZip(new File(sourcePath), "", zipOutputStream);
		} catch (FileNotFoundException e) {
			LOG.error("create ZIP file failed: ", e);
		} finally {
			try {
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
			} catch (IOException e) {
				LOG.error("create ZIP file failed: ", e);
			}

		}
	}

	private static void writeZip(File file, String parentPath,
			ZipOutputStream zipOutputStream) {
		if (file.exists()) {
			if (file.isDirectory()) {

				parentPath += file.getName() + File.separator;
				File[] files = file.listFiles();
				for (File f : files) {
					writeZip(f, parentPath, zipOutputStream);
				}
			} else {
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);
					ZipEntry ze = new ZipEntry(parentPath + file.getName());
					zipOutputStream.putNextEntry(ze);
					byte[] content = new byte[1024];
					int len;
					while ((len = fileInputStream.read(content)) != -1) {
						zipOutputStream.write(content, 0, len);
						zipOutputStream.flush();
					}

				} catch (FileNotFoundException e) {
					LOG.error("create ZIP file failed: ", e);
				} catch (IOException e) {
					LOG.error("create ZIP file failed: ", e);
				} finally {
					try {
						if (fileInputStream != null) {
							fileInputStream.close();
						}
					} catch (IOException e) {
						LOG.error("create ZIP file failed: ", e);
					}
				}
			}
		}
	}
}