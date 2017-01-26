package sasrestro.sessionejb.restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.misc.DirectSqlUtils;
import sasrestro.model.restaurant.MenuItemModel;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@LocalBean
public class MenuItemEJB extends GenericDAO<MenuItemModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public MenuItemEJB() {
		super(MenuItemModel.class);

	}
	
	public List<MenuItemModel> getByItemCat(int catId)
	{
		List<MenuItemModel> lstItems = new ArrayList<>();
		String sql = "select * from tbl_items o where o.category ="+catId;
		
		System.out.println(sql);
		
		lstItems =  DirectSqlUtils.getListOfResultSets(sql, MenuItemModel.class);
		
		return lstItems;
		
	}

}
