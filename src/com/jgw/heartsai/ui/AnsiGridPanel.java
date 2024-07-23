
package com.jgw.heartsai.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.io.AnsiOutputStream;

/**
 * This class is part of the Friday UI, and allows you to create a horizontal
 * table with individual horizontal cells that expand based on the size of the
 * content they contain.
 * 
 * This class contains the contents of the individual cell of the table, which
 * is the packaged into a table by AnsiGrid.
 * 
 * This class is ANSI-aware.
 */
public class AnsiGridPanel {

	private final List<String> panelContents = new ArrayList<>();

	public AnsiGridPanel() {
	}

	public void addLineToPanel(Ansi ansiStr) {

		panelContents.add(ansiStr.toString());
	}

	public void addLineToPanel(String str) {

		panelContents.add(str);
	}

	public void addLinesToPanel(List<String> strList) {
		panelContents.addAll(strList);
	}

	public int getMaxStrippedLength() {

		List<Integer> l = panelContents.stream().map(e -> stripAnsi(e).toString().length()).sorted()
				.collect(Collectors.toList());

		if (l.isEmpty()) {
			return 0;
		}

		return l.get(l.size() - 1);

	}

	public List<String> renderWithWidth(int width) {
		List<String> result = new ArrayList<>();

		for (String line : panelContents) {

			line = padWithSpaces(line, width);
			result.add(line);

		}

		return result;

	}

	public static int getStrippedWidth(String str) {
		return stripAnsi(str).length();
	}

	public static String padWithSpaces(String line, int width) {
		while (getStrippedWidth(line) < width) {
			line = line + " ";
		}
		return line;

	}

	public static String stripAnsi(String str) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final AnsiOutputStream ansiOutput = new AnsiOutputStream(baos, null, AnsiMode.Strip, null, AnsiType.Emulation,
				AnsiColors.TrueColor, Charset.forName("UTF-8"), null, null, false);

		try {
			baos.write(str.getBytes());
			return baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
