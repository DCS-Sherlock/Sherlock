package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;

public abstract class AbstractReportGenerator {
	String descriptionFileName;

	public AbstractReportGenerator() {
	}

	public AbstractReportGenerator(String descriptionFileName) {
		this.descriptionFileName = descriptionFileName;
	}

	public abstract String GenerateReport(List<? extends ICodeBlockPair> codeBlockPairs);
}
