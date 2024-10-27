package com.example.travel

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["spring.profiles.active=test"])
class TravelApplicationTests {

	@Test
	fun contextLoads() {
	}

}
