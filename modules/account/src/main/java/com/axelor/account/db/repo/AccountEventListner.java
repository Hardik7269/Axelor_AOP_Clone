package com.axelor.account.db.repo;

import java.time.temporal.ChronoUnit;

import javax.persistence.PostPersist;

import com.axelor.contact.db.Event;

public class AccountEventListner {
	

	@PostPersist
	public void calculateDuration(Event event) {
		System.err.println("Listner call");
		long durationInSeconds = ChronoUnit.DAYS.between(event.getStartDateT().toLocalDate(), event.getEndDateT().toLocalDate());
		event.setDuration(durationInSeconds);
	}
}
