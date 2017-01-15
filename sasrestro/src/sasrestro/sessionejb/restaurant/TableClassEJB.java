package sasrestro.sessionejb.restaurant;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.model.restaurant.TableClass;
import sasrestro.sessionejb.core.GenericDAO;



@Stateless
@LocalBean
public class TableClassEJB extends GenericDAO<TableClass> implements Serializable {

	private static final long serialVersionUID = 1L;

	public TableClassEJB() {
		super(TableClass.class);
	
	}

}
