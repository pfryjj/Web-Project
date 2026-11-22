package kr.ac.kopo.subscription.service;

import java.util.List;

import kr.ac.kopo.subscription.dao.SubscriptionDAO;
import kr.ac.kopo.subscription.dao.SubscriptionDAOImpl;
import kr.ac.kopo.subscription.vo.CategoryStatVO;
import kr.ac.kopo.subscription.vo.SubSearchVO;
import kr.ac.kopo.subscription.vo.SubSummaryVO;
import kr.ac.kopo.subscription.vo.SubscriptionVO;

public class SubscriptionServiceImpl implements SubscriptionService {

	private SubscriptionDAO subscriptionDao;

	public SubscriptionServiceImpl() {
		subscriptionDao = new SubscriptionDAOImpl();
	}

	@Override
	public void register(SubscriptionVO sub) {
		subscriptionDao.insert(sub);
	}

	@Override
	public List<SubscriptionVO> getMySubscriptions(SubSearchVO search) {
		return subscriptionDao.findByCondition(search);
	}

	@Override
	public SubscriptionVO getMySubscription(int sno, String memberId) {
		return subscriptionDao.findBySno(sno, memberId);
	}

	@Override
	public boolean modify(SubscriptionVO sub) {
		return subscriptionDao.update(sub) == 1;
	}

	@Override
	public boolean remove(int sno, String memberId) {
		return subscriptionDao.delete(sno, memberId) == 1;
	}

	@Override
	public boolean changeStatus(int sno, String memberId, String status) {
		if (!SubscriptionVO.STATUS_MAP.containsKey(status)) {
			return false;
		}
		return subscriptionDao.updateStatus(sno, memberId, status) == 1;
	}

	@Override
	public boolean renew(int sno, String memberId) {
		return subscriptionDao.renewNextPayDate(sno, memberId) == 1;
	}

	@Override
	public SubSummaryVO getSummary(String memberId) {
		return subscriptionDao.getSummary(memberId);
	}

	@Override
	public List<CategoryStatVO> getCategoryStats(String memberId, long monthlyTotal) {
		List<CategoryStatVO> stats = subscriptionDao.sumByCategory(memberId);
		for (CategoryStatVO stat : stats) {
			int percent = monthlyTotal == 0 ? 0 : (int) Math.round(stat.getTotal() * 100.0 / monthlyTotal);
			stat.setPercent(percent);
		}
		return stats;
	}
}
