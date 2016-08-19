package br.eti.clairton.repository.servlet;

import br.eti.clairton.repository.Comparator;

class Record {
	public final Object value;
	public final Comparator comparator;

	public Record(final Object value, final Comparator comparator) {
		this.value = value;
		this.comparator = comparator;
	}
}