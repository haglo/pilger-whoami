package org.suite.model.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.suite.model.entity.enums.DefaultLanguage;
import org.suite.model.entity.enums.DefaultTheme;

@Entity
public class ElytronUser extends Superclass implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(unique = true)
	private String username;

	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "euser_erole", joinColumns = @JoinColumn(name = "euser_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "erole_id", referencedColumnName = "id"))
	private List<ElytronRole> elytronRoles;

	/**
	 * Enum Language
	 */
	@Enumerated(EnumType.STRING)
	private DefaultLanguage defaultLanguage;

	/**
	 * Enum Theme
	 */
	@Enumerated(EnumType.STRING)
	private DefaultTheme defaultTheme;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<ElytronRole> getElytronRoles() {
		return elytronRoles;
	}

	public void setElytronRoles(List<ElytronRole> elytronRoles) {
		this.elytronRoles = elytronRoles;
	}

	public DefaultLanguage getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(DefaultLanguage defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public DefaultTheme getDefaultTheme() {
		return defaultTheme;
	}

	public void setDefaultTheme(DefaultTheme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

}
