package sasrestro.mb.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.misc.AbstractMB;
import sasrestro.model.account.AccHeadMap;
import sasrestro.model.util.VatSettingModel;
import sasrestro.sessionejb.account.AccHeadMapEJB;
import sasrestro.sessionejb.util.VatSettingEJB;

@ViewScoped
@ManagedBean(name = "vatSettingMB")
public class VatSettingMB extends AbstractMB implements Serializable {
	private static final long serialVersionUID = 1L;

	@EJB
	VatSettingEJB vatSettingEJB;

	@EJB
	AccHeadMapEJB accHeadMapEJB;

	private double vatPercent, servChargePercent;

	@PostConstruct
	public void checkSavedVat() {
		AccHeadMap vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");
		if (vatAccHeadMap != null) {
			VatSettingModel savedVat = vatSettingEJB.getVatSettingByMapId(vatAccHeadMap.getAccHeadMapId());
			if (savedVat != null) {
				vatPercent = savedVat.getPercent();
			}
		} else {
			vatPercent = 0.0;
		}

		AccHeadMap servChargeAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge");
		if (servChargeAccHeadMap != null) {
			VatSettingModel savedServiceCharge = vatSettingEJB
					.getVatSettingByMapId(servChargeAccHeadMap.getAccHeadMapId());
			if (savedServiceCharge != null) {
				servChargePercent = savedServiceCharge.getPercent();
			}
		} else {
			servChargePercent = 0.0;
		}
	}

	public double getVatPercent() {
		return vatPercent;
	}

	public void setVatPercent(double vatPervent) {
		this.vatPercent = vatPervent;
	}

	public double getServChargePercent() {
		return servChargePercent;
	}

	public void setServChargePercent(double servChargePercent) {
		this.servChargePercent = servChargePercent;
	}

	public void save() {
		AccHeadMap vatAccHead = accHeadMapEJB.getByMapPurpose("Vat");
		AccHeadMap servChargeAccHead = accHeadMapEJB.getByMapPurpose("Service Charge");
		if (vatAccHead == null || servChargeAccHead == null) {
			if (vatAccHead == null) {
				displayErrorMessageToUser("Please map vat account.");
			} else if (servChargeAccHead == null) {
				displayErrorMessageToUser("Please map service charge account.");
			}
			return;
		} else {
			VatSettingModel vatObj = new VatSettingModel();
			VatSettingModel serviceChargeObj = new VatSettingModel();

			List<VatSettingModel> saveLst = new ArrayList<>();
			List<VatSettingModel> dbVatLst = vatSettingEJB.findAll();
			List<VatSettingModel> updateLst = new ArrayList<>();

			if (dbVatLst.size() > 0) {
				for (VatSettingModel obj : dbVatLst) {
					if (obj.getAccHeadMap().equals(vatAccHead)) {
						obj.setPercent(vatPercent);
						updateLst.add(obj);
					} else if (obj.getAccHeadMap().equals(servChargeAccHead)) {
						obj.setPercent(servChargePercent);
						updateLst.add(obj);
					}
				}
				vatSettingEJB.updateList(updateLst);
				displayInfoMessageToUser("Update successfull.");
			} else {
				vatObj.setAccHeadMap(vatAccHead);
				vatObj.setPercent(vatPercent);
				saveLst.add(vatObj);

				serviceChargeObj.setAccHeadMap(servChargeAccHead);
				serviceChargeObj.setPercent(servChargePercent);
				saveLst.add(serviceChargeObj);
				
				vatSettingEJB.saveList(saveLst);
				displayInfoMessageToUser("Save successfull.");
			}
		}
	}
}
