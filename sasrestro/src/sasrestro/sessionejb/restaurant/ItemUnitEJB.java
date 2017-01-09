package sasrestro.sessionejb.restaurant;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.model.restaurant.ItemClassModel;
import sasrestro.model.restaurant.ItemUnitModel;
import sasrestro.sessionejb.core.GenericDAO;



@Stateless
@LocalBean
public class ItemUnitEJB extends GenericDAO<ItemUnitModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ItemUnitEJB() {
		super(ItemUnitModel.class);
	
	}

}
