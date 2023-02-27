package com.programmingPractise.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmingPractise.productservice.dto.ProductRequest;
import com.programmingPractise.productservice.dto.ProductResponse;
import com.programmingPractise.productservice.model.Product;
import com.programmingPractise.productservice.repository.ProductRepository;
import com.programmingPractise.productservice.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	ProductRepository productRepository;


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		Assertions.assertEquals(1,productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}


	@Test
	void shouldGetAllProducts() throws Exception {
		// create some products
		List<Product> productList = Arrays.asList(
				Product.builder().name("iPhone 13").description("iPhone 13").price(BigDecimal.valueOf(1200)).build(),
				Product.builder().name("Samsung Galaxy S21").description("Samsung Galaxy S21").price(BigDecimal.valueOf(1000)).build(),
				Product.builder().name("Google Pixel 6").description("Google Pixel 6").price(BigDecimal.valueOf(800)).build()
		);
		productRepository.saveAll(productList);

		
		// retrieve all products using GET request
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("iPhone 13"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Samsung Galaxy S21"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("Google Pixel 6"));
	}




}


