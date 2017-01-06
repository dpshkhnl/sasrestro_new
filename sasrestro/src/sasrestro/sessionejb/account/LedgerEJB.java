package sasrestro.sessionejb.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.misc.DirectSqlUtils;
import sasrestro.model.account.LedgerMcg;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@LocalBean
public class LedgerEJB extends GenericDAO<LedgerMcg> {
	public LedgerEJB() {
		super(LedgerMcg.class);
	}

	public List<LedgerMcg> getLedger(int accHeadId, Date fromDate, Date toDate) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fromDt", fromDate);
		parameters.put("toDt", toDate);
		parameters.put("accHeadIdPassed", accHeadId);

		return super.findAllWithGivenCondition(LedgerMcg.LIST_FOR_LEDGER_REPORT, parameters);
	}

	public double findBroughtDownAmount(String toDate, int accHeadId) {
		return ((BigDecimal) DirectSqlUtils
				.getSingleResult("SELECT ABS(ifnull((SUM(dr_amt)-SUM(cr_amt)),0.00)) AS PrevBalance \n"
						+ "				 FROM ledger_mcg   \n" + "				 WHERE acc_code_id = '" + accHeadId
						+ "'\n" + "				 AND posted_date < '" + toDate + "'\n"
						+ "				 AND led_id >= (select IFNULL(min(led_id), 0)\n"
						+ "				 from  ledger_mcg \n"
						+ "				 where  posted_date = (select posted_date \n"
						+ "					from  ledger_mcg \n"
						+ "				 		where  led_id = (select IFNULL(max(led_id),0) from  ledger_mcg\n"
						+ "				  			where posted_date < '" + toDate + "'	))\n"
						+ "						and is_opening = 1	)")).doubleValue();
	}

}
