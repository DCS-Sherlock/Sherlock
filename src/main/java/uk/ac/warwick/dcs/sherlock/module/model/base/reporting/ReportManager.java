package uk.ac.warwick.dcs.sherlock.module.model.base.reporting;

import java.util.*;
import uk.ac.warwick.dcs.sherlock.api.model.AbstractReportGenerator;

/**
 * A class to handle report generation in general (does not generate reports itself).
 *
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for reports; sending
 * the relevant information to the actual report generator in use.
 *
 * very wip
 */

public class ReportManager {
	//Info to be stored one way or another. Not sure about format yet.
	//
	//files
	//line numbers (either a range or list)
	//column/where in line numbers (where necessary)
	//detection type
	//percentage/score/whatever
	//Variable names
	//Method names

	//All generated reports are stored in some manner. Currently the key is the file name but wip
	Map<String, String> reports;

	AbstractReportGenerator reportGenerator;

	ReportManager(AbstractReportGenerator reportGenerator) {
		this.reportGenerator = reportGenerator;
		reports = new HashMap<String, String>();
	}

	//Generate a report for a specified file, with some parameters that need to be defined
	void GenerateReport(String filename) {}
}
