package sasrestro.sessionejb.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.Query;

import sasrestro.model.account.AccountReportModel;
import sasrestro.model.account.LedgerMcg;
import sasrestro.sessionejb.core.GenericDAO;

/**
 * @author Ganesh-Magnus
 * 
 */
@Stateless
@Local
public class AccountReportEJB extends GenericDAO<AccountReportModel> {

	public AccountReportEJB() {
		super(AccountReportModel.class);
		// System.out.println("Hello i am ----Account Report EJB------------");
		// TODO Auto-generated constructor stub
	}

	/*
	 * @PostConstruct public void hello() { System.out
	 * .println("*Hello I am Account Report EJB called"); }
	 */
	@SuppressWarnings("unchecked")
	public List<AccountReportModel> getTrialBalanceReportData(int level, String toDate, String fromDate, int fyId) {
		String levelCondtion = level == 1
				? " (aa.acc_code like CONCAT(a.acc_code,'.%') and (a.PARENT=0 OR a.PARENT>0)) OR " : " ";

		/*
		 * "SELECT * FROM " +
		 * "( select a.acc_code,a.acc_name,a.acc_type,a.parent, " +
		 * "	CASE WHEN ((a.acc_type='1' OR a.acc_type='2' OR (a.acc_type='5' AND a.drcr='Dr'))) then  "
		 * +
		 * " IFNULL((select (SUM(dr_amt)-SUM(cr_amt)) 	FROM  ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id  "
		 * + " where (" + levelCondtion +
		 * " l.acc_code_id=a.acc_head_id) and  l.posted_date between '" +
		 * fromDate + "' AND '" + toDate + "' and l.fy_id=" + fyId + " ),0.00) "
		 * + " else 0.00 end as drAmt, " +
		 * " CASE WHEN ((a.acc_type='3' OR a.acc_type='4' OR (a.acc_type='5' AND a.drcr='Cr'))) then  "
		 * +
		 * " IFNULL((select (SUM(cr_amt)-SUM(dr_amt)) 	FROM  ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
		 * + " where (" + levelCondtion +
		 * " l.acc_code_id=a.acc_head_id) and  l.posted_date between '" +
		 * fromDate + "' AND '" + toDate + "' and l.fy_id=" + fyId + " ),0.00) "
		 * + " else 0.00 end as crAmt From acc_head_mcg a " + " ) as a " +
		 * " where (a.drAmt>0.00 or a.crAmt>0.00) " + " order by 1 ";
		 */

		String qry1 = "SELECT\n" + "	*\n" + "FROM\n" + "	(\n" + "		SELECT\n" + "			a.acc_code,\n"
				+ "			a.acc_name,\n" + "			a.acc_type,\n" + "			a.parent,\n" + "			CASE\n"
				+ "		WHEN (\n" + "			(\n" + "				a.acc_type = '1'\n"
				+ "				OR a.acc_type = '2'\n" + "				OR (\n" + "					a.acc_type = '5'\n"
				+ "					AND a.drcr = 'Dr'\n" + "				)\n" + "			)\n"
				+ "		) THEN\n" + "			IFNULL(\n" + "				(\n" + "					SELECT\n"
				+ "						(SUM(dr_amt) - SUM(cr_amt))\n" + "					FROM\n"
				+ "						ledger_mcg l\n"
				+ "					INNER JOIN acc_head_mcg aa ON l.acc_code_id = aa.acc_head_id\n"
				+ "					WHERE\n" + "						(\n" + "							"
				+ levelCondtion + " l.acc_code_id = a.acc_head_id\n" + "						)\n"
				+ "					AND l.posted_date BETWEEN '" + fromDate + "'\n" + "					AND '" + toDate
				+ "'\n" + "					AND l.fy_id = 1\n" + "				),\n" + "				0.00\n"
				+ "			)\n" + "		ELSE\n" + "			0.00\n" + "		END AS drAmt,\n" + "		CASE\n"
				+ "	WHEN (\n" + "		(\n" + "			a.acc_type = '3'\n" + "			OR a.acc_type = '4'\n"
				+ "			OR (\n" + "				a.acc_type = '5'\n" + "				AND a.drcr = 'Cr'\n"
				+ "			)\n" + "		)\n" + "	) THEN\n" + "		IFNULL(\n" + "			(\n"
				+ "				SELECT\n" + "					(SUM(cr_amt) - SUM(dr_amt))\n" + "				FROM\n"
				+ "					ledger_mcg l\n"
				+ "				INNER JOIN acc_head_mcg aa ON l.acc_code_id = aa.acc_head_id\n" + "				WHERE\n"
				+ "					(\n" + "						" + levelCondtion
				+ " l.acc_code_id = a.acc_head_id\n" + "					)\n"
				+ "				AND l.posted_date BETWEEN '" + fromDate + "'\n" + "				AND '" + toDate + "'\n"
				+ "				AND l.fy_id = 1\n" + "			),\n" + "			0.00\n" + "		)\n" + "	ELSE\n"
				+ "		0.00\n" + "	END AS crAmt\n" + "	FROM\n" + "		acc_head_mcg a\n" + "	) AS trb\n" + "WHERE\n"
				+ "	(\n" + "		trb.drAmt > 0.00\n" + "		OR trb.crAmt > 0.00\n" + "	)\n" + "ORDER BY\n" + "	1";

		List<Object> objLst = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(qry1);
		objLst.addAll(query.getResultList());
		List<AccountReportModel> result = new ArrayList<AccountReportModel>();
		AccountReportModel accRptModel;
		for (Object obj : objLst) {
			Object[] objValue = (Object[]) obj;
			accRptModel = new AccountReportModel();
			accRptModel.setAccountCode((String) objValue[0]);
			accRptModel.setParticulars((String) objValue[1]);
			accRptModel.setDrAmt(Double.valueOf(objValue[4].toString()));
			accRptModel.setCrAmt(Double.valueOf(objValue[5].toString()));
			result.add(accRptModel);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<AccountReportModel> getThisAccTypeReportData(int level, String toDate, String fromDate, int fyId,
			int accType) {
		String levelCondtion = level == 1 ? " (aa.acc_code like CONCAT(a.acc_code,'.%') and a.PARENT=0) OR  " : " ";
		String calcStr = (accType == 2 || accType == 1) ? " (SUM(dr_amt)-SUM(cr_amt)) " : " (SUM(cr_amt)-SUM(dr_amt)) ";
		String qry = /*
						 * " SELECT acc_code as accountCode,acc_name as particulars,drAmt,crAmt FROM (SELECT a.acc_code, a.acc_name,a.parent, "
						 * + "  CASE when ("+accType+" in(1,2)) then (SELECT  "
						 * +calcStr+" FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						 * + "  WHERE ( "
						 * +levelCondtion+" l.acc_code_id=a.acc_head_id) " +
						 * "  AND l.fy_id="
						 * +fyId+"   AND l.posted_date between '2014-01-01' AND '"
						 * +toDate+"'  ) else 0.00 end AS drAmt," +
						 * "  CASE when ("+accType+" in(3,4)) then (SELECT  "
						 * +calcStr+" FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						 * + "  WHERE ( "
						 * +levelCondtion+" l.acc_code_id=a.acc_head_id) " +
						 * "  AND l.fy_id="
						 * +fyId+"   AND l.posted_date between '2014-01-01' AND '"
						 * +toDate+"'  ) else 0.00 end AS crAmt" +
						 * "  FROM acc_head_mcg AS a WHERE (acc_type='"
						 * +accType+"'))  AS T1  where drAmt<>0 or crAmt<>0  ORDER BY acc_code "
						 * ;
						 */
				" SELECT acc_code as accountCode,acc_name as particulars,drAmt,crAmt FROM (SELECT a.acc_code, a.acc_name,a.parent, "
						+ "  CASE when (" + accType + " in(1,2)) then (SELECT  " + calcStr
						+ " FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						+ "  WHERE ( " + levelCondtion + " l.acc_code_id=a.acc_head_id) " + "  AND l.fy_id=" + fyId
						+ "   AND l.posted_date between '" + fromDate + "' AND '" + toDate
						+ "'  ) else 0.00 end AS drAmt," + "  CASE when (" + accType + " in(3,4)) then (SELECT  "
						+ calcStr + " FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						+ "  WHERE ( " + levelCondtion + " l.acc_code_id=a.acc_head_id) " + "  AND l.fy_id=" + fyId
						+ "   AND l.posted_date between '" + fromDate + "' AND '" + toDate
						+ "'  ) else 0.00 end AS crAmt" + "  FROM acc_head_mcg AS a WHERE (acc_type='" + accType
						+ "'))  AS T1  where drAmt<>0 or crAmt<>0  ORDER BY acc_code ";
		Query query = getEntityManager().createNativeQuery(qry, AccountReportModel.class);

		List<AccountReportModel> result = query.getResultList();
		return result;
	}

	public double findProfitLossAmount(int level, String toDate, String fromDate, int fyId) {
		String levelCondtion = level == 1 ? " (aa.acc_code like CONCAT(a.acc_code,'.%') and a.PARENT=0) OR  " : " ";
		String qry = /*
						 * " SELECT acc_code as accountCode,acc_name as particulars,drAmt,crAmt FROM (SELECT a.acc_code, a.acc_name,a.parent, "
						 * +
						 * "  CASE when (a.acc_type=2) then (SELECT  (SUM(dr_amt)-SUM(cr_amt)) FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						 * + "  WHERE ( "
						 * +levelCondtion+" l.acc_code_id=a.acc_head_id) " +
						 * "  AND l.fy_id="
						 * +fyId+"   AND l.posted_date between '2014-01-01' AND '"
						 * +toDate+"'  ) else 0.00 end AS drAmt," +
						 * "  CASE when (a.acc_type=4) then (SELECT  (SUM(cr_amt)-SUM(dr_amt)) FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						 * + "  WHERE ( "
						 * +levelCondtion+" l.acc_code_id=a.acc_head_id) " +
						 * "  AND l.fy_id="
						 * +fyId+"   AND l.posted_date between '2014-01-01' AND '"
						 * +toDate+"'  ) else 0.00 end AS crAmt" +
						 * "  FROM acc_head_mcg AS a  )  AS T1  where parent=0 and (drAmt<>0 or crAmt<>0)  ORDER BY acc_code "
						 * ;
						 */
				" SELECT acc_code as accountCode,acc_name as particulars,drAmt,crAmt FROM (SELECT a.acc_code, a.acc_name,a.parent, "
						+ "  CASE when (a.acc_type=2) then (SELECT  (SUM(dr_amt)-SUM(cr_amt)) FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						+ "  WHERE ( " + levelCondtion + " l.acc_code_id=a.acc_head_id) " + "  AND l.fy_id=" + fyId
						+ "   AND l.posted_date between '" + fromDate + "' AND '" + toDate
						+ "'  ) else 0.00 end AS drAmt,"
						+ "  CASE when (a.acc_type=4) then (SELECT  (SUM(cr_amt)-SUM(dr_amt)) FROM ledger_mcg l inner join acc_head_mcg aa on l.acc_code_id=aa.acc_head_id "
						+ "  WHERE ( " + levelCondtion + " l.acc_code_id=a.acc_head_id) " + "  AND l.fy_id=" + fyId
						+ "   AND l.posted_date between '" + fromDate + "' AND '" + toDate
						+ "'  ) else 0.00 end AS crAmt"
						+ "  FROM acc_head_mcg AS a  )  AS T1  where parent=0 and (drAmt<>0 or crAmt<>0)  ORDER BY acc_code ";
		Query query = getEntityManager().createNativeQuery(qry, AccountReportModel.class);

		@SuppressWarnings("unchecked")
		List<AccountReportModel> result = query.getResultList();
		double incomeSum = 0.00, expenseSum = 0.00;
		for (AccountReportModel arm : result) {
			if (arm.getCrAmt() > 0.00)
				incomeSum += arm.getCrAmt();
			else
				expenseSum += arm.getDrAmt();
		}
		return incomeSum - expenseSum;

	}

	@SuppressWarnings("unchecked")
	public List<LedgerMcg> getLedger(int accHeadId, Date fromDate, Date toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String qry = "SELECT l FROM LedgerMcg l " + "JOIN l.accountHead a " + "WHERE a.accHeadId='" + accHeadId + "' "
				+ "AND (l.postedDate BETWEEN '" + sdf.format(fromDate) + "' AND '" + sdf.format(toDate) + "') ";
		Query query = getEntityManager().createQuery(qry, LedgerMcg.class);

		return (List<LedgerMcg>) query.getResultList();
	}

}
