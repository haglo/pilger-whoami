package org.pilger.controler;

import org.pilger.keycloak.KeycloakClient;
import org.pilger.keycloak.KeycloakUserInfo;
import org.pilger.model.entity.ResultDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WhoamiControler {

	@GetMapping(path = "/validate/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultDTO validate(@PathVariable(value = "token") String token) {
		ResultDTO result = new ResultDTO();
		KeycloakClient keycloakClient = new KeycloakClient();
		boolean isOK = false;

		log.info("Whoami-Token: " + token);

		isOK = keycloakClient.validateTokenString(token);

		try {
			KeycloakUserInfo keylocakUserInfo = keycloakClient.retrieveUserInfoFromKeycloak(token);
			if (!((keylocakUserInfo.getSub().isEmpty()) || (keylocakUserInfo.getSub()==null))) {
				isOK = true;
			} else {
				isOK = false;
			}
		} catch (Exception e) {
			isOK = false;
			e.printStackTrace();
		}
		
		result.setValide(isOK);

		return result;
	}

}
