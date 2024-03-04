package com.axelor.account.web;

import com.axelor.account.db.Move;
import com.axelor.account.service.InvoiceAccountService;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;


public class InvoiceAccountController {
	
	public void generateMoveFromInvoice(ActionRequest request, ActionResponse response) {
		Context context= request.getContext();
		Invoice invoice = context.asType(Invoice.class);
		try {
			Move getMove = Beans.get(InvoiceAccountService.class).generateMoveFromInvoice(invoice);
			System.err.println(getMove);
			response.setView(ActionView.define("Move").model(Move.class.getName()).add("form" , "move-form")
					.context("_showRecord", getMove.getId()).map());
			
		} catch (Exception e) {
			response.setError("There is an error with the move generation"+ e.getMessage());
		}
		
	}
	
}
