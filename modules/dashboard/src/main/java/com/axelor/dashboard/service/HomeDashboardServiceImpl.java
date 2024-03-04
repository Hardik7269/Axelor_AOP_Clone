package com.axelor.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.axelor.db.JPA;
import com.axelor.rpc.Context;

public class HomeDashboardServiceImpl implements HomeDashboardService {

	@Override
	public List<Map<String, Object>> calculateAmountInvoice(Map<String, Object> obj, Context context) {
		Long customerId = Long.valueOf(obj.get("id").toString());
		int cId = Integer.parseInt(customerId.toString());

		LocalDateTime startDateT = LocalDate
				.parse(context.get("invoiceDate").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
		LocalDateTime endDateT = startDateT.plusMonths(3);

		Query query = JPA.em()
				.createQuery("SELECT SUM(invoice.exTaxTotal), invoice.invoiceDateT " + "FROM Invoice AS invoice "
						+ "WHERE invoice.statusSelect != 0 " + "AND invoice.customer.id = :customerId "
						+ "AND invoice.invoiceDateT BETWEEN :startDateT AND :endDateT "
						+ "GROUP BY invoice.invoiceDateT");

		query.setParameter("customerId", customerId);
		query.setParameter("startDateT", startDateT);
		query.setParameter("endDateT", endDateT);

		List<Object[]> list = query.getResultList();
		System.out.println("list :" + list);
		List<Map<String, Object>> result = new ArrayList<>();

		for (Object[] ele : list) {
			Map<String, Object> mapped = new HashMap<>();
			mapped.put("exTaxTotal", ele[0]);
			mapped.put("invoiceDateT", ele[1]);
			result.add(mapped);
		}
		return result;
	}
}
