package sasrestro.sessionejb.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import sasrestro.model.user.AccessControlList;
import sasrestro.model.user.UserLoginInfo;
import sasrestro.sessionejb.core.GenericDAO;

@Stateless
@Local
public class LoginInfoEJB extends GenericDAO<UserLoginInfo> implements Serializable {

	private static final long serialVersionUID = 1L;

	public LoginInfoEJB() {
		super(UserLoginInfo.class);

	}

	public List<UserLoginInfo> getActiveSessionByUser(int userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllWithGivenCondition(
				UserLoginInfo.GET_ACTIVE_SESSION_BY_USER, parameters);
	}

	public List<UserLoginInfo> getAllActiveSession() {
		return findAllWithGivenCondition(UserLoginInfo.GET_ACTIVE_USER, null);
	}
	
	public List<UserLoginInfo> getAccPreviliageActiveSession(AccessControlList accessObj) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("accessModel", accessObj);
		return findAllWithGivenCondition(UserLoginInfo.GET_ACC_PREVILIAGE_ACTIVE_USER, parameters);
	}
	
}
