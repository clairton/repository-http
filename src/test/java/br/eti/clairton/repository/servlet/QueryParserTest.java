package br.eti.clairton.repository.servlet;

import static br.eti.clairton.repository.Comparators.ENDS_WITH;
import static br.eti.clairton.repository.Comparators.EQUAL;
import static br.eti.clairton.repository.Comparators.EQUAL_IGNORE_CASE;
import static br.eti.clairton.repository.Comparators.EXIST;
import static br.eti.clairton.repository.Comparators.GREATER_THAN;
import static br.eti.clairton.repository.Comparators.GREATER_THAN_OR_EQUAL;
import static br.eti.clairton.repository.Comparators.LESS_THAN;
import static br.eti.clairton.repository.Comparators.LESS_THAN_OR_EQUAL;
import static br.eti.clairton.repository.Comparators.NOT_EQUAL;
import static br.eti.clairton.repository.Comparators.NOT_NULL;
import static br.eti.clairton.repository.Comparators.NULL;
import static br.eti.clairton.repository.Comparators.STARTS_WITH;
import static br.eti.clairton.repository.Order.Direction.ASC;
import static br.eti.clairton.repository.Order.Direction.DESC;
import static br.eti.clairton.repository.servlet.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.eti.clairton.repository.Order;
import br.eti.clairton.repository.Predicate;

@RunWith(VRaptorRunner.class)
public class QueryParserTest {
	private @Inject QueryParser queryParser;

	@Before
	public void init() {
		// para registrar os converters
		navigate().get("/").execute();
	}
	
	@Test
	public void testInController() {
		VRaptorTestResult result = navigate()
									.get("/aplicacoes?nome=Gol&id=3&ids[]=1&ids[]=2&page=1&per_page=10&sort=nome&direction=ASC")
									.execute();
		result.wasStatus(200);
	}


