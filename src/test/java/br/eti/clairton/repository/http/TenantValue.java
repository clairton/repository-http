package br.eti.clairton.repository.http;

import javax.enterprise.context.RequestScoped;

import br.eti.clairton.repository.tenant.Value;

@RequestScoped
public class TenantValue implements Value<String> {

	@Override
	public String get() {
		return "test";
	}

}
