package sasrestro.mb.user;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import sasrestro.misc.AbstractMB;
import sasrestro.misc.JCalendarFunctions;
import sasrestro.model.user.User;
import sasrestro.model.user.UserLoginInfo;
import sasrestro.model.util.DayInOutStatusModel;
import sasrestro.sessionejb.user.LoginInfoEJB;
import sasrestro.sessionejb.user.RoleEJB;
import sasrestro.sessionejb.user.UserEJB;
import sasrestro.sessionejb.util.DayInOutEJB;
import sasrestro.sessionejb.util.FiscalYrEJB;

/**
 * @author nebula
 *
 */
@ViewScoped
@ManagedBean
public class LoginMB extends AbstractMB implements Serializable {


	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = UserMB.INJECTION_NAME)
	private UserMB userMB;
	@EJB
	UserEJB userEJB;
	@EJB
	private FiscalYrEJB fiscalYrEJB;

	@EJB
	private DayInOutEJB dayInOutEJB;

	@EJB
	private LoginInfoEJB loginInfoEJB;

	@EJB
	private RoleEJB roleEJB;

	private String email;
	private String password;
	private User currentUser;
	private boolean chkUser;
	private String transactionDate;
	private String transactionDateNp;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String login() throws NoSuchAlgorithmException, InvalidKeySpecException{ 
		if(dateCheck()){
		List<User> userList=userEJB.listAll();
		if(userList==null)
		{
			//displayErrorMessageToUser("Database is unreachable. Please, Contact your system admin for assistance.");
			return "/dberror.xhtml";
		}
		else
		{
			if(userList.size()<=0)
			{
				displayInfoMessageToUser("There are no users in the system. Please, Contact your system admin for assistance.");
				return null;
			}
			else
			{
				User user = userEJB.isValidLogin(email, password);
				if(user != null&&user.getStatus().ordinal()==1){
					DayInOutStatusModel dayInStatus = new DayInOutStatusModel();
					dayInStatus = dayInOutEJB.getActiveWorkDay();

					FacesContext context = FacesContext.getCurrentInstance();
					HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
					request.getSession().setAttribute("user", user);


					if (dayInOutEJB.getActiveWorkDay() == null)
					{
						userMB.setUser(user);
						RequestContext rc = RequestContext.getCurrentInstance();
						rc.execute("dayInDialog.show()");
						return null;
					}
					else{
						ServletContext servletContext = request.getSession().getServletContext();
						User att =(User) servletContext.getAttribute("user-"+user.getId());
						if ( att == null || att.getId() == 0 )
						{
							String hostname = getUserIpAddr(request);
							System.out.println(hostname);
							servletContext.setAttribute("user-"+user.getId(), user);
							servletContext.setAttribute("userIP-"+user.getId(), hostname);
							User attribute =(User) servletContext.getAttribute("user-"+user.getId());
							System.out.println(attribute.getName());
						}
						else
						{
							if (!chkUser)
							{
								chkUser = true;			
								displayInfoMessageToUser("User is already Logged In in:" + servletContext.getAttribute("userIP-"+user.getId())+" . Please contact System Admin for further assistance");
								return null;
							}
						}
						//request.getSession().setAttribute("user", arg1);
						if (loginInfoEJB.getActiveSessionByUser(user.getId()).size() != 0 )
						{
							List<UserLoginInfo>lstLogin = loginInfoEJB.getActiveSessionByUser(user.getId());
							for(UserLoginInfo loginInfo : lstLogin ){

								loginInfo.setLogoutTime(new Date());
								loginInfo.setUpdatedBy(user.getId());
								loginInfo.setUpdatedDateAD(new Date());
								loginInfo.setRemarks("Log out during next login");
								loginInfoEJB.update(loginInfo);
							}



						}
						UserLoginInfo loginInfo = new UserLoginInfo();
						loginInfo.setLoginTime(new Date());
						loginInfo.setCreatedBy(user.getId());
						loginInfo.setUserId(user);
						loginInfo.setCreatedDateAD(new Date());
						loginInfo.setRemarks("login");
						loginInfoEJB.save (loginInfo);
						user.setDayInStatus(dayInStatus);
						userMB.setUser(user);

						String page = "/pages/protected/index.xhtml?faces-redirect=true";
						return page;

					}
				}
				displayErrorMessageToUser("Check your email/password. May be your account is inactive!");

			}
		}
	}
		return null;
	}

	public void forceLogin() throws NoSuchAlgorithmException, InvalidKeySpecException
	{

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		ServletContext servletContext = request.getSession().getServletContext();
		servletContext.removeAttribute("user-"+userMB.getUser().getId());
		servletContext.removeAttribute("userIP-"+userMB.getUser().getId());


		List<UserLoginInfo>lstLogin = loginInfoEJB.getActiveSessionByUser(userMB.getUser().getId());
		if (lstLogin.size() != 0){
			for(UserLoginInfo loginInfo : lstLogin ){

				loginInfo.setLogoutTime(new Date());
				loginInfo.setUpdatedBy(userMB.getUser().getId());
				loginInfo.setUpdatedDateAD(new Date());
				loginInfo.setRemarks("Force logout");
				loginInfoEJB.update(loginInfo);
			}
		}

		displayInfoMessageToUser("User "+userMB.getUser().getEmail()+" has been logged out successfully");
		//login();

	}
	public String getUserIpAddr(HttpServletRequest request) {      
		String ip = request.getHeader("x-forwarded-for");      
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
			ip = request.getHeader("Proxy-Client-IP");      
		}      
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
			ip = request.getHeader("WL-Proxy-Client-IP");      
		}      
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
			ip = request.getRemoteAddr();      
		}      
		return ip;      
	}

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}   
	public void logout()
	{
		long ms;
		FacesContext context = FacesContext.getCurrentInstance();

		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		if (session != null){
			ms = session.getLastAccessedTime();
			long millis = System.currentTimeMillis();
			if (millis- ms <600000){
				System.out.println("Active in anothertab");
			}
			else
			{
				User user = new User();
				user = userMB.getUser();

				List<UserLoginInfo>lstLogin = loginInfoEJB.getActiveSessionByUser(user.getId());
				for(UserLoginInfo loginInfo : lstLogin ){

					loginInfo.setLogoutTime(new Date());
					loginInfo.setUpdatedBy(user.getId());
					loginInfo.setUpdatedDateAD(new Date());
					loginInfo.setRemarks("Session Expire logout");
					loginInfoEJB.update(loginInfo);
				}
				HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
				ServletContext servletContext = request.getSession().getServletContext();

				servletContext.removeAttribute("user-"+userMB.getUser().getId());
				servletContext.removeAttribute("userIP-"+userMB.getUser().getId());
				request.getSession().invalidate();
			}
		}
		else
			System.out.println("There's no session created for the current user");



		//return "pages/public/login.xhtml?faces-redirect=true";

	}


	public void forceLogOut()
	{
		User user = new User();
		user = userMB.getUser();
		if (currentUser.getId() == user.getId())
		{
			displayErrorMessageToUser("This user cannot be force Log Out");
			return;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		ServletContext servletContext = request.getSession().getServletContext();

		servletContext.removeAttribute("user-"+currentUser.getId());
		servletContext.removeAttribute("userIP-"+currentUser.getId());

		List<UserLoginInfo>lstLogin = loginInfoEJB.getActiveSessionByUser(currentUser.getId());
		for(UserLoginInfo loginInfo : lstLogin ){

			loginInfo.setLogoutTime(new Date());
			loginInfo.setUpdatedBy(user.getId());
			loginInfo.setUpdatedDateAD(new Date());
			loginInfo.setRemarks("Force logout");
			loginInfoEJB.update(loginInfo);
		}
		displayInfoMessageToUser("User "+currentUser.getEmail()+" has been logged out successfully");
	}

	public User getCurrentUser() {
		if (currentUser == null)
			currentUser = new User();
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public boolean isChkUser() {
		return chkUser;
	}

	public void setChkUser(boolean chkUser) {
		this.chkUser = chkUser;
	}

	public void reset()
	{
		chkUser = false;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	@PostConstruct
	private void init(){
		JCalendarFunctions jcal = new JCalendarFunctions();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		if(dayInOutEJB.getActiveWorkDay() == null){
			this.transactionDate = sdf.format(dayInOutEJB.getPreviousDay().getDayInDateEn()) + " A.D.";
			this.transactionDateNp = jcal.getNepaliDate(dayInOutEJB.getPreviousDay().getDayInDateEn()) + " B.S.";
		}else{
			this.transactionDate = sdf.format(dayInOutEJB.getActiveWorkDay().getDayInDateEn()) + " A.D. ";
			this.transactionDateNp = jcal.getNepaliDate(dayInOutEJB.getActiveWorkDay().getDayInDateEn()) + " B.S.";
		}
	}

	public String getTransactionDateNp() {
		return transactionDateNp;
	}

	public void setTransactionDateNp(String transactionDateNp) {
		this.transactionDateNp = transactionDateNp;
	}

	private Boolean dateCheck(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			if(date.before(sdf.parse("01-02-2017"))){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		displayInfoMessageToUser("Please renew license for ERP Software. Sorry for Inconvinence.");
		return false;
	}
}
