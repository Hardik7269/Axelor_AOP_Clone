package com.axelor.dashboard.service;

import com.axelor.app.AxelorModule;

public class DashboardModule extends AxelorModule{
	@Override
	protected void configure() {
		bind(HomeDashboardService.class).to(HomeDashboardServiceImpl.class);
	}
}	
 