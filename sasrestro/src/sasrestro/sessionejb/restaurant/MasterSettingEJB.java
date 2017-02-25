package sasrestro.sessionejb.restaurant;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.model.restaurant.MasterSettingModel;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@LocalBean
public class MasterSettingEJB extends GenericDAO<MasterSettingModel> implements Serializable {

	private static final long serialVersionUID = 1L;

	public MasterSettingEJB() {
		super(MasterSettingModel.class);
	
	}
}



