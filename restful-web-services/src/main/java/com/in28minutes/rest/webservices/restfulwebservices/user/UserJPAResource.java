package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in28minutes.rest.webservices.restfulwebservices.Exception.NoEmailException;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.NoNameException;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.PostException;
import com.in28minutes.rest.webservices.restfulwebservices.Exception.UserNotFoundException;

@RestController
public class UserJPAResource {
	
	//method of dependency injection - this object is used to supply the dependencies of another object	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	//check validity of user input
    Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"("
    		+ "?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-"
    		+ "z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]"
    		+ "?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]"
    		+ ")");
   
	
	//get /users
	//retrieveAllUsers
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers() { return userRepository.findAll();}
	
	
	//get /users/{id}
	@GetMapping("/jpa/users/{id}")
	public Resource<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		
		if(!user.isPresent()) 
			throw new UserNotFoundException("id-" + id);
		
		//"all-users", SERVER_PATH + "/users/
		
		//retrieveAllUsers hateoas model
		Resource<User> model = new Resource<>(user.get());
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		model.add(linkTo.withRel("all-users"));
				
		return model;
	}
	
	//get /users/{id}
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);

	}
	
	
	//HATEOAS hypermedia as the engine of application state
	
	
	
	//return status called CREATED
	//input - details of user
	//output - CREATED and return the created URI
	
	//POST method to use with Postman
	@PostMapping("/jpa/users/")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user, String name, String email) {
		java.util.regex.Matcher mat = pattern.matcher(user.getEmail());
		if(user.getName() == "" || user.getName() == null) {
			throw new NoNameException("name-" + name);
		}
		
		if(!mat.matches()) {
			throw new NoEmailException("email-" + "email is not valid, enter a valid email.");
		}
		User savedUser = userRepository.save(user);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(savedUser.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrieveAllUsers(@PathVariable int id) { 
		Optional<User> userOptional = userRepository.findById(id);
		if(!userOptional.isPresent()) {
			throw new UserNotFoundException("id-" + id);
		}
		
		return userOptional.get().getPosts();
		
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPost(@Valid @PathVariable int id, @RequestBody Post post){
		
		Optional<User> userOptional = userRepository.findById(id);
		
		if(!userOptional.isPresent()) {
			throw new UserNotFoundException("id-" + id);
		}
		
		if(post.getDescription() == "" || post.getDescription().length() == 1) {
			throw new PostException("Description is too short or does not exist");
			
		}
		
		User user = userOptional.get();
		
		post.setUser(user);
		
		postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(post.getId()).toUri();
		
		return ResponseEntity.created(location).build();
		
		
	}
/*	
	@PutMapping("/jpa/users/{id}")
	public ResponseEntity<Object> updateUserName(@Valid @PathVariable Integer id, User partialUpdate, @RequestParam String name){
		
		Optional<User> userOptional = userRepository.findById(id);
		partialUpdate = userOptional.get();
		partialUpdate.setName(name);
		userRepository.save(partialUpdate);
		
		return ResponseEntity.ok("User name has been updated.");
		
	}
*/
	
	@RequestMapping(value = "/jpa/users/{id}", method = RequestMethod.PUT)
	public User updateUser(@PathVariable("id") Integer id, @Valid User updatedUser, 
			@RequestParam String name, @RequestParam String email) {
		Optional<User> user = userRepository.findById(id);
		updatedUser = user.get();
		updatedUser.setName(name);
		updatedUser.setEmail(email);
		userRepository.save(updatedUser);
		
		return updatedUser;
		
		
		
	}
	
	

}
