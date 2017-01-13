package sasrestro.mb.restuarant;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.faces.context.FacesContext;
import javax.faces.event.MethodExpressionActionListener;

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
import sasrestro.model.account.AccHeadMap;
import sasrestro.model.account.JournalPk;
import sasrestro.model.account.JournalVoucherDetailModel;
import sasrestro.model.account.JournalVoucherModel;
import sasrestro.model.restaurant.ItemClassModel;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.account.AccHeadMapEJB;
import sasrestro.sessionejb.account.CodeValueEJB;
import sasrestro.sessionejb.account.JournalVoucherEJB;
import sasrestro.sessionejb.restaurant.OrderItemEJB;
import sasrestro.sessionejb.restaurant.TableEJB;
import sasrestro.sessionejb.util.VatSettingEJB;

@ManagedBean(name = "newDashBoardMB")
@ViewScoped
public class NewDashBoardMB extends AbstractMB implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_COLUMN_COUNT = 3;
	private int columnCount = DEFAULT_COLUMN_COUNT;

	@EJB
	OrderItemEJB orderItemEJB;

	@EJB
	TableEJB tableEJB;

	@EJB
	VatSettingEJB vatEJB;

	@EJB
	AccHeadMapEJB accHeadMapEJB;

	@EJB
	CodeValueEJB codeValueEJB;

	@EJB
	JournalVoucherEJB journalEJB;

	@ManagedProperty(value = UserMB.INJECTION_NAME)
	private UserMB userMB;

	private List<OrderModel> lstOrder, lstTable;
	private List<TableModel> lstAllTable;
	private TableModel tableModel, mergeTable;
	private Dashboard dashboard;
	private double billAmout, vat, serviceCharge, billTotal, cashAmount, bankAmount, chequeNum, creditAmount;
	private String customerName;
	private boolean chkCash, chkBank, chkCredit;
	private ItemClassModel itemClass;

	public List<OrderModel> getLstOrder() {
		if (lstOrder == null)
			lstOrder = new ArrayList<>();
		return lstOrder;
	}

	public void setLstOrder(List<OrderModel> lstOrder) {
		this.lstOrder = lstOrder;
	}

	public UserMB getUserMB() {
		return userMB;
	}

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
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

	public double getBillTotal() {
		return billTotal;
	}

	public void setBillTotal(double billTotal) {
		this.billTotal = billTotal;
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

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public double getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(double bankAmount) {
		this.bankAmount = bankAmount;
	}

	public double getChequeNum() {
		return chequeNum;
	}

	public void setChequeNum(double chequeNum) {
		this.chequeNum = chequeNum;
	}

	public double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public boolean isChkCash() {
		return chkCash;
	}

	public void setChkCash(boolean chkCash) {
		this.chkCash = chkCash;
	}

	public boolean isChkBank() {
		return chkBank;
	}

	public void setChkBank(boolean chkBank) {
		this.chkBank = chkBank;
	}

	public boolean isChkCredit() {
		return chkCredit;
	}

	public void setChkCredit(boolean chkCredit) {
		this.chkCredit = chkCredit;
	}

	public ItemClassModel getItemClass() {
		if (itemClass == null)
			itemClass = new ItemClassModel();
		return itemClass;
	}

	public void setItemClass(ItemClassModel itemClass) {
		this.itemClass = itemClass;
	}

	@PostConstruct
	public void init() {
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
			panel.setClosable(false);
			panel.setToggleable(true);

			getDashboard().getChildren().add(panel);
			// refreshChildren(panel, tblModel.getTableId());
			DashboardColumn column = model.getColumn(i % getColumnCount());
			column.addWidget(panel.getId());

			CommandButton submit = new CommandButton();
			submit.setValue("View");
			submit.setIcon("ui-icon-zoomin");
			submit.setUpdate(":activeBill");
			submit.setId("createkjkas" + i);
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

			panel.getChildren().add(submit);

		}
	}

	public void loadBill(int tableId) {
		AccHeadMap vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");
		AccHeadMap servChargeAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge");
		DecimalFormat df = new DecimalFormat("#.##");

		lstOrder = new ArrayList<OrderModel>();
		lstOrder = orderItemEJB.getOrdersFromActiveTable(tableId);
		tableModel = tableEJB.find(tableId);
		billAmout = 0;

		// lstOrder = orderItemEJB.findAll();
		for (OrderModel ord : lstOrder) {
			double amt = ord.getQuantity() * ord.getItemId().getPrice();
			billAmout += amt;
		}
		billAmout = Double.valueOf(df.format(billAmout));
		vat = billAmout * vatEJB.getVatSettingByMapId(vatAccHeadMap.getAccHeadMapId()).getPercent() / 100;
		// vat = billAmout *13/ 100;
		vat = Double.valueOf(df.format(vat));
		serviceCharge = (billAmout + vat)
				* vatEJB.getVatSettingByMapId(servChargeAccHeadMap.getAccHeadMapId()).getPercent() / 100;
		// serviceCharge = (billAmout + vat)* 10/ 100;

		serviceCharge = Double.valueOf(df.format(serviceCharge));
		billTotal = billAmout + vat + serviceCharge;
		billTotal = Double.valueOf(df.format(billTotal));

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("activeBillDia.show();");
		context.update(":activeBill");

	}

	public void mergeTable() {
		List<OrderModel> lstOrderModel = new ArrayList<>();
		lstOrderModel = new ArrayList<OrderModel>();
		lstOrderModel = orderItemEJB.getOrdersFromActiveTable(tableModel.getTableId());
		mergeTable = tableEJB.find(mergeTable.getTableId());
		for (OrderModel order : lstOrderModel) {
			order.setTable_id(mergeTable);
		}
		orderItemEJB.updateList(lstOrderModel);
		displayInfoMessageToUser(
				tableModel.getTableName() + " merged to " + mergeTable.getTableName() + " Successfully");
	}

	public void saveReceipt() {
		if (bankAmount > 0 && chequeNum < 0) {
			displayErrorMessageToUser("Cheque number required.");
			return;
		} else if (chequeNum > 0 && bankAmount < 0) {
			displayErrorMessageToUser("Bank Amount is required.");
			return;
		}

		if (creditAmount > 0 && customerName == "") {
			displayErrorMessageToUser("Customer name is required.");
			return;
		}

		JournalVoucherModel jvm = getJVListForSave();

		if (jvm == null) {
			return;
		} else if (journalEJB.postToLedgerDirectApproval(jvm)) {
			resetReceipt();
			displayInfoMessageToUser("Receipt done successfully.");
		} else {
			displayErrorMessageToUser("Error occured while saving receipt./nPlease try again later.");
		}
	}

	private void resetReceipt() {
		billTotal = 0;
		cashAmount = 0;
		bankAmount = 0;
		chequeNum = 0;
		creditAmount = 0;
		customerName = "";
	}

	private JournalVoucherModel getJVListForSave() {
		AccHeadMap cashAccHeadMap = accHeadMapEJB.getByMapPurpose("Cash");
		AccHeadMap debitorAccHeadMap = accHeadMapEJB.getByMapPurpose("Debitor");
		AccHeadMap salesAccHeadMap = accHeadMapEJB.getByMapPurpose("Sales");

		List<JournalVoucherDetailModel> jvdmList = new ArrayList<JournalVoucherDetailModel>();
		JournalVoucherModel jvm = new JournalVoucherModel();

		if (cashAccHeadMap == null || debitorAccHeadMap == null || salesAccHeadMap == null) {
			displayErrorMessageToUser("Cash, Debitor or sales accounts need to be mapped.");
			return null;
		}

		jvm.setReceiptAmt(billTotal);
		jvm.setStatus(0);
		jvm.setRvFlag(0);
		jvm.setToFromType(1);
		jvm.setJvDateAd(userMB.getUser().getDayInStatus().getDayInDateEn());
		jvm.setCreatedDate(new Date());
		jvm.setJvDateBs(userMB.getUser().getDayInStatus().getDayInDateNp());
		jvm.setJvType(codeValueEJB.findCodeValueByCVType("Journal Voucher Type", "Receipt Voucher").get(0));
		jvm.setPostedBy(userMB.getUser());
		jvm.setUpdatedBy(userMB.getUser());
		jvm.setBrId(1);
		jvm.setCreatedBy(userMB.getUser());
		jvm.setApprovedBy(userMB.getUser());
		jvm.setAuditedBy(userMB.getUser());
		jvm.setReceivedBy(userMB.getUser());
		jvm.setSubmittedBy(userMB.getUser());
		jvm.setNarration("Receipt from sales.");
		if (chequeNum > 0) {
			jvm.setChequeNo(String.valueOf(chequeNum));
		}

		JournalVoucherDetailModel jvd;

		if (bankAmount > 0 && chequeNum > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(cashAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(bankAmount);
			jvd.setCrAmt(0);
			jvd.setNarration("Receipt from cheque no" + chequeNum);
			jvd.setJvm(jvm);
			jvdmList.add(jvd);
		}

		if (cashAmount > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(cashAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(cashAmount);
			jvd.setCrAmt(0);
			jvd.setNarration("Recepit from cash.");
			jvd.setJvm(jvm);
			jvdmList.add(jvd);
		}

		if (creditAmount > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(debitorAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(creditAmount);
			jvd.setCrAmt(0);
			jvd.setNarration("Credit recepit from " + customerName);
			jvd.setJvm(jvm);
			jvdmList.add(jvd);
		}

		jvd = new JournalVoucherDetailModel();
		jvd.setAccountHead(salesAccHeadMap.getAccHeadModel());
		jvd.setDrAmt(0);
		jvd.setCrAmt(billTotal);
		jvd.setNarration("Receipt from sales.");
		jvd.setJvm(jvm);
		jvdmList.add(jvd);

		jvm.setJvdmList(jvdmList);

		jvm.setFiscalYrModel(userMB.getUser().getFyModel());

		if (jvm.getJournalPk().getJvNo() <= 0 && jvm.getJournalPk().getFyId() <= 0
				&& jvm.getJournalPk().getJvType() <= 0) {
			JournalPk jp = new JournalPk();
			jp.setFyId(jvm.getFiscalYrModel().getFyId());
			jp.setJvType(jvm.getJvType().getCvId());
			jp.setJvNo(journalEJB.getMaxJvNoWithJvType(jvm.getFiscalYrModel().getFyId(), jvm.getJvType().getCvId()));
			jvm.setJournalPk(jp);
		}

		return jvm;
	}

	public void loadBillByClass() {
		List<OrderModel> lstOrderTemp = new ArrayList<OrderModel>();
		lstOrder = orderItemEJB.getOrdersFromActiveTable(tableModel.getTableId());

		getItemClass();
		for (OrderModel od : lstOrder) {
			if (od.getItemId().getItemClass().getClassId() == itemClass.getClassId()) {
				lstOrderTemp.add(od);
			}
		}
		lstOrder = new ArrayList<>(lstOrderTemp);

	}
}
