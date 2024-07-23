
package com.jgw.heartsai.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Friday UI, and allows you to create a horizontal
 * table with individual horizontal cells that expand based on the size of the
 * content they contain.
 * 
 * The AnsiGridPanel contains the contents of each individual cell.
 * 
 * This class is ANSI-aware.
 */
public class AnsiGrid {

	private final List<AnsiGridPanel> panels = new ArrayList<>();

	public AnsiGridPanel createNewPanel() {
		AnsiGridPanel result = new AnsiGridPanel();
		panels.add(result);
		return result;
	}

	public String render(int padding) {

		List<List<String>> renderedPanel = new ArrayList<>();

		List<Integer> maxWidth = new ArrayList<Integer>();

		for (AnsiGridPanel agp : panels) {

			int width = agp.getMaxStrippedLength() + padding;

			maxWidth.add(width);
			List<String> panelContents = agp.renderWithWidth(width);
			renderedPanel.add(panelContents);

		}

		String result = "";
		boolean continueLoop = true;
		int lineNumber = 0;

		String[] currLineOutput = new String[renderedPanel.size()];
		while (continueLoop) {

			continueLoop = false;

			for (int panelNum = 0; panelNum < renderedPanel.size(); panelNum++) {
				List<String> currPanel = renderedPanel.get(panelNum);

				String lineContents = AnsiGridPanel.padWithSpaces("", maxWidth.get(panelNum));
				if (lineNumber < currPanel.size()) {
					lineContents = currPanel.get(lineNumber);
					continueLoop = true;
				}
				currLineOutput[panelNum] = lineContents;
			}

			for (String str : currLineOutput) {
				result += str;
			}
			result = result + "\n";

			lineNumber++;
		}

		return result;
	}

}
