package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = { "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
		"classpath:flyway/migrations/V1.1__Jeep_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
class CreateOrderTest {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testCreateOrderReturnsSuccess201() {
		String body = createOrderBody();
		String uri = String.format("http://localhost:%d/orders", serverPort);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

		ResponseEntity<Order> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity, Order.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();

		Order order = response.getBody();
		assertThat(order.getCustomer().getCustomerId()).isEqualTo("MAYNARD_TORBJORG");
		assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.WRANGLER);
		assertThat(order.getModel().getTrimLevel()).isEqualTo("Sport");
		assertThat(order.getModel().getNumDoors()).isEqualTo(2);
		assertThat(order.getColor().getColorId()).isEqualTo("EXT_DIAMOND_BLACK");
		assertThat(order.getEngine().getEngineId()).isEqualTo("2_0_TURBO");
		assertThat(order.getTire().getTireId()).isEqualTo("35_TOYO");
		assertThat(order.getOptions()).hasSize(6);

	}

	String createOrderBody() {
		return "{\n"
				+ "   \"customer\":\"MAYNARD_TORBJORG\",\n"
				+ "   \"model\":\"WRANGLER\",\n"
				+ "   \"trim\":\"Sport\",\n"
				+ "   \"doors\":2,\n"
				+ "   \"color\":\"EXT_DIAMOND_BLACK\",\n"
				+ "   \"engine\":\"2_0_TURBO\",\n"
				+ "   \"tire\":\"35_TOYO\",\n"
				+ "   \"options\":[\n"
				+ "      \"DOOR_QUAD_4\",\n"
				+ "      \"EXT_AEV_LIFT\",\n"
				+ "      \"EXT_WARN_WINCH\",\n"
				+ "      \"EXT_WARN_BUMPER_FRONT\",\n"
				+ "      \"EXT_WARN_BUMPER_REAR\",\n"
				+ "      \"EXT_ARB_COMPRESSOR\"\n"
				+ "   ]\n"
				+ "}";
	}

}
