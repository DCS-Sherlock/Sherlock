package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultFile;
import uk.ac.warwick.dcs.sherlock.engine.component.IResultJob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.*;

@Entity (name = "ResultJob")
public class EntityResultJob implements IResultJob, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	private List<EntityResultFile> fileResults;

	EntityResultJob() {
		super();
		this.fileResults = Collections.synchronizedList(new LinkedList<>());
	}

	@Override
	public IResultFile addFile(ISourceFile file) {
		// Check file not in fileResults first

		if (file instanceof EntityFile) {
			EntityResultFile f = new EntityResultFile((EntityFile) file);
			this.fileResults.add(f);
			return f;
		}

		return null;
	}

	@Override
	public List<IResultFile> getFileResults() {
		return new LinkedList<>(this.fileResults);
	}

	@Override
	public long getPersistentId() {
		return this.id;
	}

	@Override
	public void remove() {
		for (EntityResultFile f : this.fileResults) {
			f.remove();
		}
		BaseStorage.instance.database.removeObject(this);
	}

	@Override
	public void store() {
		List<Object> list = new LinkedList<>();
		this.fileResults.forEach(f -> list.addAll(f.store()));
		list.add(this);
		BaseStorage.instance.database.storeObject(list);
	}
}
