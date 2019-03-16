package org.suite.model.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ElytronUserPrincipal implements UserDetails{
	
 	private static final long serialVersionUID = 1L;
	private ElytronUser elytyronUser;
    private List<ElytronRole> elytronRoles;
    
    public ElytronUserPrincipal(ElytronUser elytyronUser, List<ElytronRole> elytronRoles){
        super();
        this.elytyronUser = elytyronUser;
        this.elytronRoles = elytronRoles;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	       if(null==elytronRoles){
	            return Collections.emptySet();
	        }
	        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
	        elytronRoles.forEach(group->{
	           grantedAuthorities.add(new SimpleGrantedAuthority(group.getRolename()));
	        });
	        return grantedAuthorities;
	}

	@Override
	public String getPassword() {
        return this. elytyronUser.getPassword();
	}

	@Override
	public String getUsername() {
        return this. elytyronUser.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
