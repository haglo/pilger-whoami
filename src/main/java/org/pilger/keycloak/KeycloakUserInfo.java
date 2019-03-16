package org.pilger.keycloak;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "sub", "email_verified", "preferred_username", "email" })
public class KeycloakUserInfo {

	@JsonProperty("sub")
	private String sub;
	@JsonProperty("email_verified")
	private Boolean emailVerified;
	@JsonProperty("preferred_username")
	private String preferredUsername;
	@JsonProperty("email")
	private String email;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("sub")
	public String getSub() {
		return sub;
	}

	@JsonProperty("sub")
	public void setSub(String sub) {
		this.sub = sub;
	}

	@JsonProperty("email_verified")
	public Boolean getEmailVerified() {
		return emailVerified;
	}

	@JsonProperty("email_verified")
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@JsonProperty("preferred_username")
	public String getPreferredUsername() {
		return preferredUsername;
	}

	@JsonProperty("preferred_username")
	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
