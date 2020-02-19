package com.in28minutes.rest.webservices.restfulwebservices.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.IM_USED)
public class UserCreatedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserCreatedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	

}
