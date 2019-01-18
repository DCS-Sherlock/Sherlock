package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

public interface IResultJob {

	IResultFile addFile(ISourceFile file);

	List<IResultFile> getFileResults();

}
