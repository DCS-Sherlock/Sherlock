package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings ("Duplicates")
@Entity (name = "CodeBlockGroup")
public class EntityCodeBlockGroup implements ICodeBlockGroup, Serializable {

	private static final long serialVersionUID = 1L;

	private DetectionType type;
	private String comment;

	private Map<Long, EntityCodeBlock> blockMap;

	public EntityCodeBlockGroup() {
		super();
		this.type = null;
		this.comment = null;
		this.blockMap = new HashMap<>();
	}

	@Override
	public void addCodeBlock(ISourceFile file, float score, ITuple<Integer, Integer> line) {
		if (this.blockMap.containsKey(file.getPersistentId())) {
			this.blockMap.get(file.getPersistentId()).append(score, line);
		}
		else {
			if (file instanceof EntityFile) {
				EntityCodeBlock block = new EntityCodeBlock((EntityFile) file, score, line);
				this.blockMap.put(file.getPersistentId(), block);
			}
			else {
				BaseStorage.logger.error("Could not add file {}, it is not from the database", file.getFileDisplayName());
			}
		}
	}

	@Override
	public void addCodeBlock(ISourceFile file, float score, List<ITuple<Integer, Integer>> lines) {
		if (this.blockMap.containsKey(file.getPersistentId())) {
			this.blockMap.get(file.getPersistentId()).append(score, lines);
		}
		else {
			if (file instanceof EntityFile) {
				EntityCodeBlock block = new EntityCodeBlock((EntityFile) file, score, lines);
				this.blockMap.put(file.getPersistentId(), block);
			}
			else {
				BaseStorage.logger.error("Could not add file {}, it is not from the database", file.getFileDisplayName());
			}
		}
	}

	@Override
	public List<? extends ICodeBlock> getCodeBlocks() {
		return new LinkedList<>(this.blockMap.values());
	}

	@Override
	public ICodeBlock getCodeBlock(ISourceFile file) {
		return this.blockMap.get(file.getPersistentId());
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public DetectionType getDetectionType() {
		return this.type;
	}

	@Override
	public void setDetectionType(DetectionType type) {
		this.type = type;
	}
}
