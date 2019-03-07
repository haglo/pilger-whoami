package org.pilger.controler;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WhoamiControler {

	private static final String RESSOURCE_URL = "http://keycloak:8080/auth/realms/pilger/protocol/openid-connect/certs";
	private RestTemplate restTemplate;
	private RestTemplateBuilder restTemplateBuilder;
	private String publicKey;
	private Parser parser;

	@GetMapping("/validate/{token}")
	ResultDTO validate(@PathVariable(value = "token") String ttoken) {
		ResultDTO result = new ResultDTO();
		result.setValide(true);
		log.info("Whoami-Token: " + ttoken.toString());
		publicKey = retrievePublicKey();
		return result;
	}

	private String retrievePublicKey() {
		String pKey = "";
		parser = new Parser();
		parser.convertToList();

//		JacksonJsonParser parser = new JacksonJsonParser();
//		ObjectMapper objectMapper = new ObjectMapper();
		return pKey;
	}

}
