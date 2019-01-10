package uk.ac.warwick.dcs.sherlock.api.report;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockPair;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class AbstractReportGenerator {

	/**
	 * The unformatted descriptions are stored in this map of strings
	 */
	protected Map<DetectionType, String> baseDescriptions;

	public AbstractReportGenerator(String descriptionFileName) {
		baseDescriptions = new HashMap<DetectionType, String>();

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(descriptionFileName))) {
			String currentLine = bufferedReader.readLine();
			DetectionType currentType = DetectionType.COMMENT;

			while (currentLine != null) {
				//Check the line isn't commented out and isn't empty
				if (!currentLine.startsWith("//") && currentLine != "") {
					if (currentLine.startsWith("::")) {
						//"::" at the start of the line indicates what DetectionType the next set of lines will be
						String typeString = currentLine.replace("::", "");
						currentType = DetectionType.valueOf(typeString);
					}
					else if (baseDescriptions.containsKey(currentType)) {
						//If there's already a partial description, append the current line to it
						StringJoiner stringJoiner = new StringJoiner("\n");
						stringJoiner.add(baseDescriptions.get(currentType));
						stringJoiner.add(currentLine);

						String newDescription = stringJoiner.toString();
						baseDescriptions.replace(currentType, newDescription);
					}
					else {
						baseDescriptions.put(currentType, currentLine);
					}
				}

				currentLine = bufferedReader.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract String GenerateReport(List<? extends ICodeBlockPair> codeBlockPairs);
}
