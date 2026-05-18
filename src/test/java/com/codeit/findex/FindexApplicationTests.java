package com.codeit.findex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest(properties = {
		"findex.batch.cron=0 0 2 * * ?",
		"openapi.endpoint=http://localhost:8080/mock-api"
})
class FindexApplicationTests {

	@Test
	void contextLoads() {
	}

}
