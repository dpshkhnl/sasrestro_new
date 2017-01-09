package sasrestro.sessionejb.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import sasrestro.model.util.VatSettingModel;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@LocalBean
public class VatSettingEJB extends GenericDAO<VatSettingModel> implements Serializable {
	private static final long serialVersionUID = 1L;

	public VatSettingEJB() {
		super(VatSettingModel.class);
	}

	public VatSettingModel getVatSettingByMapId(int mapId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("mapId", mapId);

		return super.findOneResult(VatSettingModel.FIND_BY_MAP_ID, param);
	}

}
