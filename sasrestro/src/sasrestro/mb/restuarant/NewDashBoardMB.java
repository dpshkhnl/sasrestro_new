package sasrestro.mb.restuarant;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import javafx.print.PrinterJob;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import sasrestro.mb.user.UserMB;
import sasrestro.misc.AbstractMB;
import sasrestro.misc.DirectSqlUtils;
import sasrestro.misc.JrUtils;
import sasrestro.model.account.AccHeadMap;
import sasrestro.model.account.JournalPk;
import sasrestro.model.account.JournalVoucherDetailModel;
import sasrestro.model.account.JournalVoucherModel;
import sasrestro.model.restaurant.BillDetailModel;
import sasrestro.model.restaurant.BillModel;
import sasrestro.model.restaurant.BillReportModel;
import sasrestro.model.restaurant.ItemClassModel;
import sasrestro.model.restaurant.OrderModel;
import sasrestro.model.restaurant.TableClass;
import sasrestro.model.restaurant.TableModel;
import sasrestro.sessionejb.account.AccHeadMapEJB;
import sasrestro.sessionejb.account.CodeValueEJB;
import sasrestro.sessionejb.account.JournalVoucherEJB;
import sasrestro.sessionejb.restaurant.BillEJB;
import sasrestro.sessionejb.restaurant.OrderItemEJB;
import sasrestro.sessionejb.restaurant.TableClassEJB;
import sasrestro.sessionejb.restaurant.TableEJB;
import sasrestro.sessionejb.util.VatSettingEJB;

