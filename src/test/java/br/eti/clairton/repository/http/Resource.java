package br.eti.clairton.repository.http;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

import org.mockito.Mockito;

import net.vidageek.mirror.dsl.Mirror;

/**
 * Produz os recursos.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@ApplicationScoped
public class Resource {
	public static final String TENANT = "valorQueNãoPodeAparecer";
	public static final String UNIT_NAME = "default";

	public static final String ENVIROMENT_PARAM = "br.com.caelum.vraptor.environment";

	private final Mirror mirror = new Mirror();

	private EntityManagerFactory emf;

	private EntityManager em;

	@PostConstruct
	public void init() {
		emf = Persistence.createEntityManagerFactory(UNIT_NAME);
		em = emf.createEntityManager();
	}

	@Produces
	public EntityManager getEm() {
		return em;
	}

	@Produces
	public Metamodel getMetamodel() {
		return em.getMetamodel();
	}

	@Produces
	public Cache getCache() {
		return Mockito.mock(Cache.class);
	}

	@Produces
	public Mirror getMirror() {
		return mirror;
	}

	@Produces
	@ApplicationScoped
	public Connection getConnection(final @Default EntityManager em) {
		try {
			/*
			 * O hibernate não implementa o entityManager de forma a recuperar a
			 * o connection
			 */
			final Class<?> klass = Class
					.forName("org.hibernate.internal.SessionImpl");
			final Object session = em.unwrap(klass);
			return (Connection) klass.getDeclaredMethod("connection").invoke(
					session);
		} catch (final Exception e) {
			return em.unwrap(Connection.class);
		}
	}
}
