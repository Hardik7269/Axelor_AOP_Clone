package com.axelor.account.service;

import java.util.List;

import com.axelor.account.db.Move;
import com.axelor.account.db.MoveLine;
import com.axelor.account.db.repo.MoveLineRepository;
import com.axelor.account.db.repo.MoveRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class MoveServiceImpl implements MoveService {

	protected final MoveRepository moveRepository;
	protected final MoveLineRepository moveLineRepository;

	@Inject
	public MoveServiceImpl(MoveRepository moveRepository, MoveLineRepository moveLineRepository) {
		super();
		this.moveRepository = moveRepository;
		this.moveLineRepository = moveLineRepository;
	}

	@Override
	@Transactional
	public void setRefrence(Move move) {
		Move obj = Beans.get(MoveRepository.class).find(move.getId());
		String str = (move.getId() < 100) ? "MOVE0" + obj.getId().toString() : "MOVE" + obj.getId().toString();
		obj.setRefrence(str);

		List<MoveLine> moveLineList = obj.getMoveLineList();

		for (MoveLine moveLine : moveLineList) {
			MoveLine newMoveLine = Beans.get(MoveLineRepository.class).find(moveLine.getId());
			newMoveLine.setRefrence(newMoveLine.getId() < 100 ? "ML0" + newMoveLine.getId() : "ML" + newMoveLine.getId());
			Beans.get(MoveLineRepository.class).save(newMoveLine);
		}
		Beans.get(MoveRepository.class).save(obj);
	}
}
