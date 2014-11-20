package org.linuxkernel.proof.digger.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextExtract {

	private static List<String> _lines;
	private final static int _BLOCKWIDTH;
	private static int _threshold;
	private static String _html;
	@SuppressWarnings("unused")
	private static boolean _flag;
	private static int _start;
	private static int _end;
	private static StringBuilder _text;
	private static List<Integer> _indexDistribution;

	static {
		_lines = new ArrayList<>();
		_indexDistribution = new ArrayList<>();
		_text = new StringBuilder();
		_BLOCKWIDTH = 3;
		_flag = false;
	}


	public static String parse(String _html) {
		return parse(_html, false);
	}

	
	public static String parse(String html, boolean _flag) {
		TextExtract._html = html;
		processHTML();
		return getHTMLText();
	}

	private static void processHTML() {
		_html = _html.replaceAll("<!DOCTYPE.*?>", "");
		_html = _html.replaceAll("<!--.*?-->", ""); // remove html comment
		_html = _html.replaceAll("<script.*?>.*?</script>", ""); // remove
																	// javascript
		_html = _html.replaceAll("<style.*?>.*?</style>", ""); // remove css
		_html = _html.replaceAll("&.{2,5};|&#.{2,5};", ""); // remove special
															// char
		_html = _html.replaceAll("<.*?>", "");
	}

	private static String getHTMLText() {
		_lines = Arrays.asList(_html.split("\r\n"));
		_indexDistribution.clear();

		for (int i = 0; i < _lines.size() - _BLOCKWIDTH; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + _BLOCKWIDTH; j++) {
				_lines.set(j, _lines.get(j).replaceAll("\\s+", ""));
				wordsNum += _lines.get(j).length();
			}
			_indexDistribution.add(wordsNum);
		}

		_start = -1;
		_end = -1;
		boolean boolstart = false, boolend = false;
		_text.setLength(0);

		for (int i = 0; i < _indexDistribution.size() - 1; i++) {
			if (_indexDistribution.get(i) > _threshold && !boolstart) {
				if (_indexDistribution.get(i + 1).intValue() != 0 || _indexDistribution.get(i + 2).intValue() != 0
						|| _indexDistribution.get(i + 3).intValue() != 0) {
					boolstart = true;
					_start = i;
					continue;
				}
			}
			if (boolstart) {
				if (_indexDistribution.get(i).intValue() == 0 || _indexDistribution.get(i + 1).intValue() == 0) {
					_end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				for (int ii = _start; ii <= _end; ii++) {
					if (_lines.get(ii).length() < 5) {
						continue;
					}
					tmp.append(_lines.get(ii)).append("\n");
				}
				String str = tmp.toString();
				if (str.contains("Copyright") || str.contains("版权所有")) {
					continue;
				}
				_text.append(str);
				boolstart = boolend = false;
			}
		}
		return _text.toString();
	}
}