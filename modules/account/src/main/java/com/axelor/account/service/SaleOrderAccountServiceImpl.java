package com.axelor.account.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.invoice.db.repo.InvoiceRepository;
import com.axelor.sale.db.SaleOrder;
import com.axelor.sale.db.SaleOrderLine;
import com.google.inject.persist.Transactional;

public class SaleOrderAccountServiceImpl implements SaleOrderAccountService{

	@Transactional
	@Override
	public Invoice generateInvoiceFromSaleOrders(SaleOrder saleOrder) {
		Invoice invoice = new Invoice();
		invoice.setSaleOrder(saleOrder);
		invoice.setStatusSelect(0);
		invoice.setCustomer(saleOrder.getCustomer());
		invoice.setInTaxTotal(saleOrder.getInTaxTotal());
		LocalDate estDate = saleOrder.getEstimatedInvoiceDate();
		invoice.setInvoiceDateT(Optional.ofNullable(estDate).map(LocalDate::atStartOfDay).orElse(null));
		List<SaleOrderLine> saleOrderLine = saleOrder.getSaleOrderLineList();
		List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
		
		for(int i = 0 ; i < saleOrderLine.size() ; i++) {
			SaleOrderLine sol = saleOrderLine.get(i);
			setInvoiceLinesFromSaleOrderLine(sol, invoiceLineList , invoice);
		}
		invoice.setInvoiceLineList(invoiceLineList);
		Beans.get(InvoiceRepository.class).save(invoice);
		return invoice;
	}
	
	public void setInvoiceLinesFromSaleOrderLine(SaleOrderLine sol , List<InvoiceLine> invoiceLineList , Invoice invoice) {
		InvoiceLine invoiceLine = new InvoiceLine();
		invoiceLine.setProduct(sol.getProduct());
		invoiceLine.setDescription(sol.getDescription());
		invoiceLine.setUnitPriceUntaxed(sol.getUnitPriceUntaxed());
		invoiceLine.setExTaxTotal(sol.getExTaxTotal());
		invoiceLine.setQuantity(sol.getQuantity());
		invoiceLine.setTaxRate(sol.getTaxRate());
		invoiceLine.setInvoice(invoice);
		BigDecimal inTaxCalculate = sol.getExTaxTotal().multiply(BigDecimal.ONE.add(sol.getTaxRate())); 
		invoiceLine.setInTaxTotal(inTaxCalculate);
		invoiceLineList.add(invoiceLine);
	}
}