	@Test
	public void testParseArray() {
		// nome[]=remove&nome[]=update
		final String[] values = new String[] { "remove", "update" };
		final Map<String, String[]> params = new HashMap<>();
		params.put("nome", values);
		final Collection<Predicate> predicates = queryParser.parse(params, Recurso.class);
		assertEquals(1, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateRemove = interator.next();
		assertEquals(Arrays.asList(values), predicateRemove.getValue());
		assertEquals("*", predicateRemove.getComparator().toString());
		assertEquals("nome", predicateRemove.getAttribute().getName());
	}

	@Test
	public void testOrder() {
		final Map<String, String[]> params = new HashMap<>();
		params.put("direction", new String[]{"asc", "desc"});
		params.put("sort", new String[]{"nome", "aplicacao.id", "aplicacao.nome"});
		final List<Order> orders = queryParser.order(params, Recurso.class);
		assertEquals(3, orders.size());
		assertEquals(ASC, orders.get(0).getDirection());
		assertEquals(DESC, orders.get(1).getDirection());
		assertEquals(ASC, orders.get(2).getDirection());
		assertEquals(1, orders.get(0).getAttributes().size());
		assertEquals(2, orders.get(1).getAttributes().size());
	}

	@Test
	public void testIds() {
		final Map<String, String[]> params = new HashMap<>();
		params.put("ids[]", new String[]{">=0", "<>100", "<1000"});
		final Collection<Predicate> predicates = queryParser.parse(params, Recurso.class);
		assertEquals(3, predicates.size());
	}

	@Test
	public void testArray() {
		final Map<String, String[]> params = new HashMap<>();
		params.put("id[]", new String[]{">=0", "<>100", "<1000"});
		final Collection<Predicate> predicates = queryParser.parse(params, Recurso.class);
		assertEquals(3, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateMaiorQueZero = interator.next();
		assertEquals(">=", predicateMaiorQueZero.getComparator().toString());
		assertEquals("0", predicateMaiorQueZero.getValue());
		assertTrue(Recurso_.id.equals(predicateMaiorQueZero.getAttributes()[0]));
		final Predicate predicateDiferenteDe100 = interator.next();
		assertEquals("100", predicateDiferenteDe100.getValue());
		assertEquals("<>", predicateDiferenteDe100.getComparator().toString());
		assertTrue(Recurso_.id.equals(predicateDiferenteDe100.getAttributes()[0]));
		final Predicate predicateMenorQuer1000 = interator.next();
		assertEquals("1000", predicateMenorQuer1000.getValue());
		assertEquals("<", predicateMenorQuer1000.getComparator().toString());
		assertTrue(Recurso_.id.equals(predicateMenorQuer1000.getAttributes()[0]));
	}

	@Test
	public void testParseComplex() {
		final Map<String, String[]> params = new HashMap<>();
		params.put("aplicacao.nome", new String[]{"=*Pass"});
		params.put("aplicacao.id", new String[]{">=0"});
		final Collection<Predicate> predicates = queryParser.parse(params, Recurso.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("=*", predicateNome.getComparator().toString());
		assertEquals("Pass", predicateNome.getValue());
		assertTrue(Recurso_.aplicacao.equals(predicateNome.getAttributes()[0]));
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttributes()[1]));
		final Predicate predicateId = interator.next();
		assertEquals("0", predicateId.getValue());
		assertEquals(">=", predicateId.getComparator().toString());
		assertEquals("aplicacao", predicateId.getAttributes()[0].getName());
		assertEquals("id", predicateId.getAttributes()[1].getName());
	}

	@Test
	public void testParseSimple() {
		// nome=Pass&id=>=0
		final Map<String, String[]> params = new HashMap<>();
		params.put("nome", new String[]{"=*Pass"});
		params.put("id", new String[]{">=0"});
		final Collection<Predicate> predicates = queryParser.parse(params, Aplicacao.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("Pass", predicateNome.getValue());
		assertEquals("=*", predicateNome.getComparator().toString());
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttribute()));
		final Predicate predicateId = interator.next();
		assertEquals("0", predicateId.getValue());
		assertEquals(">=", predicateId.getComparator().toString());
		assertEquals("id", predicateId.getAttribute().getName());
	}

	@Test
	public void testIgual() {
		assertEquals(EQUAL, queryParser.to("abc" ).comparator);
	}

	@Test
	public void testIgualComSimbolo() {
		assertEquals(EQUAL, queryParser.to("==abc").comparator);
	}

	@Test
	public void testIgualNaoSensitive() {
		assertEquals(EQUAL_IGNORE_CASE, queryParser.to("=*abc").comparator);
	}

	@Test
	public void testDiferente() {
		assertEquals(NOT_EQUAL, queryParser.to("<>abc").comparator);
	}

	@Test
	public void testExiste() {
		assertEquals(EXIST, queryParser.to("∃").comparator);
	}

	@Test
	public void testNaoNulo() {
		assertEquals(NOT_NULL, queryParser.to("!∅" ).comparator);
	}

	@Test
	public void testNulo() {
		assertEquals(NULL, queryParser.to("∅").comparator);
	}

	@Test
	public void testMaior() {
		assertEquals(GREATER_THAN, queryParser.to(">45").comparator);
	}

	@Test
	public void testMaiorOuIgual() {
		assertEquals(GREATER_THAN_OR_EQUAL, queryParser.to(">=45").comparator);
	}

	@Test
	public void testMenorOuIgual() {
		assertEquals(LESS_THAN_OR_EQUAL, queryParser.to("<=45").comparator);
	}

	@Test
	public void testMenor() {
		assertEquals(LESS_THAN, queryParser.to("<45").comparator);
	}

	@Test
	public void testTeminaCom() {
		assertEquals(ENDS_WITH, queryParser.to("$bar").comparator);
	}

	@Test
	public void testComecaCom() {
		assertEquals(STARTS_WITH, queryParser.to("^foo").comparator);
	}
}
