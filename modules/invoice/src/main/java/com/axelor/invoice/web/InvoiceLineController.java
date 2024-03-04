package com.axelor.invoice.web;

import java.math.BigDecimal;

import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;


public class InvoiceLineController {
	
	public void setTaxRate(ActionRequest request , ActionResponse response) {
		response.setValue("taxRate", 0.2);
		Context context = request.getContext();
		Invoice invoice = context.getParent().asType(Invoice.class);
		
		Boolean isHidden = invoice.getCustomer().getIsSubjectToTaxes();
		if(isHidden == false) {
			response.setAttr("taxRate", "hidden", true);
		}
	}
	
	public void setDiscription(ActionRequest request , ActionResponse response) {
		Context context = request.getContext();
		InvoiceLine invoiceLine = context.asType(InvoiceLine.class);
		
		response.setValue("description", invoiceLine.getProduct().getFullName());
	}
	
	public void setUnitPriceUntaxed(ActionRequest request , ActionResponse response) {
		Context context = request.getContext();
		InvoiceLine invoiceLine = context.asType(InvoiceLine.class);
		
		response.setValue("unitPriceUntaxed", invoiceLine.getProduct().getUnitPriceUntaxed());
	}
	public void setExTaxTotal(ActionRequest request , ActionResponse response) {
	    Context context = request.getContext();
	    InvoiceLine invoiceLine = context.asType(InvoiceLine.class);
	    
	    BigDecimal unitPriceUntaxed = invoiceLine.getUnitPriceUntaxed();
	    BigDecimal calculateExTax = invoiceLine.getQuantity().multiply(unitPriceUntaxed);
	    
	    response.setValue("exTaxTotal", calculateExTax);
	    
	    setInTaxTotal(request, response, calculateExTax);
	}
	public void setInTaxTotal(ActionRequest request , ActionResponse response , BigDecimal exTaxTotal) {
		Context context = request.getContext();
		InvoiceLine invoiceLine = context.asType(InvoiceLine.class);
		
		BigDecimal calculateRate = exTaxTotal.multiply(new BigDecimal(String.valueOf(invoiceLine.getTaxRate())));
		
		response.setValue("inTaxTotal", (exTaxTotal.add(calculateRate)));
	}
	
	
	
}
