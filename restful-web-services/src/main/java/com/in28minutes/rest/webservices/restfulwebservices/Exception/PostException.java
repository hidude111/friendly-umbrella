package com.in28minutes.rest.webservices.restfulwebservices.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LENGTH_REQUIRED)
public class PostException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PostException(String message) {
		super(message);
	}

}
