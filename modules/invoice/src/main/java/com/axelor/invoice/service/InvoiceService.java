package com.axelor.invoice.service;

import java.util.List;

public interface InvoiceService {
	public void setStatusCancelBtn(List<Integer> list);
	public void invoiceLineMergeBtn(List<Integer> idList);
}
