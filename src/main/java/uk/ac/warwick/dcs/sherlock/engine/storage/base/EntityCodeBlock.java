package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;
import java.util.stream.*;

@Entity (name = "CodeBlock")
public class EntityCodeBlock implements ICodeBlock, Serializable {

	private static final long serialVersionUID = 1L;

	private EntityCodeBlockGroup group;

	private EntityFile file;
	private float score;

	private int size;
	private List<Integer> lines;

	EntityCodeBlock() {
		super();
	}

	EntityCodeBlock(EntityCodeBlockGroup group, EntityFile file, float score, ITuple<Integer, Integer> lines) {
		super();
		this.group = group;
		this.file = file;
		this.score = score;

		this.size = 0;
		this.lines = new ArrayList<>();
		this.addLineToList(lines);
	}

	EntityCodeBlock(EntityCodeBlockGroup group, EntityFile file, float score, List<ITuple<Integer, Integer>> lines) {
		super();
		this.group = group;
		this.file = file;
		this.score = score;

		this.size = 0;
		this.lines = new ArrayList<>();
		lines.forEach(this::addLineToList);
	}

	@Override
	public float getBlockScore() {
		return this.score;
	}

	@Override
	public ISourceFile getFile() {
		return this.file;
	}

	@Override
	public List<ITuple<Integer, Integer>> getLineNumbers() {
		return IntStream.range(0, this.size).mapToObj(this::getLineFromList).collect(Collectors.toList());
	}

	void append(float score, ITuple<Integer, Integer> lines) {
		this.score = ((this.score * this.size) + score) / (this.size + 1); //new avg score
		this.addLineToList(lines);
	}

	void append(float score, List<ITuple<Integer, Integer>> lines) {
		this.score = ((this.score * this.size) + score) / (this.size + lines.size()); //new avg score
		lines.forEach(this::addLineToList);
	}

	void markRemove() {
		this.size = -5;
	}

	private void addLineToList(ITuple<Integer, Integer> line) {
		if (line == null || line.getKey() == null || line.getValue() == null) {
			BaseStorage.logger.warn("Null line tuple added to code block");
			return;
		}

		if (this.size == 0 || this.getLineNumbers().stream().noneMatch(line::equals)) {
			this.lines.add(line.getKey());
			this.lines.add(line.getValue());
			this.size++;
		}
	}

	private ITuple<Integer, Integer> getLineFromList(int index) {
		return new Tuple<>(this.lines.get(index * 2), this.lines.get((index * 2) + 1));
	}
}
