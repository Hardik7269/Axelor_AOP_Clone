package com.axelor.dashboard.service;

import java.util.List;
import java.util.Map;

import com.axelor.rpc.Context;

public interface HomeDashboardService {
	public List<Map<String, Object>> calculateAmountInvoice(Map<String, Object> obj, Context context);
}
