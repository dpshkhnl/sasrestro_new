package sasrestro.model.util;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import sasrestro.model.account.AccHeadMap;

@Entity
@Table(name = "vat_setting_mcg")
@NamedQueries({
		@NamedQuery(name = "VatSettingModel.findByMapId", query = "SELECT vat FROM VatSettingModel vat  where vat.accHeadMap.accHeadMapId = :mapId") })
public class VatSettingModel implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_BY_MAP_ID = "VatSettingModel.findByMapId";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vat_setting_id")
	private int vatSettingId;

	@Column(name = "percent")
	private double percent;

	@OneToOne
	@JoinColumn(name = "acc_map_id", referencedColumnName = "acc_head_map_id")
	private AccHeadMap accHeadMap;

	public int getVatSettingId() {
		return vatSettingId;
	}

	public void setVatSettingId(int vatSettingId) {
		this.vatSettingId = vatSettingId;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public AccHeadMap getAccHeadMap() {
		return accHeadMap;
	}

	public void setAccHeadMap(AccHeadMap accHeadMap) {
		this.accHeadMap = accHeadMap;
	}
}
