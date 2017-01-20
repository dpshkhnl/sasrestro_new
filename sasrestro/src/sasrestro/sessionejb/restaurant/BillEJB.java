package sasrestro.sessionejb.restaurant;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.misc.DirectSqlUtils;
import sasrestro.model.restaurant.BillModel;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.core.GenericDAO;


@Stateless
@LocalBean
public class BillEJB extends GenericDAO<BillModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public BillEJB() {
		super(BillModel.class);
	
	}
	public List<BillModel> getByBillModel(BillModel billModel)
	{
		List<BillModel> lstTables = new ArrayList<>();
		String sql = "select * from tbl_bill b ";
		String cond ="";
		
		if (billModel.getBillNo() != 0)
		{
			cond  +=" b.bill_no ="+ billModel.getBillNo();
		}
		if (billModel.getBillTo() != null && !billModel.getBillTo().equals(""))
		{
			if (!cond.equals(""))
				cond+=" and ";
			cond+= "b.bill_to like '"+ billModel.getBillTo()+"%'"; 
		}
		if (billModel.getDate() !=  null)
		{
			if (!cond.equals(""))
				cond+=" and ";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			cond+= "b.date_time = '"+ sdf.format(billModel.getDate())+"'"; 
		}
		if (!cond.equals(""))
		{
			sql += " where "+cond;
		}
		
		System.out.println(sql);
		
		lstTables =  DirectSqlUtils.getListOfResultSets(sql, BillModel.class);
		
		return lstTables;
		
	}

}