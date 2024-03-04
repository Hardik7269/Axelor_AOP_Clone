package com.axelor.invoice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axelor.contact.db.Contact;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.invoice.db.repo.InvoiceLineRepository;
import com.axelor.invoice.db.repo.InvoiceRepository;
import com.axelor.sale.db.Product;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;


public class InvoiceServiceImpl implements InvoiceService{
	
	protected final InvoiceRepository invoiceRepository;
	protected final InvoiceLineRepository invoiceLineRepository;
	
	@Inject
	public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceLineRepository invoiceLineRepository) {
		this.invoiceRepository = invoiceRepository;
		this.invoiceLineRepository = invoiceLineRepository;
	}
	


	@Transactional(rollbackOn = Exception.class)
	@Override
	public void setStatusCancelBtn(List<Integer> list) {
		
		for(Integer listItem : list) {
			Long id = (long) listItem;
			Invoice invoice = Beans.get(InvoiceRepository.class).find(id);
			if(invoice.getStatusSelect()!=2) {				
				invoice.setStatusSelect(3);
			}
			Beans.get(InvoiceRepository.class).save(invoice);
		}
	}
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void invoiceLineMergeBtn(List<Integer> idList) {
		List<Invoice> invoiceList = new ArrayList<Invoice>();		
				
		for(Integer id : idList) {
			Invoice invoice = Beans.get(InvoiceRepository.class).find((long)id);	
			invoiceList.add(invoice);
		}
		
		String customerName = invoiceList.get(0).getCustomer().getFullName().toString();
		for(Invoice inv : invoiceList) {
			if(!(customerName.equals(inv.getCustomer().getFullName().toString()))){
				return;
			}
		}
		//we might need an repo invoice here
		Invoice newInvoice = generateNewInvoice(invoiceList.get(0).getCustomer());
		for(Invoice inv : invoiceList) {
			inv.setArchived(true);
			inv.setGeneratedInvoice(newInvoice);
			invoiceRepository.save(newInvoice);
		}
		
		List<InvoiceLine> allInvoiceLines = new ArrayList<InvoiceLine>();
		for (Invoice invoices : invoiceList) {
			allInvoiceLines.addAll(invoices.getInvoiceLineList());
		}
		
		Map<Product, List<InvoiceLine>> mapProduct = returnInvoiceLinesProductWise(allInvoiceLines);
		for (Map.Entry<Product, List<InvoiceLine>> entry : mapProduct.entrySet()) {
		    Product product = entry.getKey();
		    boolean allSameField = checkAllFields(mapProduct.get(product));
		    if(allSameField) {
		    	InvoiceLine mergedInvoiceLine = new InvoiceLine();
		    	setFieldsInvoiceLine(mapProduct.get(product), mergedInvoiceLine, newInvoice , product);
		    	List<InvoiceLine> setInvoiceLine = new ArrayList<InvoiceLine>();
		    	setInvoiceLine.add(mergedInvoiceLine);
		    	newInvoice.setInvoiceLineList(setInvoiceLine);
		    	setNewInvoiceTax(mergedInvoiceLine , newInvoice , false , null);
		    	Beans.get(InvoiceLineRepository.class).save(mergedInvoiceLine);
		    }else {
		    	List<InvoiceLine> mergedInvoiceLineList = new ArrayList<InvoiceLine>();
		    	for(InvoiceLine il : mapProduct.get(product)) {
		    		mergedInvoiceLineList.add(invoiceLineRepository.copy(il, true));
		    	}
		    	for(InvoiceLine il : mergedInvoiceLineList) {
		    		il.setInvoice(newInvoice);
		    		il.setProduct(product);
		    		invoiceLineRepository.save(il);
		    	}
		    	setNewInvoiceTax(null, newInvoice, true, mergedInvoiceLineList);
		    	newInvoice.setInvoiceLineList(mergedInvoiceLineList);
		    	mapProduct.put(product, mergedInvoiceLineList);
		    }
		    List<InvoiceLine> productInvoiceLines = entry.getValue();
		}
		invoiceRepository.save(newInvoice);
		
	}

	private void setNewInvoiceTax(InvoiceLine mergedInvoiceLine, Invoice newInvoice , boolean isMultipleInvoice , List<InvoiceLine> invoiceLineList) {
		if(isMultipleInvoice) {
			BigDecimal exTax = BigDecimal.ZERO;
			BigDecimal inTax = BigDecimal.ZERO;
			for(InvoiceLine il : invoiceLineList) {
				exTax = exTax.add(il.getExTaxTotal());
				inTax = inTax.add(il.getInTaxTotal());
			}
			newInvoice.setExTaxTotal(exTax);
			newInvoice.setInTaxTotal(inTax);
			
		}else {
			newInvoice.setExTaxTotal(mergedInvoiceLine.getExTaxTotal());
			newInvoice.setInTaxTotal(mergedInvoiceLine.getInTaxTotal());
		}
	}

	private void setFieldsInvoiceLine(List<InvoiceLine> allInvoiceLines , InvoiceLine mergedInvoiceLine , Invoice newInvoice , Product product) { 
		BigDecimal exTax = BigDecimal.ZERO;
		BigDecimal quantity= BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal inTax = BigDecimal.ZERO;
		String description = new String();
		for(InvoiceLine iv : allInvoiceLines) {
			exTax = exTax.add(iv.getExTaxTotal()) ;
			quantity = quantity.add(iv.getQuantity());
			inTax = inTax.add(iv.getInTaxTotal());
			unitPrice = iv.getUnitPriceUntaxed();
			description = iv.getDescription();
		}
		mergedInvoiceLine.setUnitPriceUntaxed(unitPrice);
		mergedInvoiceLine.setQuantity(quantity);
		mergedInvoiceLine.setExTaxTotal(exTax);;
		mergedInvoiceLine.setInTaxTotal(inTax);;
		mergedInvoiceLine.setDescription(description);
		mergedInvoiceLine.setInvoice(newInvoice);
		mergedInvoiceLine.setProduct(product);
		invoiceLineRepository.save(mergedInvoiceLine);
	}

	private boolean checkAllFields(List<InvoiceLine> invoiceLines) {
	
		for(InvoiceLine il : invoiceLines) {
			if((invoiceLines.get(0).getUnitPriceUntaxed()).equals(il.getUnitPriceUntaxed()) &&
				(invoiceLines.get(0).getTaxRate()).equals(il.getTaxRate()) &&
				(invoiceLines.get(0).getDescription()).equals(il.getDescription())){
				
			}else {
				return false;
			}	
		}
		return true;
	}

	public Map<Product , List<InvoiceLine>> returnInvoiceLinesProductWise(List<InvoiceLine> invoiceLines){
		Map<Product , List<InvoiceLine>> map = new HashMap<Product , List<InvoiceLine>>();
		for(InvoiceLine il : invoiceLines) {
			map.put(il.getProduct(), invoiceLines);
		}
		return map;
	}

	public Invoice generateNewInvoice(Contact customer ) {
		Invoice newInvoice = new Invoice();
		newInvoice.setCustomer(customer);
		newInvoice.setInvoiceDateT(LocalDateTime.now());
		newInvoice.setStatusSelect(0);
		invoiceRepository.save(newInvoice);
		return newInvoice;
	}

}

