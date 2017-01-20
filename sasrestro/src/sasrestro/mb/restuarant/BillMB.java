package sasrestro.mb.restuarant;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import sasrestro.model.restaurant.BillModel;
import sasrestro.sessionejb.restaurant.BillEJB;

@ViewScoped
@ManagedBean(name="billMB")
public class BillMB {

	@EJB
	BillEJB billEJB;
	
	private BillModel billModel;
	private List<BillModel> lstBillModel;
	double totDiscount,totBillAmount,totVat,totTSC,grandTotal;
	public BillModel getBillModel() {
		if (billModel == null)
			billModel = new BillModel();
		
		return billModel;
	}
	public void setBillModel(BillModel billModel) {
		this.billModel = billModel;
	}
	public List<BillModel> getLstBillModel() {
		if(lstBillModel == null)
			lstBillModel = new ArrayList<>();
		return lstBillModel;
	}
	public void setLstBillModel(List<BillModel> lstBillModel) {
		this.lstBillModel = lstBillModel;
	}
	
	public void searchBill()
	{
		getLstBillModel();
		lstBillModel = billEJB.getByBillModel(billModel);
		totDiscount = 0;
		totBillAmount = 0;
		totTSC = 0;
		totVat = 0;
		grandTotal = 0;
		for (BillModel bill : lstBillModel)
		{
			if(!bill.isStatus()){
			totDiscount = totDiscount+ bill.getDiscount();
			totBillAmount = totBillAmount+bill.getBillAmount();
			totTSC = totTSC + bill.getServiceCharge();
			totVat = totVat + bill.getVatAmount();
			grandTotal = grandTotal+ bill.getGrandTotal();
			}
		}
		
	}
	public double getTotDiscount() {
		return totDiscount;
	}
	public void setTotDiscount(double totDiscount) {
		this.totDiscount = totDiscount;
	}
	public double getTotBillAmount() {
		return totBillAmount;
	}
	public void setTotBillAmount(double totBillAmount) {
		this.totBillAmount = totBillAmount;
	}
	public double getTotVat() {
		return totVat;
	}
	public void setTotVat(double totVat) {
		this.totVat = totVat;
	}
	public double getTotTSC() {
		return totTSC;
	}
	public void setTotTSC(double totTSC) {
		this.totTSC = totTSC;
	}
	public double getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}
	
}
