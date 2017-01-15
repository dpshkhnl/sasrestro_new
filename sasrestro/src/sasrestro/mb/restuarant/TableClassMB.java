package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.misc.AbstractMB;
import sasrestro.model.restaurant.TableClass;
import sasrestro.sessionejb.restaurant.TableClassEJB;

@ViewScoped
@ManagedBean(name="tableClassMB")
public class TableClassMB extends AbstractMB implements Serializable {
	
	@EJB
	TableClassEJB tableClassEJB;
	
	private static final long serialVersionUID = 1L;
	private TableClass tableClassModel;
	private List<TableClass> lstTableClass;
	private List<TableClass> lstAllTableClass;
	
	public TableClass getTableClass() {
		
		if(tableClassModel == null)
			tableClassModel = new TableClass();
		return tableClassModel;
	}
	public void setTableClass(TableClass tableClassModel) {
		this.tableClassModel = tableClassModel;
	}
	public List<TableClass> getLstTableClass() {
		if (lstTableClass == null)
			lstTableClass = new ArrayList<>();
			lstTableClass = tableClassEJB.findAll();
		return lstTableClass;
	}
	public void setLstTableClass(List<TableClass> lstTableClass) {
		this.lstTableClass = lstTableClass;
	}
	
	public void saveClass()
	{
		if (tableClassModel.getClassId() == 0){
		tableClassEJB.save(tableClassModel);
		displayInfoMessageToUser("Save Successfull");
		}
		else
		{
			tableClassEJB.update(tableClassModel);
			displayInfoMessageToUser("Update Successfull");
		}
		tableClassModel =  null;
		
	}
	public void deleteClass()
	{
		tableClassEJB.delete(tableClassModel.getClassId(), TableClass.class );
		displayInfoMessageToUser("Delete Successfull!");
		tableClassModel = null;
	}
	public List<TableClass> getLstAllTableClass() {
		return lstAllTableClass;
	}
	public void setLstAllTableClass(List<TableClass> lstAllTableClass) {
		this.lstAllTableClass = lstAllTableClass;
	}

}
