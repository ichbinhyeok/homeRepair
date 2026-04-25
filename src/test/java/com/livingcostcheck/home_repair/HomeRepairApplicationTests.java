package com.livingcostcheck.home_repair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class HomeRepairApplicationTests {

	private static final ObjectMapper JSON = new ObjectMapper();
	private static final Pattern JSON_LD_SCRIPT = Pattern.compile(
			"<script\\s+type=\"application/ld\\+json\"[^>]*>(.*?)</script>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testIndexPage() throws Exception {
		mockMvc.perform(get("/home-repair"))
				.andExpect(status().isOk())
				.andExpect(view().name("pages/index"));
	}

	@Test
	void testRootRedirect() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isMovedPermanently())
				.andExpect(redirectedUrl("/inspection-response-letter"));
	}

	@Test
	void acquisitionPagesRenderValidSoftwareApplicationJsonLd() throws Exception {
		assertSoftwareApplicationJsonLd("/inspection-report-negotiation-tool");
		assertSoftwareApplicationJsonLd("/what-to-ask-for-after-home-inspection");
	}

	private void assertSoftwareApplicationJsonLd(String path) throws Exception {
		String html = mockMvc.perform(get(path))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Matcher matcher = JSON_LD_SCRIPT.matcher(html);
		assertTrue(matcher.find(), "missing JSON-LD script on " + path);

		String schemaJson = matcher.group(1).trim();
		assertFalse(schemaJson.contains("\\-"), "invalid JSON hyphen escape on " + path);

		JsonNode schema = JSON.readTree(schemaJson);
		assertEquals("SoftwareApplication", schema.get("@type").asText());
		assertEquals("https://lifeverdict.com" + path, schema.get("url").asText());
	}

}
