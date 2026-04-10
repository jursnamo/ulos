package com.enterprise.ulos;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String home() {
		return "Halo, Spring Boot Ulos berjalan!";
	}
}
