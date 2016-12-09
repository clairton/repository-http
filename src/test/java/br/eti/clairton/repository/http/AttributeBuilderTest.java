package br.eti.clairton.repository.http;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(VRaptorRunner.class)
public class AttributeBuilderTest {

	@Inject
	private AttributeBuilder attributeBuilder;

	@Test
	public void testAdd() {
		final Attribute<?, ?>[] attributes = attributeBuilder
				.add(Operacao_.recurso).add(Recurso_.aplicacao)
				.add(Aplicacao_.nome).toArray();
		assertEquals(3, attributes.length);
	}

	@Test
	public void testWith3() {
		final Attribute<?, ?>[] attributes = attributeBuilder.with(
				Operacao.class, "recurso.aplicacao.nome");
		assertEquals(3, attributes.length);
	}

	@Test
	public void testWith2() {
		final Attribute<?, ?>[] attributes = attributeBuilder.with(
				Operacao.class, "recurso.nome");
		assertEquals(2, attributes.length);
	}

	@Test
	public void testWithColchete() {
		final Attribute<?, ?>[] attributes = attributeBuilder.with(
				Operacao.class, "recurso[aplicacao][nome]");
		assertEquals(3, attributes.length);
		// assertTrue(Operacao_.recurso.equals(attributes[1]));
		// assertTrue(Recurso_.aplicacao.equals(attributes[1]));
		// assertTrue(Aplicacao_.nome.equals(attributes[2]));
	}

	@Test
	public void testWithCollection() {
		final Attribute<?, ?>[] attributes = attributeBuilder.with(Aplicacao.class, "recursos.nome");
		assertEquals(2, attributes.length);
	}

	@Test
	public void testWith1() {
		final Attribute<?, ?>[] attributes = attributeBuilder.with(
				Operacao.class, "recurso");
		assertEquals(1, attributes.length);
	}
}
