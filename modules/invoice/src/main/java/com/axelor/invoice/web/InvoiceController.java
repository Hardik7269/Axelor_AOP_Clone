package com.axelor.invoice.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;


public class InvoiceController {

		public void setInvoiceDateTime(ActionRequest request, ActionResponse response) {			
	        response.setValue("invoiceDateT", LocalDateTime.now());
		}
				
		public void calculateTaxTotal(ActionRequest request, ActionResponse response) {
		    try {
		        Context context = request.getContext();
		        Invoice invoice = context.asType(Invoice.class);
		        List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
		        BigDecimal sumInTaxTotal = BigDecimal.ZERO;
		        BigDecimal sumExTaxTotal = BigDecimal.ZERO;
		        for (int i = 0; i < invoiceLineList.size(); i++) {
		            sumExTaxTotal = sumExTaxTotal.add(invoiceLineList.get(i).getExTaxTotal());
		            sumInTaxTotal = sumInTaxTotal.add(invoiceLineList.get(i).getInTaxTotal());
		        }
		        response.setValue("inTaxTotal", sumInTaxTotal);
		        response.setValue("exTaxTotal", sumExTaxTotal); 
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		public void validateRecord(ActionRequest request, ActionResponse response) {
			validationOfRecords(request , response);
			response.setValue("statusSelect", 1);
		}
		
		public void ventilateRecord(ActionRequest request, ActionResponse response) {
			validationOfRecords(request , response);
			response.setValue("statusSelect", 2);
		}
		
		public void validationOfRecords(ActionRequest request, ActionResponse response) {
			Context context = request.getContext();
			Invoice invoice = context.asType(Invoice.class);
			
			if (invoice.getInvoiceLineList().size() == 0) {
				response.setError("At least one invoice line is required");
			}
			List<InvoiceLine> invoiceLineList  = invoice.getInvoiceLineList();
			for (int i = 0; i < invoiceLineList.size(); i++) {
			    if (invoiceLineList.get(i).getInTaxTotal().compareTo(BigDecimal.ZERO) <= 0) {
			    	response.setError("One invoice line has a null or negative total");
			    }
			}
			
			calculateTaxTotal(request , response);
		}
		
		public void cancelBtn(ActionRequest request , ActionResponse response) {
			response.setAlert("This action will cancel this invoice. Do you want to proceed?");
		}
		public void setSelectFun(ActionRequest request , ActionResponse response) {
			response.setValue("statusSelect", 3);
			calculateTaxTotal(request , response);
		}
		
}
