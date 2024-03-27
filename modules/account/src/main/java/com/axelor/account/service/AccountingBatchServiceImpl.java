package com.axelor.account.service;

import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;

import com.axelor.account.db.AccountingBatch;
import com.axelor.account.db.Batch;
import com.axelor.account.db.Move;
import com.axelor.account.db.repo.BatchRepository;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.repo.InvoiceRepository;
import com.google.inject.persist.Transactional;

public class AccountingBatchServiceImpl implements AccountingBatchService {

	public static final int FETCH_LIMIT = 20;
	protected final BatchRepository batchRepository;
	protected final InvoiceRepository invoiceRepository;
	protected final InvoiceAccountService invoiceAccountService;

	@Inject
	public AccountingBatchServiceImpl(BatchRepository batchRepository, InvoiceRepository invoiceRepository,
			InvoiceAccountService invoiceAccountService) {
		this.batchRepository = batchRepository;
		this.invoiceRepository = invoiceRepository;
		this.invoiceAccountService = invoiceAccountService;
	}

	protected static int anamoly = 0;
	protected static int sucess = 0;

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void generateBatches(AccountingBatch accountingBatch) {

		Batch newBatch = new Batch();
		List<Invoice> invoiceList = invoiceRepository.all().fetch(FETCH_LIMIT);
		newBatch.setStartDateT(ZonedDateTime.now());

		for (Invoice inv : invoiceList) {

			if (inv.getStatusSelect().equals(1)) {
				inv.setStatusSelect(2);
				Move newMove = invoiceAccountService.generateMoveFromInvoice(inv);

				if (newMove == null) {
					newBatch.setAnomaly(anamoly + 1);
				} else {
					newBatch.setDone(sucess + 1);
				}
			}
		}
		newBatch.setAccountingBatch(accountingBatch);
		newBatch.setEndDateT(ZonedDateTime.now());
		batchRepository.save(newBatch);

	}
}
