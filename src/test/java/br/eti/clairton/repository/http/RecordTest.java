package br.eti.clairton.repository.http;

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
import static br.eti.clairton.repository.http.Record.valueOf;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RecordTest {

	@Test
	public void testIgual() {
		assertEquals(EQUAL, valueOf("abc").comparator);
	}

	@Test
	public void testIgualComSimbolo() {
		assertEquals(EQUAL, valueOf("==abc").comparator);
	}

	@Test
	public void testIgualNaoSensitive() {
		assertEquals(EQUAL_IGNORE_CASE, valueOf("=*abc").comparator);
	}

	@Test
	public void testDiferente() {
		assertEquals(NOT_EQUAL, valueOf("<>abc").comparator);
	}

	@Test
	public void testExiste() {
		assertEquals(EXIST, valueOf("∃").comparator);
	}

	@Test
	public void testNaoNulo() {
		assertEquals(NOT_NULL, valueOf("!∅" ).comparator);
	}

	@Test
	public void testNulo() {
		assertEquals(NULL, valueOf("∅").comparator);
	}

	@Test
	public void testMaior() {
		assertEquals(GREATER_THAN, valueOf(">45").comparator);
	}

	@Test
	public void testMaiorOuIgual() {
		assertEquals(GREATER_THAN_OR_EQUAL, valueOf(">=45").comparator);
	}

	@Test
	public void testMenorOuIgual() {
		assertEquals(LESS_THAN_OR_EQUAL, valueOf("<=45").comparator);
	}

	@Test
	public void testMenor() {
		assertEquals(LESS_THAN, valueOf("<45").comparator);
	}

	@Test
	public void testTeminaCom() {
		assertEquals(ENDS_WITH, Record.valueOf("$bar").comparator);
	}

	@Test
	public void testComecaCom() {
		assertEquals(STARTS_WITH, Record.valueOf("^foo").comparator);
	}

}
