package sasrestro.model.restaurant;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import sasrestro.model.user.User;


	@Entity
	@Table(name="tbl_bill")
	public class BillModel implements Serializable {

		private static final long serialVersionUID = 1L; 
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "bill_id")
		private int billId;
		
		@Column(name ="bill_no")
		private int billNo;
		
		@Column(name ="bill_to")
		private String billTo;
		
		@Column(name ="address")
		private String address;
		
		@Column(name ="vat_no")
		private String custVAT;
		
		@Column(name ="bill_amount")
		private Double billAmount;
		
		@Column(name ="received_amount")
		private Double receivedAmount;
		
		
		@Column(name ="Vat_amount")
		private Double vatAmount;
		
		@Column(name ="service_charge")
		private Double serviceCharge;
		
		@Column(name ="grand_total")
		private Double grandTotal;
		
		@Column(name="discount")
		private Double discount;
		
		@Column(name="card_cheque_no")
		private Double cardNo;
		
		@Column(name = "date_time")
		@Temporal(TemporalType.TIMESTAMP)
		private Date date;
		
		@OneToOne
		@JoinColumn(name="collected_by")
		private User collectedBy;
		
		
		@Column(name="status")
		private boolean status;
		
		@Column(name="remarks")
		private String remarks;
		
		
		@OneToMany(mappedBy="billId", cascade=CascadeType.PERSIST, orphanRemoval=true)
		@JoinColumn
		private List<BillDetailModel> lstBillDetails;
		

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
		

		public int getBillId() {
			return billId;
		}

		public void setBillId(int billId) {
			this.billId = billId;
		}

		public int getBillNo() {
			return billNo;
		}

		public void setBillNo(int billNo) {
			this.billNo = billNo;
		}

		

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Double getBillAmount() {
			return billAmount;
		}

		public void setBillAmount(Double billAmount) {
			this.billAmount = billAmount;
		}

		public Double getReceivedAmount() {
			return receivedAmount;
		}

		public void setReceivedAmount(Double receivedAmount) {
			this.receivedAmount = receivedAmount;
		}

		public Double getVatAmount() {
			return vatAmount;
		}

		public void setVatAmount(Double vatAmount) {
			this.vatAmount = vatAmount;
		}

		public Double getServiceCharge() {
			return serviceCharge;
		}

		public void setServiceCharge(Double serviceCharge) {
			this.serviceCharge = serviceCharge;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public User getCollectedBy() {
			return collectedBy;
		}

		public void setCollectedBy(User collectedBy) {
			this.collectedBy = collectedBy;
		}

		public List<BillDetailModel> getLstBillDetails() {
			return lstBillDetails;
		}

		public void setLstBillDetails(List<BillDetailModel> lstBillDetails) {
			this.lstBillDetails = lstBillDetails;
		}

		public Double getGrandTotal() {
			return grandTotal;
		}

		public void setGrandTotal(Double grandTotal) {
			this.grandTotal = grandTotal;
		}

		public Double getDiscount() {
			return discount;
		}

		public void setDiscount(Double discount) {
			this.discount = discount;
		}

		public String getBillTo() {
			return billTo;
		}

		public void setBillTo(String billTo) {
			this.billTo = billTo;
		}

		public String getCustVAT() {
			return custVAT;
		}

		public void setCustVAT(String custVAT) {
			this.custVAT = custVAT;
		}

		public Double getCardNo() {
			return cardNo;
		}

		public void setCardNo(Double cardNo) {
			this.cardNo = cardNo;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		

}
