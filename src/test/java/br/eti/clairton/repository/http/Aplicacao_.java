package br.eti.clairton.repository.http;

import java.time.LocalDate;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.repository.Model_;
import br.eti.clairton.repository.http.Aplicacao.Tipo;

@StaticMetamodel(Aplicacao.class)
public abstract class Aplicacao_ extends Model_ {
	public static volatile SingularAttribute<Aplicacao, String> nome;
	public static volatile SingularAttribute<Aplicacao, Tipo> tipo;
	public static volatile SingularAttribute<Aplicacao, LocalDate> criadoEm;
	public static volatile CollectionAttribute<Aplicacao, Recurso> recursos;
}
