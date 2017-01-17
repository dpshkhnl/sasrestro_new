package sasrestro.sessionejb.restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.misc.DirectSqlUtils;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@LocalBean
public class TableEJB extends GenericDAO<TableModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public TableEJB() {
		super(TableModel.class);
	
	}
	
	public List<TableModel> getByTableClass(int classId)
	{
		List<TableModel> lstTables = new ArrayList<>();
		String sql = "select * from tbl_tables o where o.table_class ="+classId;
		
		System.out.println(sql);
		
		lstTables =  DirectSqlUtils.getListOfResultSets(sql, TableModel.class);
		
		return lstTables;
		
	}

}
