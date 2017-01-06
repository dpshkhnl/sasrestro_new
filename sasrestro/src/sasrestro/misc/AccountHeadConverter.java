package sasrestro.misc;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import sasrestro.model.account.AccHeadMcg;
import sasrestro.sessionejb.account.AccHeadEJB;

@ManagedBean(name = "accHeadConverter")
@RequestScoped
public class AccountHeadConverter implements Converter, Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private AccHeadEJB accHeadEJB;

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				return accHeadEJB.find(Integer.valueOf(value));
			} catch (NumberFormatException e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid account head."));
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((AccHeadMcg) object).getAccHeadId());
		} else {
			return null;
		}
	}
}
