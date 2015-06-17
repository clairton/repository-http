package br.eti.clairton.repository.vraptor;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class TenantValue implements
		br.eti.clairton.repository.TenantValue<String> {

	@Override
	public String get() {
		return "test";
	}

}
