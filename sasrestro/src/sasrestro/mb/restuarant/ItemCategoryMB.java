package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.misc.AbstractMB;
import sasrestro.model.restaurant.ItemCategoryModel;
import sasrestro.model.restaurant.ItemClassModel;
import sasrestro.sessionejb.restaurant.ItemCategoryEJB;

@ViewScoped
@ManagedBean(name="itemCategoryMB")
public class ItemCategoryMB extends AbstractMB implements Serializable {
	
	@EJB
	ItemCategoryEJB itemCategoryEJB;
	
	private static final long serialVersionUID = 1L;
	
	private ItemCategoryModel itemCategoryModel;
	List<ItemCategoryModel> lstItemCategory;

	public ItemCategoryModel getItemCategoryModel() {
		if (itemCategoryModel == null)
			itemCategoryModel = new ItemCategoryModel();
		return itemCategoryModel;
	}

	public void setItemCategoryModel(ItemCategoryModel itemCategoryModel) {
		this.itemCategoryModel = itemCategoryModel;
	}
	
	public void saveCategory()
	{
		if (itemCategoryModel.getCategoryId() == 0){
			itemCategoryEJB.save(itemCategoryModel);
		displayInfoMessageToUser("Save Successfull");
		}
		else
		{
			itemCategoryEJB.update(itemCategoryModel);
			displayInfoMessageToUser("Update Successfull");
		}
		itemCategoryModel =  null;
		
	}
	public void deleteCategory()
	{
		itemCategoryEJB.delete(itemCategoryModel.getCategoryId(), ItemCategoryModel.class );
		displayInfoMessageToUser("Delete Successfull!");
		itemCategoryModel = null;
	}

	public List<ItemCategoryModel> getLstItemCategory() {
		if(lstItemCategory == null)
			lstItemCategory = new ArrayList<>();
		lstItemCategory = itemCategoryEJB.findAll();
		return lstItemCategory;
	}

	public void setLstItemCategory(List<ItemCategoryModel> lstItemCategory) {
		this.lstItemCategory = lstItemCategory;
	}
}
