package sasrestro.model.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "acl_child")
@NamedQueries(
{@NamedQuery(name = "AccessControlListChild.findBYPARENT", query = "select acl from AccessControlListChild acl where acl.parentAcl.aclId = :aclId "),
	@NamedQuery(name = "AccessControlListChild.findBYViewName", query = "select acl from AccessControlListChild acl where acl.viewName = :viewName ")})
public class AccessControlListChild implements Serializable {

		private static final long serialVersionUID = 1L;

		public static final String FIND_BY_PARENT = "AccessControlListChild.findBYPARENT";
		public static final String FIND_BY_VIEWNAME = "AccessControlListChild.findBYViewName";
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name="acl_id")
		private int aclId;
		
		@OneToOne
		@JoinColumn (name = "role_parent")
		private AccessControlList parentAcl;
		
		@Column(unique = true)
		private String viewName;
		
		@Column(unique = true)
		private String displayName;
		
		@Transient
		private boolean read;
		
		@Transient
		private boolean write;
		
		@Transient
		private boolean delete;
		public int getAclId() {
			return aclId;
		}

		public void setAclId(int aclId) {
			this.aclId = aclId;
		}

		public AccessControlList getParentAcl() {
			return parentAcl;
		}

		public void setParentAcl(AccessControlList parentAcl) {
			this.parentAcl = parentAcl;
		}

		public String getViewName() {
			return viewName;
		}

		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public boolean isRead() {
			return read;
		}

		public void setRead(boolean read) {
			this.read = read;
		}

		public boolean isWrite() {
			return write;
		}

		public void setWrite(boolean write) {
			this.write = write;
		}

		public boolean isDelete() {
			return delete;
		}

		public void setDelete(boolean delete) {
			this.delete = delete;
		}

		
}
