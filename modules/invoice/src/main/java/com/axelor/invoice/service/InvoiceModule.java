package com.axelor.invoice.service;

import com.axelor.app.AxelorModule;

public class InvoiceModule extends AxelorModule{
	@Override
	protected void configure() {
		bind(InvoiceService.class).to(InvoiceServiceImpl.class);
	}
}
