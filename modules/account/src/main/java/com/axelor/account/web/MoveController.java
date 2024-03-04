package com.axelor.account.web;

import com.axelor.account.db.Move;
import com.axelor.account.service.MoveService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class MoveController {
	
	public void setRefrence(ActionRequest request , ActionResponse response) {
		Context context = request.getContext();
		Move move = context.asType(Move.class);
		
		Beans.get(MoveService.class).setRefrence(move);
	}
}
