package uk.ac.warwick.dcs.sherlock.engine.storage.base.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class DBStudent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column (name="STUDENT_ID")
	private long id;

	private String identifier;

	public DBStudent() {
		this.identifier = "temporary";
	}

}
