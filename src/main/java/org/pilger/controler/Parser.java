package org.pilger.controler;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Parser {
	private static final String example = "{\"keys\": [{\"kid\": \"gEP39t3BZMlxq2xqfxA1y3Vs-C9fz4PtE-DMERE9iuE\",\"kty\": \"RSA\", \"alg\": \"RS256\", \"use\": \"sig\",\"n\": \"xvqVo8LLbZXCvEfEl5Uw3A0U6Fe_Tx0QU85wXqd0INoLUQMh1YLxJ45dOl1vB_I9P9Z7KjC3rCW3xOmyEVfw-k-ZhaMgcwcwcnhcZw1dJpQ-HYt-aj4OwST3RfZbvYTWzc8-0we4ptNf-_unvxUgaiqaj3FNK02aEA0oEbrjGrW0qBD-hUppmcZ0J80DAXcUOux1FlzTX4NQNXAsUYU0c-qI8UuDZbqXbirEMp35EGkvJJdbI0qVGRXI_R4c0Kn5kiUkqxGUm92X0QxLe9-as28UsyjgGuseNdhjiAURPv3HxMGW8xmxF0-phjFN4DVjM6PbhFna42hsN0kp5R3_3Q\", \"e\": \"AQAB\"}]}";

	public List<String> convertToList() {
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> elements = null;
		try {
			elements = objectMapper.readValue(example, List.class);
			System.out.println("elements = " + elements);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return elements;
	}
}
