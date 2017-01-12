package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.MethodExpressionActionListener;

import org.primefaces.component.behavior.ajax.AjaxBehavior;
import org.primefaces.component.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import sasrestro.mb.user.UserMB;
import sasrestro.misc.AbstractMB;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.restaurant.OrderItemEJB;
import sasrestro.sessionejb.restaurant.TableEJB;


@ManagedBean(name="newDashBoardMB")
@ViewScoped
public class NewDashBoardMB extends AbstractMB implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_COLUMN_COUNT = 3;
	private int columnCount = DEFAULT_COLUMN_COUNT;
	
	List<OrderModel> lstOrder ;
	private TableModel tableModel;
	private List<TableModel> lstAllTable;
	double billAmout;
	double vat;
	double serviceCharge;
	private TableModel mergeTable;
	List<OrderModel> lstTable;
	public List<OrderModel> getLstOrder() {
		if (lstOrder == null)
			lstOrder = new ArrayList<>();
		return lstOrder;
	}

	public void setLstOrder(List<OrderModel> lstOrder) {
		this.lstOrder = lstOrder;
	}

	@ManagedProperty(value = UserMB.INJECTION_NAME)
	private UserMB userMB;

	public UserMB getUserMB() {
		return userMB;
	}

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	private Dashboard dashboard;

	@EJB
	OrderItemEJB orderItemEJB;

	@EJB
	TableEJB tableEJB;

	@PostConstruct
	public void init() {

		DecimalFormat d = new DecimalFormat("#.##");
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();
		dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard",
				"org.primefaces.component.DashboardRenderer");
		dashboard.setId("dashboard");

		DashboardModel model = new DefaultDashboardModel();
		for (int i = 0, n = getColumnCount(); i < n; i++) {
			DashboardColumn column = new DefaultDashboardColumn();
			model.addColumn(column);
		}
		dashboard.setModel(model);

		List<OrderModel> lstOrders = new ArrayList<OrderModel>();
		lstOrders = orderItemEJB.getDistinctActiveTable();

		for (int i = 0, n = lstOrders.size(); i < n; i++) {
			TableModel tblModel = new TableModel();
			tblModel = tableEJB.find(lstOrders.get(i).getTable_id().getTableId());
			Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
					"org.primefaces.component.PanelRenderer");
			panel.setId("measure_" + i);
			panel.setHeader(tblModel.getTableName());
			panel.setStyleClass("panel panel-primary");
			panel.setClosable(false);
			panel.setToggleable(true);

			getDashboard().getChildren().add(panel);
			//refreshChildren(panel, tblModel.getTableId());
			DashboardColumn column = model.getColumn(i % getColumnCount());
			column.addWidget(panel.getId());
			
			CommandButton submit = new CommandButton();
			submit.setValue("View");
			
			submit.setStyleClass("normal");
			submit.setIcon("ui-icon-zoomin");
			submit.setId("createkjkas"+i);
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			ELContext elContext = facesCtx.getELContext();
			Application app = facesCtx.getApplication();
			ExpressionFactory elFactory = app.getExpressionFactory();
			MethodExpression methodExpression =null;
			methodExpression = elFactory.createMethodExpression(elContext,"#{newDashBoardMB.loadBill("+tblModel.getTableId()+")}",null, new Class[]{});
			//submit.setActionExpression(methodExpression);
			submit.addActionListener(new MethodExpressionActionListener(methodExpression));
			submit.setActionExpression(methodExpression);
			
			
			
			/*FacesContext context = FacesContext.getCurrentInstance();
			MethodExpression methodExpression = context.getApplication().getExpressionFactory().createMethodExpression(
			    context.getELContext(), "#{newDashBoardMB.loadBill("+tblModel.getTableId()+")}", null,null);

			submit.addActionListener(new MethodExpressionActionListener(methodExpression));*/
			
			panel.getChildren().add(submit);

			
		}
	}
	
	public void loadBill(int tableId){
		
		lstOrder = new ArrayList<OrderModel>();
		lstOrder = orderItemEJB.getOrdersFromActiveTable(tableId);
		tableModel = tableEJB.find(tableId);
		
		// lstOrder = orderItemEJB.findAll();
		for (OrderModel ord : lstOrder) {
			double amt = ord.getQuantity() * ord.getItemId().getPrice();
			billAmout += amt;
		}
		vat = billAmout * 0.13;
		serviceCharge = (billAmout+vat) * 0.10;
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("activeBillDia.show();");
		context.update(":activeBill");
		
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public TableModel getTableModel() {
		if (tableModel == null)
			tableModel = new TableModel();
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public double getBillAmout() {
		return billAmout;
	}

	public void setBillAmout(double billAmout) {
		this.billAmout = billAmout;
	}

	public double getVat() {
		return vat;
	}

	public void setVat(double vat) {
		this.vat = vat;
	}

	public double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public List<TableModel> getLstAllTable() {
		if (lstAllTable == null)
			lstAllTable = new ArrayList<>();
		lstAllTable = tableEJB.findAll();
		return lstAllTable;
	}

	public void setLstAllTable(List<TableModel> lstAllTable) {
		this.lstAllTable = lstAllTable;
	}
	
	public void mergeTable()
	{
		List<OrderModel> lstOrderModel = new ArrayList<>();
		lstOrderModel = new ArrayList<OrderModel>();
		lstOrderModel = orderItemEJB.getOrdersFromActiveTable(tableModel.getTableId());
		mergeTable = tableEJB.find(mergeTable.getTableId());
		for (OrderModel order : lstOrderModel)
		{
			order.setTable_id(mergeTable);
		}
		orderItemEJB.updateList(lstOrderModel);
		displayInfoMessageToUser(tableModel.getTableName() +" merged to "+mergeTable.getTableName()+" Successfully");
	}

	public TableModel getMergeTable() {
		if (mergeTable == null)
			mergeTable = new TableModel();
		return mergeTable;
	}

	public void setMergeTable(TableModel mergeTable) {
		this.mergeTable = mergeTable;
	}

	public List<OrderModel> getLstTable() {
		lstTable = new ArrayList<OrderModel>();
		lstTable = orderItemEJB.getDistinctActiveTable();
		return lstTable;
	}

	public void setLstTable(List<OrderModel> lstTable) {
		this.lstTable = lstTable;
	}
	
	
}