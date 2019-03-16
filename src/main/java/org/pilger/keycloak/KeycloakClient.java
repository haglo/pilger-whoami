package org.pilger.keycloak;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Map;

import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeycloakClient {

	private final static String SERVER_URL_AUTH = "http://keycloak:8080/auth";
	private final static String REALM_ID = "Pilger";
	private final static String CLIENT_ID = "PilgerApp";
	private final static String CLIENT_SECRET = "9dabb288-2db0-4f0e-991f-3a5b41872ef4";

	private final static String SERVER_URL_USER_INFO = SERVER_URL_AUTH +"/realms/" + REALM_ID + "/protocol/openid-connect/userinfo";

	private Keycloak keycloak;
	private JWSHeader jwsHeader;

	public KeycloakClient() {
		setKeycloak(newKeycloakBuilderWithClientCredentials().build());
	}

	public KeycloakClient(String username, String password) {
		setKeycloak(newKeycloakBuilderWithPasswordCredentials(username, password).build());
	}

	private KeycloakBuilder newKeycloakBuilderWithClientCredentials() {
		return KeycloakBuilder.builder() //
				.realm(REALM_ID) //
				.serverUrl(SERVER_URL_AUTH)//
				.clientId(CLIENT_ID) //
				.clientSecret(CLIENT_SECRET) //
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS);
	}

	private KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
		return newKeycloakBuilderWithClientCredentials() //
				.username(username) //
				.password(password) //
				.grantType(OAuth2Constants.PASSWORD);
	}

	public boolean validateTokenString(String tokenString) {
		boolean isValid = false;
		JWSHeader header = null;

		/**
		 * Get JWSHeader
		 */
		DecodedJWT jwt = JWT.decode(tokenString);
		System.out.println("Whoami-original-Header = " + jwt.getHeader());
		System.out.println("Whoami-original-Payload = " + jwt.getPayload());
		System.out.println("Whoami-original-Signature = " + jwt.getSignature());

		byte[] jwtHeaderBytes = Base64.getDecoder().decode(jwt.getHeader());
		String jwtHeaderString = new String(jwtHeaderBytes);
		System.out.println("Whoami-JSON-Header = " + jwtHeaderString);

		byte[] jwtPayloadBytes = Base64.getDecoder().decode(jwt.getPayload());
		String jwtPayloadString = new String(jwtPayloadBytes);
		System.out.println("Whoami-JSON-Payload = " + jwtPayloadString);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			header = objectMapper.readValue(jwtHeaderString, JWSHeader.class);
			setJwsHeader(header);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		/**
		 * Read Values of token
		 */
		AccessToken token = null;
		try {
			token = TokenVerifier.create(tokenString, AccessToken.class).getToken();
			System.out.println("Whoami-Subject--: " + token.getSubject());
			System.out.println("Whoami-Username-: " + token.getPreferredUsername());
			System.out.println("Whoami-Email----: " + token.getEmail());

		} catch (VerificationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/**
		 * Get Public Key
		 */
		PublicKey pKey = retrievePublicKeyFromKeycloak(header);

		/**
		 * Verify Token with PublicKey
		 */
		try {
			Jwts.parser().setSigningKey(pKey).parseClaimsJws(tokenString);
			isValid = true;
		} catch (ExpiredJwtException e) {
			isValid = false;
			log.error("\r\n--JWT-Token is expired\r\n--we *cannot* use the JWT-Token as intended by its creator");
			e.printStackTrace();
		} catch (UnsupportedJwtException e) {
			isValid = false;
			log.error("we *cannot* use the JWT as intended by its creator");
			e.printStackTrace();
		} catch (MalformedJwtException e) {
			isValid = false;
			log.error("we *cannot* use the JWT as intended by its creator");
			e.printStackTrace();
		} catch (SignatureException e) {
			isValid = false;
			log.error("Token has wrong signature");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			isValid = false;
			log.error("we *cannot* use the JWT as intended by its creator");
			e.printStackTrace();
		}

		return isValid;
	}

	private PublicKey retrievePublicKeyFromKeycloak(JWSHeader jwsHeader) {
		try {
			ObjectMapper om = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> certInfos = om.readValue(new URL(getRealmCertsUrl()).openStream(), Map.class);

			List<Map<String, Object>> keys = (List<Map<String, Object>>) certInfos.get("keys");

			log.info("Whoami-JSON Public Key from Keycloak" + Arrays.toString(certInfos.entrySet().toArray()));

			Map<String, Object> keyInfo = null;
			for (Map<String, Object> key : keys) {
				String kid = (String) key.get("kid");

				if (jwsHeader.getKeyId().equals(kid)) {
					keyInfo = key;
					break;
				}
			}

			if (keyInfo == null) {
				return null;
			}

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			String modulusBase64 = (String) keyInfo.get("n");
			String exponentBase64 = (String) keyInfo.get("e");

			// see org.keycloak.jose.jwk.JWKBuilder#rs256
			Decoder urlDecoder = Base64.getUrlDecoder();
			BigInteger modulus = new BigInteger(1, urlDecoder.decode(modulusBase64));
			BigInteger publicExponent = new BigInteger(1, urlDecoder.decode(exponentBase64));

			return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public KeycloakUserInfo retrieveUserInfoFromKeycloak(String token) {
		RestTemplate restTemplate = new RestTemplate();
		
        //set up the basic authentication header 
        String authorizationHeader = "Bearer " + token;
  
        //set up the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
        
        //Create a new HttpEntity
        HttpEntity<String> entity = new HttpEntity<String>(headers);
	
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(SERVER_URL_USER_INFO);
		KeycloakUserInfo keycloakUserInfo = restTemplate.postForObject(uriBuilder.toUriString(), entity, KeycloakUserInfo.class);
		
		System.out.println("Whoami-Subject--: " + keycloakUserInfo.getSub());
		System.out.println("Whoami-Username-: " + keycloakUserInfo.getPreferredUsername());
		System.out.println("Whoami-Email----: " + keycloakUserInfo.getEmail());

		return keycloakUserInfo;

	}

	public String getRealmUrl() {
		return SERVER_URL_AUTH + "/realms/" + REALM_ID;
	}

	public String getRealmCertsUrl() {
		return getRealmUrl() + "/protocol/openid-connect/certs";
	}

	public PublicKey toPublicKey(String publicKeyString) {
		try {
			byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return null;
		}
	}

	public Keycloak getKeycloak() {
		return keycloak;
	}

	public void setKeycloak(Keycloak keycloak) {
		this.keycloak = keycloak;
	}

	public JWSHeader getJwsHeader() {
		return jwsHeader;
	}

	public void setJwsHeader(JWSHeader jwsHeader) {
		this.jwsHeader = jwsHeader;
	}
}
