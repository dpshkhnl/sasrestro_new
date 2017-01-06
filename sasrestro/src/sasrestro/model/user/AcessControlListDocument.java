package sasrestro.model.user;

import java.io.Serializable;



public class AcessControlListDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private AccessControlList aclist;
	private AccessControlListChild aclChild;
	private int isParent ;
	public AcessControlListDocument(Object obj) {
	
		if (obj instanceof AccessControlList) {
			this.aclist = (AccessControlList) obj;
			isParent = 0;
		}
		else
		{
			this.aclChild = (AccessControlListChild) obj;
			isParent = 1;
		}

	}

	public AccessControlList getAclist() {
		if (aclist == null)
			aclist = new AccessControlList();
		return aclist;
	}

	public void setAclist(AccessControlList aclist) {
		this.aclist = aclist;
	}

	public AccessControlListChild getAclChild() {
		if (aclChild == null)
			aclChild = new AccessControlListChild();
		return aclChild;
	}

	public void setAclChild(AccessControlListChild aclChild) {
		this.aclChild = aclChild;
	}

	public int getIsParent() {
		return isParent;
	}

	public void setIsParent(int isParent) {
		this.isParent = isParent;
	}

	
	}

