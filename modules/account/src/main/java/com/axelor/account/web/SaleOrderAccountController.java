package com.axelor.account.web;

import com.axelor.account.service.SaleOrderAccountService;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.sale.db.SaleOrder;
import com.axelor.sale.db.repo.SaleOrderRepository;

public class SaleOrderAccountController {

	public void generateInvoiceFromSaleOrder(ActionRequest request, ActionResponse response) {
		Context context = request.getContext();
		SaleOrder saleOrder = context.asType(SaleOrder.class);
		saleOrder = Beans.get(SaleOrderRepository.class).find(saleOrder.getId());

		try {
			Invoice invoice = (Invoice) Beans.get(SaleOrderAccountService.class)
					.generateInvoiceFromSaleOrders(saleOrder);

			response.setView(ActionView.define("Invoice").model(Invoice.class.getName()).add("form", "invoice-form")
					.context("_showRecord", invoice.getId()).map());
		} catch (Exception e) {
			response.setError("There is an error with the invoice generation");
		}

	}
}
