package uk.ac.warwick.dcs.sherlock.engine.storage.base;

import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.storage.base.entities.DBFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.util.*;

public class EmbeddedDatabase {

	private EntityManagerFactory dbFactory;
	private EntityManager em;

	public EmbeddedDatabase() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.user", "admin");
		properties.put("javax.persistence.jdbc.password", "admin");

		this.dbFactory = Persistence.createEntityManagerFactory("objectdb:" + SherlockEngine.configuration.getDataPath() + File.separator + "Sherlock.odb", properties);
		this.em = this.dbFactory.createEntityManager();

		//this.trialDBAccess();
	}

	public void storeFile(DBFile file) {
		try {
			em.getTransaction().begin();
			em.persist(file);
			em.getTransaction().commit();
		}
		finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		}
	}

	public List<DBFile> getAllFiles() {
		List<DBFile> q = null;
		em.getTransaction().begin();
		q = em.createQuery("SELECT f FROM DBFile f", DBFile.class).getResultList();
		em.getTransaction().commit();
		return q;
	}

	public void removeFiles(List<DBFile> orphans) {
		try {
			em.getTransaction().begin();
			for (DBFile orphan : orphans) {
				em.remove(orphan);
			}
			em.getTransaction().commit();
		}
		finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		}
	}

	public void close() {
		this.em.close();
		this.dbFactory.close();
	}

}
