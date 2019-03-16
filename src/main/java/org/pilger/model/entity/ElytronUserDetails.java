package org.suite.model.entity;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.suite.model.repository.ElytronRoleRepository;
import org.suite.model.repository.ElytronUserRepository;

@Component
public class ElytronUserDetails implements UserDetailsService {
	@Autowired
	ElytronUserRepository elytronUserRepository;

	@Autowired
	ElytronRoleRepository elytronRoleRepository;

	public ElytronUserDetails(ElytronUserRepository elytronUserRepository,
			ElytronRoleRepository elytronRoleRepository) {
		super();
		this.elytronUserRepository = elytronUserRepository;
		this.elytronRoleRepository = elytronRoleRepository;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ElytronUser elytronUser = elytronUserRepository.findOneByUsername(username);
		if (null == elytronUser) {
			throw new UsernameNotFoundException("cannot find username: " + username);
		}
		List<ElytronRole> elytronRoles = elytronUser.getElytronRoles();
		ElytronUserPrincipal elytronUserPrincipal = new ElytronUserPrincipal(elytronUser, elytronRoles);

		return elytronUserPrincipal;
	}
}
