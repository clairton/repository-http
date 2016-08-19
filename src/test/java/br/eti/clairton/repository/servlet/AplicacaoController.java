package br.eti.clairton.repository.servlet;

import static br.com.caelum.vraptor.view.Results.json;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.repository.Order;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.servlet.Page;
import br.eti.clairton.repository.servlet.QueryParser;

@Controller
@Path("aplicacoes")
public class AplicacaoController{



	private final Repository repository;
	private final ServletRequest request;
	private final QueryParser queryParser;

	private final Result result;
	
	@Deprecated
	public AplicacaoController() {
		this(null, null, null, null);
	}
	
	@Inject
	public AplicacaoController(Repository repository, ServletRequest request, QueryParser queryParser, final Result result) {
		super();
		this.repository = repository;
		this.request = request;
		this.queryParser = queryParser;
		this.result = result;
	}

	/**
	 * Mostra os recursos.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get("")
	public void index() {
		System.out.println(request.getParameterValues("page"));
		final Page paginate = queryParser.paginate(request, Aplicacao.class);
		final Collection<Predicate> predicates = queryParser.parse(request, Aplicacao.class);
		repository.from(Aplicacao.class);
		repository.distinct();
		if (!predicates.isEmpty()) {
			repository.where(predicates);
		}
		final List<Order> orders = queryParser.order(request, Aplicacao.class);
		repository.orderBy(orders);
		final PaginatedCollection<Aplicacao, Meta> aplicacoes = repository.collection(paginate.offset, paginate.limit);
		result.use(json()).from(aplicacoes).serialize();		
	}	
}
