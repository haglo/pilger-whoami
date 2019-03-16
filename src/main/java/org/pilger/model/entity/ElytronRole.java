package org.suite.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.security.core.GrantedAuthority;


@Entity
public class ElytronRole extends Superclass implements Serializable, GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Column(unique = true)
	private String rolename;
	
	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	@Override
	public String getAuthority() {
		return rolename;
	}

}
