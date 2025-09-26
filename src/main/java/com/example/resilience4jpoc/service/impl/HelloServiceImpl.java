package com.example.resilience4jpoc.service.impl;

import org.springframework.stereotype.Service;

import com.example.resilience4jpoc.service.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello() {
		return "Hello from service";
	}
}
