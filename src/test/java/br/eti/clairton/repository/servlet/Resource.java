package br.eti.clairton.repository.servlet;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;

import br.eti.clairton.repository.AttributeBuilder;

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

	private AttributeBuilder attributeBuilder;

	@PostConstruct
	public void init() {
		emf = Persistence.createEntityManagerFactory(UNIT_NAME);
		em = emf.createEntityManager();
		attributeBuilder = new AttributeBuilder(em);
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
	public Logger produceLogger(final InjectionPoint injectionPoint) {
		final Class<?> type = injectionPoint.getMember().getDeclaringClass();
		final String klass = type.getName();
		return LogManager.getLogger(klass);
	}

	@Produces
	public Mirror getMirror() {
		return mirror;
	}

	@Produces
	public AttributeBuilder getAttributeBuilder() {
		return attributeBuilder;
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
