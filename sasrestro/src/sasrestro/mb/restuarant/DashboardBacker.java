package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;

import org.primefaces.component.column.Column;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.effect.Effect;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.scrollpanel.ScrollPanel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import sasrestro.mb.user.UserMB;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableClass;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.restaurant.OrderItemEJB;
import sasrestro.sessionejb.restaurant.TableClassEJB;
import sasrestro.sessionejb.restaurant.TableEJB;

@ManagedBean(name = "dashboardBacker")
@ViewScoped
public class DashboardBacker implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_COLUMN_COUNT = 3;
	private int columnCount = DEFAULT_COLUMN_COUNT;
	DecimalFormat df = new DecimalFormat("##.##");

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
	
	@EJB
	TableClassEJB tableClassEJB;
	
/*	@PostConstruct
	public void init() {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard", "org.primefaces.component.DashboardRenderer");
		dashboard.setId("dashboard");

		DashboardModel model = new DefaultDashboardModel();
		for( int i = 0, n = getColumnCount(); i < n; i++ ) {
			DashboardColumn column = new DefaultDashboardColumn();
			model.addColumn(column);
		}
		dashboard.setModel(model);

		List<TableClass> lstTableClass = new ArrayList<>();
		lstTableClass = tableClassEJB.findAll();
		int a = 0;
		for (TableClass tableClass:lstTableClass){
			a++;
			Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel", "org.primefaces.component.PanelRenderer");
			panel.setId("measure_" + a);
			panel.setHeader(tableClass.getClassName());
			panel.setClosable(true);
			
			panel.setStyle("margin-left:10px;margin-bottom:10px;width:700px");
			panel.setToggleable(true);
			
			

			getDashboard().getChildren().add(panel);
			DashboardColumn column = model.getColumn(a%getColumnCount());
			column.addWidget(panel.getId());
			HtmlOutputText text = new HtmlOutputText();
			text.setValue("Dashboard widget bits!" );

			panel.getChildren().add(text);
		}
	}*/


	@PostConstruct
	public void init() {
		
	}

	public Dashboard getDashboard() {
		dashboard = null;
		dashboard = new  Dashboard();
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
		
		
		
		Panel panelHead = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
				"org.primefaces.component.PanelRenderer");
		panelHead.setId("PanelHead");
		panelHead.setStyle("width:1050px;height:500px");
		panelHead.setClosable(false);
		

		List<TableClass> lstTableClass = new ArrayList<>();
		lstTableClass = tableClassEJB.findAll();
		int a = 0;
		for (TableClass tableClass:lstTableClass){
			a++;
			
			
			Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
					"org.primefaces.component.PanelRenderer");
			panel.setId("mainpanel"+a );
			panel.setHeader(tableClass.getClassName());
			panel.setStyle("width:950px");
			panel.setClosable(false);
			panel.setToggleable(true);
			panel.setCollapsed(true);
			panel.setStyleClass("customTitleBar");
			PanelGrid panelGrid = (PanelGrid) application.createComponent(fc, "org.primefaces.component.PanelGrid",
					"org.primefaces.component.PanelGridRenderer");
			panelGrid.setId("mainpanelGrid"+a );
			panelGrid.setColumns(4);
			
		List<TableModel> lstTable = tableEJB.getByTableClass(tableClass.getClassId());

		for (int i = 0, n = lstTable.size(); i < n; i++) {
			TableModel tblModel = new TableModel();
			tblModel = tableEJB.find(lstTable.get(i).getTableId());
			Panel panelInside = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
					"org.primefaces.component.PanelRenderer");
			panelInside.setId("measure_" + i+a);
			panelInside.setHeader(tblModel.getTableName());
			panelInside.setClosable(false);
			
			panelInside.setStyle("margin-left:5px;width:190px");
			panelInside.setToggleable(true);
			
			
			//getDashboard().getChildren().add(panel);
			// refreshChildren(panel, tblModel.getTableId());
		
			List<OrderModel> lstOrders = new ArrayList<OrderModel>();
			lstOrders = orderItemEJB.getOrdersFromActiveTable(tblModel.getTableId());
			double billAmount = 0.0;
			String activeSince="N/A";
			
			
			if (lstOrders.size() >0){
			Effect effect = new Effect();
			effect.setType("pulsate");
			effect.setEvent("load");
			panelInside.getChildren().add(effect);
			panelInside.setStyleClass("active");
			Date firstOrder = new Date();
			for (OrderModel order : lstOrders)
			{
				double amt = order.getQuantity() * order.getItemId().getPrice();
				billAmount+=amt;
				if (firstOrder.after(order.getOrderTime()))
				{
					firstOrder = order.getOrderTime();
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
			activeSince = sdf.format(firstOrder);
			}
			else {
				panelInside.setStyleClass("customTitleBar");
			}
			
			HtmlOutputText text = new HtmlOutputText();
			text.setValue( "Bill Amount:" );
			
			HtmlOutputText textAmt = new HtmlOutputText();
			textAmt.setValue( " Rs."+df.format(billAmount)+" " );

			
			HtmlOutputText text1 = new HtmlOutputText();
			text1.setValue( "Logged In time : ");

			HtmlOutputText textLoggedin = new HtmlOutputText();
			textLoggedin.setValue(activeSince +" ");


			HtmlOutputText text2 = new HtmlOutputText();
			text2.setValue("Active Since: ");

			
			HtmlOutputText textActiveFrom = new HtmlOutputText();
			textActiveFrom.setValue(" N/A");
			/*
			 * CommandButton button = new CommandButton();
			 * button.setValue("Print Bill");
			 */

			panelInside.getChildren().add(text);
			panelInside.getChildren().add(textAmt);
			panelInside.getChildren().add(text1);
			panelInside.getChildren().add(textLoggedin);
			panelInside.getChildren().add(text2);
			panelInside.getChildren().add(textActiveFrom);
			// panel.getChildren().add(button);

			// }
			

			CommandButton submit = new CommandButton();
			submit.setValue("View");
			submit.setIcon("ui-icon-zoomin");
			submit.setUpdate(":activeBill");
			submit.setId("create" + i+"-"+a);
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			ELContext elContext = facesCtx.getELContext();
			Application app = facesCtx.getApplication();
			ExpressionFactory elFactory = app.getExpressionFactory();
			MethodExpression methodExpression = null;
			methodExpression = elFactory.createMethodExpression(elContext,
					"#{newDashBoardMB.loadBill(" + tblModel.getTableId() + ")}", null, new Class[] {});
			// submit.setActionExpression(methodExpression);
			submit.addActionListener(new MethodExpressionActionListener(methodExpression));
			submit.setActionExpression(methodExpression);
			panelInside.getChildren().add(submit);
			
			CommandButton merge = new CommandButton();
			merge.setValue("Merge");
			merge.setIcon("ui-icon-shuffle");
			merge.setUpdate(":mergeTableForm");
			merge.setId("mergenew" + i+"-"+a);
			MethodExpression methodExpression1 = null;
			methodExpression1 = elFactory.createMethodExpression(elContext,
					"#{newDashBoardMB.loadMergeDialog(" + tblModel.getTableId() + ")}", null, new Class[] {});
			// merge.setActionExpression(methodExpression);
			merge.addActionListener(new MethodExpressionActionListener(methodExpression1));
			merge.setActionExpression(methodExpression1);
			

			panelInside.getChildren().add(merge);
			panelGrid.getChildren().add(panelInside);

		}
		
		
		panel.getChildren().add(panelGrid);
		panelHead.getChildren().add(panel);
		
		
		}
		
		DashboardColumn column = model.getColumn(a % getColumnCount());
		column.addWidget(panelHead.getId());
		dashboard.getChildren().add(panelHead);
		
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

	private void refreshChildren(Panel panel, int tableid) {
		panel.getChildren().clear();

		List<OrderModel> lstOrder = new ArrayList<OrderModel>();
		lstOrder = orderItemEJB.getOrdersFromActiveTable(tableid);
		// lstOrder = orderItemEJB.findAll();

		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();
		ExpressionFactory ef = application.getExpressionFactory();
		ELContext elc = fc.getELContext();

		DataTable table = (DataTable) application.createComponent(DataTable.COMPONENT_TYPE);
		table.setValue(lstOrder);
		table.setVar("item");
		UIOutput tableTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		tableTitle.setValue("Table Title");
		table.getFacets().put("header", tableTitle);
		// Index
		Column indexColumn = (Column) application.createComponent(Column.COMPONENT_TYPE);
		UIOutput indexColumnTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		indexColumnTitle.setValue("Sno");
		indexColumn.getFacets().put("header", indexColumnTitle);
		// table.getColumns().add(column);
		table.getChildren().add(indexColumn);

		/*
		 * Column orderTime = (Column)
		 * application.createComponent(Column.COMPONENT_TYPE); UIOutput
		 * orderTimeTitle =
		 * (UIOutput)application.createComponent(UIOutput.COMPONENT_TYPE);
		 * orderTimeTitle.setValue("Order Time");
		 * orderTime.getFacets().put("header", orderTimeTitle);
		 * table.getChildren().add( orderTime );
		 * 
		 * ValueExpression orderExp = ef.createValueExpression(elc,
		 * "#{item.orderTime}", Object.class); HtmlOutputText orderOutput =
		 * (HtmlOutputText)application.createComponent(
		 * HtmlOutputText.COMPONENT_TYPE );
		 * orderOutput.setValueExpression("value", orderExp);
		 * orderTime.getChildren().add( orderOutput );
		 */

		ValueExpression indexValueExp = ef.createValueExpression(elc, "", Object.class);
		HtmlOutputText indexOutput = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		indexOutput.setValueExpression("value", indexValueExp);
		indexColumn.getChildren().add(indexOutput);

		// Name Column
		Column nameColumn = (Column) application.createComponent(Column.COMPONENT_TYPE);
		UIOutput nameColumnTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		nameColumnTitle.setValue("Name");
		nameColumn.getFacets().put("header", nameColumnTitle);
		table.getChildren().add(nameColumn);

		ValueExpression nameValueExp = ef.createValueExpression(elc, "#{item.itemId.name}", Object.class);
		HtmlOutputText nameOutput = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		nameOutput.setValueExpression("value", nameValueExp);
		nameColumn.getChildren().add(nameOutput);

		Column priceColumn = (Column) application.createComponent(Column.COMPONENT_TYPE);
		UIOutput priceColumnTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		priceColumnTitle.setValue("Price");
		priceColumn.getFacets().put("header", priceColumnTitle);
		table.getChildren().add(priceColumn);

		ValueExpression priceValueExp = ef.createValueExpression(elc, "#{item.itemId.price}", Object.class);
		HtmlOutputText priceOutput = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		priceOutput.setValueExpression("value", priceValueExp);
		priceColumn.getChildren().add(priceOutput);

		Column qntyColumn = (Column) application.createComponent(Column.COMPONENT_TYPE);
		UIOutput qntyColumnTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		qntyColumnTitle.setValue("Quantity");
		qntyColumn.getFacets().put("header", qntyColumnTitle);
		table.getChildren().add(qntyColumn);

		ValueExpression qntyValueExp = ef.createValueExpression(elc, "#{item.quantity}", Object.class);
		HtmlOutputText qntyOutput = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		qntyOutput.setValueExpression("value", qntyValueExp);
		qntyColumn.getChildren().add(qntyOutput);

		Column totColumn = (Column) application.createComponent(Column.COMPONENT_TYPE);
		UIOutput totColumnTitle = (UIOutput) application.createComponent(UIOutput.COMPONENT_TYPE);
		totColumnTitle.setValue("Total");
		totColumn.getFacets().put("header", totColumnTitle);
		table.getChildren().add(totColumn);

		ValueExpression totValueExp = ef.createValueExpression(elc, "#{item.itemId.price * item.quantity}",
				Object.class);
		HtmlOutputText totOutput = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		totOutput.setValueExpression("value", totValueExp);
		totColumn.getChildren().add(totOutput);

		panel.getChildren().add(table);
	}

}
