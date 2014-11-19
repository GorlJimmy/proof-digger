package org.linuxkernel.proof.digger.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;
import org.ansj.util.MyStaticValue;
import org.linuxkernel.proof.digger.config.Config;
import org.linuxkernel.proof.digger.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordSegment {

	private static final Logger LOG = LoggerFactory.getLogger(WordSegment.class);

	static {
		LOG.info("setting default path of dictionary");
		String appPath = Tools.getAppPath(WordSegment.class);
		String userLibrary = appPath + Config.DEFAULT_DIC;
		LOG.info("default.dic：" + userLibrary);
		String ambiguityLibrary = appPath + Config.AMBIGUITY_DIC;
		LOG.info("ambiguity.dic：" + ambiguityLibrary);
		// load dic
		MyStaticValue.userLibrary = userLibrary;
		MyStaticValue.ambiguityLibrary = ambiguityLibrary;

		HashMap<String, String> updateDic = FilterModifWord.getUpdateDic();
		updateDic.put("　", "_stop");
		updateDic.put("#", "_stop");

		String path = appPath + Config.COUSTOM_DIC;
		LOG.info("custom dic dirtory " + path);
		File dir = new File(path);
		File[] files = null;
		if (dir.isDirectory()) {
			files = dir.listFiles();
		} else {
			LOG.error("custom dic dirtory not exsits" + path);
		}
		for (File file : files) {
			BufferedReader reader = null;
			try {
				InputStream in = new FileInputStream(file);
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.equals("") || line.startsWith("#") || line.startsWith("//")) {
						continue;
					}
					String[] split = line.split("\\t+");
					if (split != null && split.length == 3) {
						String keyword = split[0].trim();
						String nature = split[1].trim();
						String freq = split[2].trim();
						updateDic.put(keyword, nature);
						UserDefineLibrary.insertWord(keyword, nature, Integer.parseInt(freq));
					} else {
						LOG.error("custom dic error: " + line);
					}
				}
			} catch (IOException e) {
				LOG.error("reading error: ", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOG.error("close file error: ", e);
					}
				}
			}
		}
	}

	public static List<Term> parse(String str) {
		List<Term> terms = ToAnalysis.parse(str);
		new NatureRecognition(terms).recognition();
		terms = FilterModifWord.modifResult(terms);
		return terms;
	}

}