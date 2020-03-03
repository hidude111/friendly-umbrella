package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.NoEmailException;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.NoNameException;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.UserNotFoundException;
import com.in28minutes.rest.webservices.restfulwebservices.Filtering.SomeBean;


@RestController
public class UserController {
	
	//method of dependency injection - this object is used to supply the dependencies of another object
	@Autowired
	private UserDaoService service;
	
	//check validity of user input
    Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"("
    		+ "?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-"
    		+ "z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]"
    		+ "?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]"
    		+ ")");
   
	
	//get /users
	//retrieveAllUsers
	@GetMapping("/users")
	public List<User> retrieveAllUsers() { return service.findAll(); }
	
	
	//get /users/{id}
	@GetMapping("/users/{id}")
	public Resource<User> retrieveUser(@PathVariable int id) {
		User user = service.findOne(id);
		
		if(user == null) 
			throw new UserNotFoundException("id-" + id);
		
		//"all-users", SERVER_PATH + "/users/
		
		//retrieveAllUsers hateoas model
		Resource<User> model = new Resource<>(user);
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		model.add(linkTo.withRel("all-users"));
				
		return model;
	}
	
	//get /users/{id}
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id) {
		User user = service.deleteById(id);
		
		if(user == null) {
			throw new UserNotFoundException("id-" + id);
		}
	}
	
	

	
	//HATEOAS hypermedia as the engine of application state
	
	
	
	//return status called CREATED
	//input - details of user
	//output - CREATED and return the created URI
	
	//POST method to use with Postman
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user, String name, String email) {
		java.util.regex.Matcher mat = pattern.matcher(user.getEmail());
		if(user.getName() == "" || user.getName() == null) {
			throw new NoNameException("name-" + name);
		}
		
		if(!mat.matches()) {
			throw new NoEmailException("email-" + "email is not valid, enter a valid email.");
		}
		User savedUser = service.save(user);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(savedUser.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> patchUserName(@Valid @PathVariable int id, @RequestBody User partialUpdate, @RequestParam String name){
		User user = service.findOne(id);
		user.setName(name);
		service.save(user);	
		return ResponseEntity.ok("User name has been updated.");
		

	}
	

}
