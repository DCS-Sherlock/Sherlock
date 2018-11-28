package uk.ac.warwick.dcs.sherlock.engine.database;

import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.database.entities.DBFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.util.*;

public class EmbeddedDatabase implements IDatabaseWrapper {

	private EntityManagerFactory dbFactory;

	public EmbeddedDatabase() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.user", "admin");
		properties.put("javax.persistence.jdbc.password", "admin");

		this.dbFactory = Persistence.createEntityManagerFactory("objectdb:" + SherlockEngine.configuration.getData_Path() + File.separator + "Sherlock.odb", properties);

		this.trialDBAccess();
	}

	public void trialDBAccess() {
		EntityManager em = this.dbFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			//em.persist(new DBFile("txt"));
			em.getTransaction().commit();
		}
		finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();

			em.close();
		}
	}

	public void close() {
		this.dbFactory.close();
	}

}
