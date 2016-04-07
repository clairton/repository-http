package br.eti.clairton.repository.vraptor;

import static br.eti.clairton.repository.Order.Direction.ASC;
import static br.eti.clairton.repository.Order.Direction.byString;
import static br.eti.clairton.repository.vraptor.Param.DIRECTION;
import static br.eti.clairton.repository.vraptor.Param.PAGE;
import static br.eti.clairton.repository.vraptor.Param.PER_PAGE;
import static br.eti.clairton.repository.vraptor.Param.SORT;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;
import javax.servlet.ServletRequest;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.repository.Comparator;
import br.eti.clairton.repository.Comparators;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Order;
import br.eti.clairton.repository.Order.Direction;
import br.eti.clairton.repository.Predicate;

@Dependent
public class QueryParser {
	private static final Logger logger = getLogger(QueryParser.class);

	private final List<String> query = asList(SORT, PAGE, PER_PAGE, DIRECTION);
	
	private final AttributeBuilder builder;

	private final Converters converters;

	private final Pattern escaper = compile("([^a-zA-z0-9])");

	@Deprecated
	protected QueryParser() {
		this(null, null);
	}

	@Inject
	public QueryParser(final AttributeBuilder attributeBuilder, final Converters converters) {
		super();
		this.builder = attributeBuilder;
		this.converters = converters;
	}

	public Collection<Predicate> parse(final ServletRequest request, final Class<? extends Model> modelType) {
		final Collection<Predicate> predicates = new ArrayList<Predicate>();
		final Enumeration<String> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			final String field = parameters.nextElement();
			if (query.contains(field) || "format".equals(field)) {
				continue;
			}
			final Collection<Predicate> predicate;
			final Attribute<?, ?>[] attrs = builder.with(modelType, "ids[]".equals(field) ? "id" : field);
			if(attrs.length == 1 && attrs[0] == null){
				logger.warn("Attribute {}#{} not found", modelType, field);
				continue;
			}
			final String[] values = request.getParameterValues(field);
			predicate = to(attrs, values);
			predicates.addAll(predicate);
		}
		return predicates;
	}
	
	public List<Order> order(final ServletRequest request, final Class<? extends Model> modelType){
		final Map<String, String[]> params;
		if (request.getParameterMap() != null) {
			params = request.getParameterMap();
		} else {
			params = new HashMap<String, String[]>();
		}
		final String[] sort;
		final String[] orderBy;
		if (params.containsKey(DIRECTION)) {
			sort = params.get(DIRECTION);
		} else {
			sort = new String[]{"asc"};
		}
		if (params.containsKey(SORT)) {
			orderBy = params.get(SORT);
		} else {
			orderBy = new String[]{"id"};
		}
		final List<Order> orders = new ArrayList<Order>();
		for(int i = 0, j = orderBy.length; i < j; i++){
			final String field = orderBy[i];
			final Attribute<?, ?>[] attrs = builder.with(modelType, field);
			Direction type;
			try{
				type = byString(sort[i]);
			}catch(ArrayIndexOutOfBoundsException e){
				type = ASC;
			}
			final Order order = new Order(type, attrs);
			orders.add(order);
		}
		return orders;
	}

	public Page paginate(final ServletRequest request, final Class<? extends Model> modelType) {
		final Map<String, String[]> params;
		if (request.getParameterMap() != null) {
			params = request.getParameterMap();
		} else {
			params = new HashMap<String, String[]>();
		}
		final Integer page;
		final Integer perPage;
		if (params.containsKey(PAGE) && params.containsKey(PER_PAGE)) {
			page = Integer.valueOf(params.get(PAGE)[0]);
			perPage = Integer.valueOf(params.get(PER_PAGE)[0]);
		} else {
			page = 0;
			perPage = 0;
		}
		return new Page(page, perPage);
	}
	
	protected Record to(final String value) {
		for (final Comparator c : Comparators.values()) {
			final String expression = escaper.matcher(c.toString()).replaceAll("\\\\$1").replace("^", "\\^");
			final String regex = "^" + expression + ".*";
			logger.debug(regex);
			if (value.matches(regex)) {
				return new Record(value.replaceAll(expression, ""), c);
			}
		}
		return new Record(value, Comparators.EQUAL);
	}

	protected Record to(final String[] values) {
		if (values.length == 1) {
			return to(values[0]);
		} else {
			return new Record(values, Comparators.EQUAL);
		}
	}
	

	protected <T> Predicate to(final Attribute<?, ?>[] attrs, final String value) {
		final Attribute<?, ?> lastAttr = attrs[attrs.length - 1];
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) lastAttr.getJavaType();
		final Converter<T> converter = converters.to(type);
		final Record record = to(value);
		final Object object = converter.convert(record.value.toString(), type);
		final Comparator comparator = record.comparator;
		final Predicate predicate = new Predicate(object, comparator, attrs);
		return predicate;
	}
	

	protected <T> Collection<Predicate> to(final Attribute<?, ?>[] attrs, final String[] value) {
		if (value.length > 1) {
			final List<Predicate> predicates = new ArrayList<>();
			for (final String s : value) {
				final Predicate predicate = to(attrs, s);
				predicates.add(predicate);				
			}
			//verificar se todas as comparações são iguais, significa que deve ser um like
			if(predicates.stream().filter(p -> p.getComparator().equals(Comparators.EQUAL)).count() == Long.valueOf(predicates.size())){
				final Attribute<?, ?>[] attributes = predicates.get(0).getAttributes();
				final List<?> values = predicates.stream().map(p -> p.getValue()).collect(Collectors.toList());
				final Comparator comparator = Comparators.LIKE;
				final Predicate predicate = new Predicate(values,comparator, attributes);
				return Arrays.asList(predicate);
			}			
			return predicates;
		} else {
			final Predicate predicate = to(attrs, value[0]);
			return Arrays.asList(predicate);
		}
	}

}

class Record {
	public final Object value;
	public final Comparator comparator;

	public Record(final Object value, final Comparator comparator) {
		this.value = value;
		this.comparator = comparator;
	}
}
