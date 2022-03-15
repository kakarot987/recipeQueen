package com.QueensKitchenSpringBoot.controllers.Authentication;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.ERole;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.Role;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.User;

import com.QueensKitchenSpringBoot.controllers.Authentication.payload.JwtResponse;
import com.QueensKitchenSpringBoot.controllers.Authentication.payload.LoginRequest;
import com.QueensKitchenSpringBoot.controllers.Authentication.payload.MessageResponse;
import com.QueensKitchenSpringBoot.controllers.Authentication.payload.SignupRequest;
import com.QueensKitchenSpringBoot.controllers.Authentication.repository.RoleRepository;
import com.QueensKitchenSpringBoot.controllers.Authentication.repository.UserRepository;
import com.QueensKitchenSpringBoot.controllers.Authentication.security.JwtUtils;
import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsServiceImpl;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity.ProfilePic;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Message.ResponseFile;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Message.ResponseMessage;
import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Service.FileStorageService;
import com.QueensKitchenSpringBoot.controllers.recipeData.RecipeService;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.RequestRecipeBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;


	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		List<JwtResponse> users = userDetailsService.getUser().map(user ->{
			return new JwtResponse(jwt,
					user.getId(),
					user.getUsername(),
					user.getEmail(),
					roles);
		}).collect(Collectors.toList());
		System.out.print(roles);
		Iterator<String> iterator  = roles.iterator();
		while(iterator.hasNext()) {
			String element = iterator.next();
			if(element == "ROLE_ADMIN") {
				System.out.print("Admin");
				return ResponseEntity.status(HttpStatus.OK).body(users);
			}
			else if(element == "ROLE_USER")
			{
				return ResponseEntity.ok(new JwtResponse(jwt,
						userDetails.getId(),
						userDetails.getUsername(),
						userDetails.getEmail(),
						roles));
			}
		}
		return null;
	}


	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		System.out.println("Values here are ----"+signUpRequest.getEmail() + "" +
				signUpRequest.getPassword() + "" +
				signUpRequest.getRole() + "" +
				signUpRequest.getUsername());
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword())
							 );

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	// post method for food
	@PostMapping(value = "/uploadPic", consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE
	})
	public ResponseEntity<ResponseMessage> uploadFood(@RequestPart("file") MultipartFile file,
													  @RequestParam("user_id") Long user_id){
		String message = "";
		try {
			System.out.println("User Data here " + user_id);
			fileStorageService.store(file, user_id);
			message = "Uploaded the details seccessfully: ";
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		}
		catch(Exception e) {
			message = "Could not upload the file";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}

	// get method for food
	@GetMapping("/getProfilePic")
	public ResponseEntity<List<ResponseFile>> getfood(){
		List<ResponseFile> food = fileStorageService.getAllFiles().map(foodEntity ->{
			String fileDownloadUri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/getFood/")
					.path(foodEntity.getProfile_id())
					.toUriString();

			return new ResponseFile(
					foodEntity.getName(),
					fileDownloadUri,
					foodEntity.getType(),
					foodEntity.getData().length,
					foodEntity.getProfile_id(),
					foodEntity.getUser_id()
			);
		}).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(food);
	}
	//Get food image data by id
	@GetMapping("/getProfilePic/{id}")
	public ResponseEntity<byte[]> getFood(@PathVariable String id) {
		ProfilePic foodEntity = fileStorageService.getFile(id);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + foodEntity.getName() + "\"")
				.body(foodEntity.getData());
	}

	@PostMapping("/getUserProfilePic/{user_id}")
	public ResponseEntity<List<ProfilePic>> getProfilePic(@PathVariable Long user_id){
		List<ProfilePic> userProfile = fileStorageService.getUserPic(user_id);
		System.out.println("----------------------------------"+userProfile.size());
		return ResponseEntity.ok(userProfile);
	}

	@Autowired
	RecipeService recipeService;

	@RequestMapping("/addRecipe")
	public ResponseEntity<?> addRecipe(@RequestBody RequestRecipeBody requestRecipeBody){
		return recipeService.saveRecipe(requestRecipeBody);
	}

	@PostMapping ("/getRecipe/{userId}")
	public ResponseEntity<?> getRecipe(@PathVariable("userId") String userId){
		System.out.println(userId);
		Long userIdLong = Long.valueOf(userId);
		return recipeService.getRecipe(userIdLong);
	}
}
