package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import sasrestro.misc.AbstractMB;
import sasrestro.misc.DirectSqlUtils;
import sasrestro.model.restaurant.BillReportModel;
import sasrestro.model.restaurant.ItemCategoryModel;
import sasrestro.model.restaurant.ItemUnitModel;
import sasrestro.model.restaurant.MenuItemModel;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.restaurant.ItemCategoryEJB;
import sasrestro.sessionejb.restaurant.ItemUnitEJB;
import sasrestro.sessionejb.restaurant.MenuItemEJB;
import sasrestro.sessionejb.restaurant.OrderItemEJB;
import sasrestro.sessionejb.restaurant.TableEJB;

@ViewScoped
@ManagedBean(name="orderMB")
public class OrderMB extends AbstractMB implements Serializable {
	
	
	@EJB
	TableEJB tableEJB;
	
	@EJB
	ItemCategoryEJB itemCatEJB;
	
	@EJB
	MenuItemEJB itemEJB;
	
	@EJB
	ItemUnitEJB itemUnitEJB;
	
	@EJB
	OrderItemEJB orderItemEJB;

	private static final long serialVersionUID = 1L;
	
	private OrderModel orderModel;
	private List<TableModel> lsttAllTables;
	private List<ItemCategoryModel> lstAllItemCat;
	private List<MenuItemModel> lstMenuItem;
	private List<ItemUnitModel> lstUnit;
	private List<OrderModel> lstOrderModel ;
	List<BillReportModel> lstBillItem;
	private JasperPrint jasperPrint;
	int catId;

	public OrderModel getOrderModel() {
		if (orderModel == null)
			orderModel = new OrderModel();
		if (orderModel.getItemId() == null)
			orderModel.setItemId(new MenuItemModel());
		if (orderModel.getTable_id() == null)
			orderModel.setTable_id(new TableModel());
		if (orderModel.getUnit() == null)
			orderModel.setUnit(new ItemUnitModel());
		
		return orderModel;
	}

	public void setOrderModel(OrderModel orderModel) {
		this.orderModel = orderModel;
	}

	public List<TableModel> getLsttAllTables() {
		if (lsttAllTables == null)
			lsttAllTables = new ArrayList<>();
		lsttAllTables = tableEJB.findAll();
		return lsttAllTables;
	}

	public void setLsttAllTables(List<TableModel> lsttAllTables) {
		this.lsttAllTables = lsttAllTables;
	}

	public List<ItemCategoryModel> getLstAllItemCat() {
		if (lstAllItemCat == null)
			lstAllItemCat = new ArrayList<>();
		lstAllItemCat = itemCatEJB.findAll();
		return lstAllItemCat;
	}

	public void setLstAllItemCat(List<ItemCategoryModel> lstAllItemCat) {
		this.lstAllItemCat = lstAllItemCat;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public List<MenuItemModel> getLstMenuItem() {
		if (lstMenuItem == null)
			lstMenuItem = new ArrayList<>();
		if (catId != 0)
			lstMenuItem = itemEJB.getByItemCat(catId);
			else 
				lstMenuItem = itemEJB.findAll();
			
		return lstMenuItem;
	}
	
	public void addOrder()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Long tokenNo = (Long) DirectSqlUtils.getSingleValueFromTable("select IFNULL(MAX(token_no), 0) as billNo from tbl_order WHERE order_time like '"+sdf.format(new Date())+"%'");
		orderModel.setTokenNo(tokenNo.intValue()+1);
		orderModel.setOrderTime(new Date());
		orderModel.setStatus(1);
		orderModel.setItemId(itemEJB.find(orderModel.getItemId().getItemId()));
		orderModel.setTable_id(tableEJB.find(orderModel.getTable_id().getTableId()));
		orderModel.setUnit(itemUnitEJB.find(orderModel.getUnit().getUnitId()));
		lstOrderModel.add(orderModel);
		orderModel = null;
		catId = 0;
		//orderModel.set
	}
	
	public void saveOrder()
	{
		if (lstOrderModel.size()==0)
		{
			displayErrorMessageToUser("No Order to send");
			return;
		}
		
		orderItemEJB.saveList(lstOrderModel);
		sendKOTBOT();
		displayInfoMessageToUser("Order send successfully");
		lstOrderModel = null;
	}

	public void setLstMenuItem(List<MenuItemModel> lstMenuItem) {
		this.lstMenuItem = lstMenuItem;
	}

	public List<ItemUnitModel> getLstUnit() {
		if (lstUnit == null)
			lstUnit = new ArrayList<>();
		lstUnit = itemUnitEJB.findAll();
		return lstUnit;
	}

	public void setLstUnit(List<ItemUnitModel> lstUnit) {
		this.lstUnit = lstUnit;
	}

	public List<OrderModel> getLstOrderModel() {
		if (lstOrderModel == null)
			lstOrderModel = new ArrayList<>();
		return lstOrderModel;
	}

	public void setLstOrderModel(List<OrderModel> lstOrderModel) {
		this.lstOrderModel = lstOrderModel;
	}
	
	public void sendKOTBOT()
	{
		

        try {
        	
        	lstBillItem = null;
    		
    		String tableNo ="";
    		String orderTime = "";
    		int tokenNo = 0;
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    		getLstBillItem();
    		for (OrderModel ord : lstOrderModel) {
    			BillReportModel bill = new BillReportModel();
    			bill.setItemName(ord.getItemId().getName());
    			bill.setItemPrice(ord.getItemId().getPrice());
    			bill.setQuantity(ord.getQuantity());
    			if(!ord.getRemarks().equals(""))
    			bill.setItemName(ord.getItemId().getName()+"("+ord.getRemarks()+")");
    			lstBillItem.add(bill);
    			tableNo = ord.getTable_id().getTableName();
    			orderTime= sdf.format(ord.getOrderTime());
    			tokenNo = ord.getTokenNo();
    		}
    		
    		Map<String, Object> params = new HashMap<String, Object>();
    		params.put("restroName", "Restro Management");
    		params.put("restroAddress", "Kathmandu,Nepal");
    		params.put("orderTime", orderTime);
    		params.put("tokenNo",String.valueOf(tokenNo));
    		params.put("tableNo", tableNo);
    		
    		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
    				getLstBillItem());
    		String reportPath;
    		reportPath = FacesContext.getCurrentInstance().getExternalContext()
    				.getRealPath("/reports/kotbot.jasper");

    		jasperPrint = JasperFillManager.fillReport(reportPath,
    				params, beanCollectionDataSource);
            PrintReportToPrinter(jasperPrint);//call method
	}
        catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	private void PrintReportToPrinter(JasperPrint jp) throws JRException {
		 // TODO Auto-generated method stub
  	 PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet(); 
       // printRequestAttributeSet.add(MediaSizeName.ISO_A4); //setting page size
       printRequestAttributeSet.add(new Copies(1)); 

       PrinterName printerName =new PrinterName("POS-58", null); //gets printer 

       PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet(); 
       printServiceAttributeSet.add(printerName); 

       JRPrintServiceExporter exporter = new JRPrintServiceExporter(); 

       exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp); 
       exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet); 
       exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet); 
       exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE); 
       exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE); 
       exporter.exportReport();
	}

	public List<BillReportModel> getLstBillItem() {
		if (lstBillItem == null)
			lstBillItem = new ArrayList<>();
		return lstBillItem;
	}

	public void setLstBillItem(List<BillReportModel> lstBillItem) {
		this.lstBillItem = lstBillItem;
	}
	
	

}
