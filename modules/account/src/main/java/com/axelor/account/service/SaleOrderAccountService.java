package com.axelor.account.service;

import com.axelor.invoice.db.Invoice;
import com.axelor.sale.db.SaleOrder;

public interface SaleOrderAccountService {
	public Invoice generateInvoiceFromSaleOrders(SaleOrder saleOrder);
}
