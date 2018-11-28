package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.sql.Timestamp;
import java.util.*;

public class EmbeddedDatabase {

	private EntityManagerFactory dbFactory;

	public EmbeddedDatabase() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.user", "admin");
		properties.put("javax.persistence.jdbc.password", "admin");

		this.dbFactory = Persistence.createEntityManagerFactory("objectdb:" + SherlockEngine.configuration.getDataPath() + File.separator + "Sherlock.odb", properties);

		this.trialDBAccess();
	}

	public void trialDBAccess() {
		EntityManager em = this.dbFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(new DBFile("test", "txt", new Timestamp(System.currentTimeMillis()), "123"));
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
