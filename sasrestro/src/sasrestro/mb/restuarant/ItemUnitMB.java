package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.misc.AbstractMB;
import sasrestro.model.restaurant.ItemUnitModel;
import sasrestro.sessionejb.restaurant.ItemUnitEJB;

@ViewScoped
@ManagedBean(name="itemUnitMB")
public class ItemUnitMB extends AbstractMB implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	ItemUnitEJB itemUnitEJB;

	
	private ItemUnitModel itemUnitModel;
	private List<ItemUnitModel> lstItemUnit;
	
	public ItemUnitModel getItemUnitModel() {
		
		if(itemUnitModel == null)
			itemUnitModel = new ItemUnitModel();
		return itemUnitModel;
	}
	public void setItemUnitModel(ItemUnitModel itemUnitModel) {
		this.itemUnitModel = itemUnitModel;
	}
	public List<ItemUnitModel> getLstItemUnit() {
		if (lstItemUnit == null)
			lstItemUnit = new ArrayList<>();
			lstItemUnit = itemUnitEJB.findAll();
		return lstItemUnit;
	}
	public void setLstItemUnit(List<ItemUnitModel> lstItemUnit) {
		this.lstItemUnit = lstItemUnit;
	}
	
	public void saveUnit()
	{
		if (itemUnitModel.getUnitId() == 0){
		itemUnitEJB.save(itemUnitModel);
		displayInfoMessageToUser("Save Successfull");
		}
		else
		{
			itemUnitEJB.update(itemUnitModel);
			displayInfoMessageToUser("Update Successfull");
		}
		itemUnitModel =  null;
		
	}
	public void deleteUnit()
	{
		itemUnitEJB.delete(itemUnitModel.getUnitId(), ItemUnitModel.class );
		displayInfoMessageToUser("Delete Successfull!");
		itemUnitModel = null;
	}

}
