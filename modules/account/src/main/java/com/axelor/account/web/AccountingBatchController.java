package com.axelor.account.web;

import com.axelor.account.db.AccountingBatch;
import com.axelor.account.db.repo.AccountingBatchRepository;
import com.axelor.account.service.AccountingBatchService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.google.inject.Inject;

public class AccountingBatchController {
	
	protected final AccountingBatch accountingBatch;
	@Inject
	public AccountingBatchController(AccountingBatch accountingBatch) {
		this.accountingBatch = accountingBatch;
	}



	public void generateBatches(ActionRequest request , ActionResponse response) {
		Context context = request.getContext();
		Long accountingBatchId =(long) context.get("id");
		AccountingBatch accountingBatch = Beans.get(AccountingBatchRepository.class).find(accountingBatchId);
		Beans.get(AccountingBatchService.class).generateBatches(accountingBatch);
	}
}	