@ManagedBean(name = "newDashBoardMB")
@ViewScoped
public class NewDashBoardMB extends AbstractMB implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_COLUMN_COUNT = 10;
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
	
	@EJB
	TableClassEJB tableClassEJB;
	
	@EJB
	BillEJB billEJB;

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
	private BillModel billModel ;
	private List<BillDetailModel> lstBillDetails;
	private double discoutPerc;
	DecimalFormat df = new DecimalFormat("#.##");
	List<BillReportModel> lstBillItem;
	private JasperPrint jasperPrint;
	private HttpServletResponse httpServletResponse;
	private ServletOutputStream servletOutputStream;

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

	public void calculateDiscount()
	{
		
			billModel.setDiscount(Double.valueOf(df.format(billAmout * discoutPerc /100)));
			getBillModel();
			billModel.setBillAmount(billAmout);
			AccHeadMap vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");
			AccHeadMap servChargeAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge");
			vat = billAmout * vatEJB.getVatSettingByMapId(vatAccHeadMap.getAccHeadMapId()).getPercent() / 100;
			// vat = billAmout *13/ 100;
			vat = Double.valueOf(df.format(vat));
			serviceCharge = (billAmout + vat)
					* vatEJB.getVatSettingByMapId(servChargeAccHeadMap.getAccHeadMapId()).getPercent() / 100;
			// serviceCharge = (billAmout + vat)* 10/ 100;

			serviceCharge = Double.valueOf(df.format(serviceCharge));
			billTotal = billAmout + vat + serviceCharge-billModel.getDiscount();
			billTotal = Double.valueOf(df.format(billTotal));
			
			billModel.setVatAmount(vat);
			billModel.setServiceCharge(serviceCharge);
			billModel.setGrandTotal(billTotal);
		
	}
	
	public void calculateDiscountPer()
	{
		
			getBillModel();
			billModel.setBillAmount(billAmout);
			AccHeadMap vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");
			AccHeadMap servChargeAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge");
			vat = (billAmout-billModel.getDiscount()) * vatEJB.getVatSettingByMapId(vatAccHeadMap.getAccHeadMapId()).getPercent() / 100;
			// vat = billAmout *13/ 100;
			vat = Double.valueOf(df.format(vat));
			serviceCharge = (billAmout + vat-billModel.getDiscount())
					* vatEJB.getVatSettingByMapId(servChargeAccHeadMap.getAccHeadMapId()).getPercent() / 100;
			// serviceCharge = (billAmout + vat)* 10/ 100;

			serviceCharge = Double.valueOf(df.format(serviceCharge));
			billTotal = billAmout + vat + serviceCharge-billModel.getDiscount();
			billTotal = Double.valueOf(df.format(billTotal));
			
			billModel.setVatAmount(vat);
			billModel.setServiceCharge(serviceCharge);
			billModel.setGrandTotal(billTotal);
			discoutPerc = Double.valueOf(df.format(billModel.getDiscount()*100/billAmout));
		
		
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

		List<TableClass> lstTableClass = new ArrayList<>();
		lstTableClass = tableClassEJB.findAll();
		int a = 0;
		for (TableClass tableClass:lstTableClass){
			a++;
			Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
					"org.primefaces.component.PanelRenderer");
			panel.setId("mainpanel"+a );
			panel.setHeader(tableClass.getClassName());
			
			panel.setClosable(false);
			panel.setToggleable(true);

		
		
		
		List<OrderModel> lstOrders = new ArrayList<OrderModel>();
		lstOrders = orderItemEJB.getDistinctActiveTable();
		List<TableModel> lstTable = tableEJB.findAll();

		for (int i = 0, n = lstTable.size(); i < n; i++) {
			TableModel tblModel = new TableModel();
			tblModel = tableEJB.find(lstTable.get(i).getTableId());
			Panel panelInside = (Panel) application.createComponent(fc, "org.primefaces.component.Panel",
					"org.primefaces.component.PanelRenderer");
			panelInside.setId("measure_" + i+a);
			panelInside.setHeader(tblModel.getTableName());
			panelInside.setClosable(false);
			panelInside.setStyle("margin-left:5px");
			panelInside.setToggleable(true);
			
			panel.getChildren().add(panelInside);
			//getDashboard().getChildren().add(panel);
			// refreshChildren(panel, tblModel.getTableId());
		
			
			
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

		}
		
		DashboardColumn column = model.getColumn(a % getColumnCount());
		column.addWidget(panel.getId());
		
		getDashboard().getChildren().add(panel);
		
		
		}
	}

	public void loadBill(int tableId) {
		
		lstOrder = new ArrayList<OrderModel>();
		lstOrder = orderItemEJB.getOrdersFromActiveTable(tableId);
		tableModel = tableEJB.find(tableId);
		billAmout = 0;

	

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

		double totalReceiptAmt = cashAmount + bankAmount + creditAmount;
		if (billTotal != totalReceiptAmt) {
			displayErrorMessageToUser("Receipt amount must be equal to bill amount.");
			return;
		}
		
		

		JournalVoucherModel jvm = getJVListForSave();
		
		if (jvm == null) {
			return;
		} else if (journalEJB.postToLedgerDirectApproval(jvm)) {
			saveBill();
			deleteOrder();
			loadDataForPrint();
			resetReceipt();
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("receiptDialog.hide();");
			context.execute("activeBillDia.hide();");
			context.update(":maina ");
			
			
			
			
			displayInfoMessageToUser("Receipt done successfully.");
		} else {
			displayErrorMessageToUser("Error occured while saving receipt./nPlease try again later.");
		}
	}
	
	public void loadDataForPrint()
	{
		lstBillItem = null;
		List<OrderModel> lstOrders = new ArrayList<>();
		lstOrders = null;
		getLstOrder();
		lstOrders = orderItemEJB.getOrdersFromActiveTable(tableModel.getTableId());

		getLstBillItem();
		for (OrderModel ord : lstOrders) {
			BillReportModel bill = new BillReportModel();
			bill.setItemName(ord.getItemId().getName());
			bill.setItemPrice(ord.getItemId().getPrice());
			bill.setQuantity(ord.getQuantity());
			lstBillItem.add(bill);
		}
	}

	private void resetReceipt() {
		billTotal = 0;
		cashAmount = 0;
		bankAmount = 0;
		chequeNum = 0;
		creditAmount = 0;
		customerName = "";
		billModel = null;
	}

	private JournalVoucherModel getJVListForSave() {
		AccHeadMap cashAccHeadMap = accHeadMapEJB.getByMapPurpose("Cash"),
				debitorAccHeadMap = accHeadMapEJB.getByMapPurpose("Debitor"),
				salesAccHeadMap = accHeadMapEJB.getByMapPurpose("Sales"),
				serChrgAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge"),
				vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");

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
			jvd.setNarration("Cash on sales.");
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

		if (vat > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(vatAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(0);
			jvd.setCrAmt(vat);
			jvd.setNarration("Vat on sales");
			jvd.setJvm(jvm);
			jvdmList.add(jvd);

			billTotal -= vat;
		}

		if (serviceCharge > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(serChrgAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(0);
			jvd.setCrAmt(serviceCharge);
			jvd.setNarration("Service charge on sales");
			jvd.setJvm(jvm);
			jvdmList.add(jvd);

			billTotal -= serviceCharge;
		}

		if (billTotal > 0) {
			jvd = new JournalVoucherDetailModel();
			jvd.setAccountHead(salesAccHeadMap.getAccHeadModel());
			jvd.setDrAmt(0);
			jvd.setCrAmt(billTotal);
			jvd.setNarration("Receipt from sales.");
			jvd.setJvm(jvm);
			jvdmList.add(jvd);
		}

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
	public void saveBill()
	{
		Long billNo = (Long) DirectSqlUtils.getSingleValueFromTable("select IFNULL(MAX(bill_no), 0) as billNo from tbl_bill");
		billModel.setBillNo((billNo).intValue()+1);
		billModel.setCollectedBy(userMB.getUser());
		billModel.setDate(userMB.getUser().getDayInStatus().getDayInDateEn());
		billEJB.save(billModel);
	}
	
	public void loadReceiptDialog()
	{
		AccHeadMap vatAccHeadMap = accHeadMapEJB.getByMapPurpose("Vat");
		AccHeadMap servChargeAccHeadMap = accHeadMapEJB.getByMapPurpose("Service Charge");
		
		billModel = null;
		getBillModel();
		lstBillDetails = new ArrayList<>();
			
		for (OrderModel ord : lstOrder) {
			double amt = ord.getQuantity() * ord.getItemId().getPrice();
			BillDetailModel billDetailModel = new BillDetailModel();
			
			billDetailModel.setItemId(ord.getItemId());
			billDetailModel.setQuantity(ord.getQuantity());
			billDetailModel.setAmount(ord.getItemId().getPrice() * ord.getQuantity() );
			billModel.getLstBillDetails().add(billDetailModel);
			billAmout += amt;
		}
		billAmout = Double.valueOf(df.format(billAmout));
		
		getBillModel();
		billModel.setBillAmount(billAmout);
		vat = billAmout * vatEJB.getVatSettingByMapId(vatAccHeadMap.getAccHeadMapId()).getPercent() / 100;
		// vat = billAmout *13/ 100;
		vat = Double.valueOf(df.format(vat));
		serviceCharge = (billAmout + vat)
				* vatEJB.getVatSettingByMapId(servChargeAccHeadMap.getAccHeadMapId()).getPercent() / 100;
		// serviceCharge = (billAmout + vat)* 10/ 100;

		serviceCharge = Double.valueOf(df.format(serviceCharge));
		billTotal = billAmout + vat + serviceCharge;
		billTotal = Double.valueOf(df.format(billTotal));
		
		billModel.setBillAmount(billAmout);
		billModel.setVatAmount(vat);
		billModel.setDiscount(0.0);
		billModel.setServiceCharge(serviceCharge);
		billModel.setGrandTotal(billTotal);
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("receiptDialog.show();");
		context.update(":activeBill");
		
		
	}
	
	public void loadMergeDialog()
	{
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("mergeTableDialog.show();");
		context.update(":activeBill");
	}

	public BillModel getBillModel() {
		if (billModel == null)
			billModel = new BillModel();
		if (billModel.getLstBillDetails() == null)
			billModel.setLstBillDetails(new ArrayList<BillDetailModel>());
		return billModel;
	}

	public void setBillModel(BillModel billModel) {
		this.billModel = billModel;
	}

	public List<BillDetailModel> getLstBillDetails() {
		if (lstBillDetails == null)
			lstBillDetails = new ArrayList<>();
		return lstBillDetails;
	}

	public void setLstBillDetails(List<BillDetailModel> lstBillDetails) {
		this.lstBillDetails = lstBillDetails;
	}

	public double getDiscoutPerc() {
		return discoutPerc;
	}

	public void setDiscoutPerc(double discoutPerc) {
		this.discoutPerc = discoutPerc;
	}
	
	public void deleteOrder()
	{
		List<OrderModel> lstOrders = new ArrayList<>();
		lstOrders = orderItemEJB.getOrdersFromActiveTable(tableModel.getTableId());
		for (OrderModel ord : lstOrders) {
			ord.setStatus(2); // Bill cleared
			orderItemEJB.update(ord);
		}
	}
	
	public void printReceipt() throws IOException, JRException {

		getLstBillItem();
		if (lstBillItem.size() == 0) {
			displayErrorMessageToUser("Please Save the Receipt to print");
			return;
		}
		
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
				getLstBillItem());
		String reportPath;
		reportPath = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/reports/BillReceipt.jasper");

		jasperPrint = JasperFillManager.fillReport(reportPath,
				getReportParameters(), beanCollectionDataSource);
		/*
		 * jasperPrint.setPageHeight(596); jasperPrint.setPageWidth(420);
		 */
		httpServletResponse = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		httpServletResponse.addHeader("Content-disposition",
				"inline; filename=ReceiptNo.pdf");
		servletOutputStream = httpServletResponse.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint,
				servletOutputStream);
		FacesContext.getCurrentInstance().responseComplete();

		
	}
	public Map<String, Object> getReportParameters() {
		DecimalFormat df = new DecimalFormat("##.##");
		

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cusName", billModel.getBillTo());
		params.put("Date", userMB.getUser().getDayInStatus().getDayInDateNp());
		params.put("BillNo", billModel.getBillNo());
		params.put("BillAmountInWord",
				JrUtils.toWordsMiracle(String.valueOf(billModel.getBillAmount())));
		params.put("discount", billModel.getDiscount());
		params.put("serviceCharge", billModel.getServiceCharge());
		params.put("vat", billModel.getVatAmount());
		params.put("discountPer", discoutPerc);

		return params;
	}

	public List<BillReportModel> getLstBillItem() {
		if (lstBillItem == null)
			lstBillItem = new ArrayList<>();
		return lstBillItem;
	}

	public void setLstBillItem(List<BillReportModel> lstBillItem) {
		this.lstBillItem = lstBillItem;
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
	
	public void sendKOTBOT()
	{
		

        try {
        	getLstOrder();
        	
        	lstBillItem = null;
    		getLstOrder();
    		String tableNo ="";
    		String orderTime = "";
    		int tokenNo = 0;
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    		getLstBillItem();
    		for (OrderModel ord : lstOrder) {
    			BillReportModel bill = new BillReportModel();
    			bill.setItemName(ord.getItemId().getName());
    			bill.setItemPrice(ord.getItemId().getPrice());
    			bill.setQuantity(ord.getQuantity());
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
	

}
