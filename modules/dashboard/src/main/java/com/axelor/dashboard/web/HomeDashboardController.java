package com.axelor.dashboard.web;

import java.util.List;
import java.util.Map;

import com.axelor.dashboard.service.HomeDashboardService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class HomeDashboardController{
	
	public void getHomeDashboardData(ActionRequest request, ActionResponse response) {
		
		Context context = request.getContext();
		Map<String, Object> obj =(Map<String, Object>)request.getRawContext().get("customer");
		
		List<Map<String, Object>> reasult = Beans.get(HomeDashboardService.class).calculateAmountInvoice(obj , context);
		System.out.println(reasult);
		response.setData(reasult);
	}
}
