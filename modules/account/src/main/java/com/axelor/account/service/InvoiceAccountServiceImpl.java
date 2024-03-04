package com.axelor.account.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.axelor.account.db.Move;
import com.axelor.account.db.MoveLine;
import com.axelor.account.db.repo.MoveLineRepository;
import com.axelor.account.db.repo.MoveRepository;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class InvoiceAccountServiceImpl implements InvoiceAccountService{
	
	private static BigDecimal debit = BigDecimal.ZERO;

	protected final MoveRepository moveReposetory;
	
	@Inject
	public InvoiceAccountServiceImpl(MoveRepository moveReposetory) {
		this.moveReposetory = moveReposetory;
	}
	
	@Override
	@Transactional
	public Move generateMoveFromInvoice(Invoice invoice) {
		Move move = new Move();
		move.setInvoice(invoice);
		move.setOperationDate(LocalDate.now());
		
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();
		List<MoveLine> moveLines = new ArrayList<MoveLine>();
		
		for(InvoiceLine invoceLine : invoiceLines) {
			setMoveLinesFromInvoiceLines(moveLines, invoceLine, move  );
		}
		MoveLine debitMoveLine = new MoveLine();
		debitMoveLine.setMove(move);
		debitMoveLine.setAccount(invoice.getCustomer().getAccount());
		
		debitMoveLine.setDebit(invoice.getInTaxTotal());
		debitMoveLine.setCredit(debit);
		Beans.get(MoveLineRepository.class).save(debitMoveLine);
		moveLines.add(debitMoveLine);
		move.setMoveLineList(moveLines);
		try {
			Beans.get(MoveRepository.class).save(move);			
		} catch (Exception e) {
			System.out.println("There is an error with the move generation - " + e.getMessage());
			e.printStackTrace();
		}
		return move;
	}
	
	public void setMoveLinesFromInvoiceLines(List<MoveLine> moveLines , InvoiceLine invoceLine, Move move) {
		
		MoveLine moveLine = new MoveLine();
		moveLine.setMove(move);
		moveLine.setAccount(invoceLine.getProduct().getAccount());
		moveLine.setCredit(invoceLine.getInTaxTotal());
		moveLine.setDebit(debit);
		Beans.get(MoveLineRepository.class).save(moveLine);
		moveLines.add(moveLine);
	}

}
