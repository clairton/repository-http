package br.eti.clairton.repository.http;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.repository.Model_;

@StaticMetamodel(Operacao.class)
public abstract class Operacao_ extends Model_ {

	public static volatile SingularAttribute<Operacao, String> nome;
	public static volatile SingularAttribute<Operacao, Recurso> recurso;

}
