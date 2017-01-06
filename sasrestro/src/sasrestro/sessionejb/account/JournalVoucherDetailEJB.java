package sasrestro.sessionejb.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import sasrestro.model.account.JournalVoucherDetailModel;
import sasrestro.model.account.JournalVoucherModel;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@Local
public class JournalVoucherDetailEJB extends GenericDAO<JournalVoucherDetailModel> {
	public JournalVoucherDetailEJB() {
		super(JournalVoucherDetailModel.class);
	}

	public List<JournalVoucherDetailModel> getVoucherDetail(JournalVoucherModel jvm) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jvm", jvm);

		return findAllWithGivenCondition(JournalVoucherDetailModel.GET_VOUCHER_DETAIL, parameters);
	}
}
