package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.misc.AbstractMB;
import sasrestro.model.restaurant.ItemClassModel;
import sasrestro.model.restaurant.MenuItemModel;
import sasrestro.sessionejb.restaurant.ItemClassEJB;

@ViewScoped
@ManagedBean(name="itemClassMB")
public class ItemClassMB extends AbstractMB implements Serializable {
	
	@EJB
	ItemClassEJB itemClassEJB;
	
	private static final long serialVersionUID = 1L;
	private ItemClassModel itemClassModel;
	private List<ItemClassModel> lstItemClass;
	
	public ItemClassModel getItemClassModel() {
		
		if(itemClassModel == null)
			itemClassModel = new ItemClassModel();
		return itemClassModel;
	}
	public void setItemClassModel(ItemClassModel itemClassModel) {
		this.itemClassModel = itemClassModel;
	}
	public List<ItemClassModel> getLstItemClass() {
		if (lstItemClass == null)
			lstItemClass = new ArrayList<>();
			lstItemClass = itemClassEJB.findAll();
		return lstItemClass;
	}
	public void setLstItemClass(List<ItemClassModel> lstItemClass) {
		this.lstItemClass = lstItemClass;
	}
	
	public void saveClass()
	{
		if (itemClassModel.getClassId() == 0){
		itemClassEJB.save(itemClassModel);
		displayInfoMessageToUser("Save Successfull");
		}
		else
		{
			itemClassEJB.update(itemClassModel);
			displayInfoMessageToUser("Update Successfull");
		}
		itemClassModel =  null;
		
	}
	public void deleteClass()
	{
		itemClassEJB.delete(itemClassModel.getClassId(), ItemClassModel.class );
		displayInfoMessageToUser("Delete Successfull!");
		itemClassModel = null;
	}

}
