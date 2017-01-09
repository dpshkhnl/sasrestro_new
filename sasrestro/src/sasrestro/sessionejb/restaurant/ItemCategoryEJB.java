package sasrestro.sessionejb.restaurant;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.model.restaurant.ItemCategoryModel;
import sasrestro.sessionejb.core.GenericDAO;


@Stateless
@LocalBean
public class ItemCategoryEJB extends GenericDAO<ItemCategoryModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ItemCategoryEJB() {
		super(ItemCategoryModel.class);
	
	}
}