package br.eti.clairton.repository.http;

import static br.eti.clairton.repository.Comparators.EQUAL;
import static br.eti.clairton.repository.Comparators.values;
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;
import static java.util.regex.Pattern.compile;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import br.eti.clairton.repository.Comparator;

public class Record {
	private static final Logger logger = getLogger(Record.class.getSimpleName());
	public static final Pattern escaper = compile("([^a-zA-z0-9])");
	public final Object value;
	public final Comparator comparator;


	public Record(final Object value, final Comparator comparator) {
		this.value = value;
		this.comparator = comparator;
	}
	
	public static Record valueOf(final String[] values) {
		if (values.length == 1) {
			return valueOf(values[0]);
		} else {
			return new Record(values, EQUAL);
		}		
	}
	
	public static Record valueOf(final String value) {
		for (final Comparator comparator : values()) {
			final String expression = escaper.matcher(comparator.toString()).replaceAll("\\\\$1").replace("^", "\\^");
			final String regex = "^" + expression + ".*";
			logger.log(FINE, regex);
			if (value.matches(regex)) {
				return new Record(value.replaceAll(expression, ""), comparator);
			}
		}
		return new Record(value, EQUAL);
	}
}