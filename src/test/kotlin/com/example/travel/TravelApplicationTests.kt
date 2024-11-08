package com.example.travel

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest(properties = ["spring.profiles.active=test"])
class TravelApplicationTests {

	@Test
	@WithMockUser(username = "testUser", roles = ["USER"])
	fun contextLoads() {
	}

}
