package sasrestro.model.restaurant;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="master_setting")
public class MasterSettingModel implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int Id;
	
	@Column(name="restaurant_name")
	private String restaurantName;
	
	@Column(name="address")
	private String address;
	
	@Column(name ="phone")
	private String phone;
	
	@Column(name ="email")
	private String email;
	
	@Column(name ="website")
	private String website;
	
	@Column(name ="VAT")
	private double vat;
	
	@Column(name = "service_charge")
	private double serviceCharge;
	
	@Column(name ="printer_pos")
	private String posPrinterName;
	
	@Column(name ="printer_kot")
	private String kotPrinterName;
	
	@Column(name ="printer_bot")
	private String botPrinterName;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public String getPosPrinterName() {
		return posPrinterName;
	}

	public void setPosPrinterName(String posPrinterName) {
		this.posPrinterName = posPrinterName;
	}

	public String getKotPrinterName() {
		return kotPrinterName;
	}

	public void setKotPrinterName(String kotPrinterName) {
		this.kotPrinterName = kotPrinterName;
	}

	public String getBotPrinterName() {
		return botPrinterName;
	}

	public void setBotPrinterName(String botPrinterName) {
		this.botPrinterName = botPrinterName;
	}

}
