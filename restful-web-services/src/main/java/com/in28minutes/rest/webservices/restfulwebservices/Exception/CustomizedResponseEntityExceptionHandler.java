package com.in28minutes.rest.webservices.restfulwebservices.Exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


//applicable across all controllers

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler 
extends ResponseEntityExceptionHandler {
	 
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request){
		
		ExceptionResponse exceptionresponse = new ExceptionResponse(new Date(),
				ex.getMessage(), request.getDescription(false));
		
		return new ResponseEntity(exceptionresponse, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
		
		ExceptionResponse exceptionresponse = new ExceptionResponse(new Date(),
				ex.getMessage(), request.getDescription(false));
		
		return new ResponseEntity(exceptionresponse, HttpStatus.NOT_FOUND);
		
	}
	
	
	//check for email

	//if the object does not have a name or email
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

			ExceptionResponse exceptionresponse = new ExceptionResponse(new Date(),
				"Validation Failed: Check name or email", ex.getBindingResult().getObjectName().toString());
			return new ResponseEntity(exceptionresponse, HttpStatus.BAD_REQUEST);
	}

	


}
