package school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import school.model.User;
import school.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Value("${jwt.validityInSeconds}")
    private long JWT_TOKEN_VALIDITY_IN_SECONDS = 60;
    private final UserService service;

    @PostMapping(path="/api/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> createUser(@RequestBody User userToCreate) {
        User userCreated = service.createStudent(userToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PostMapping(path="/api/authenticate", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> authenticate(@RequestBody User user) {
        String token = "";
        try {
            token = service.authenticate(user.getEmail(), user.getPassword(), JWT_TOKEN_VALIDITY_IN_SECONDS);
        } catch(Exception | Error e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .maxAge(JWT_TOKEN_VALIDITY_IN_SECONDS)
                // .httpOnly(true) //with false, the frontend javascript would not see this cookie
                .secure(true)
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}

    @GetMapping("grades")
	public ResponseEntity<Resource> servePageGrades(@CookieValue(value="token", defaultValue="") String token) {
        String target = "priviledged/grades.html";
        try {
            service.validateToken(token);
        } catch (Exception | Error e) {
            target = "static/index.html";
        }
        Resource htmlFile = new ClassPathResource(target);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlFile);
	}

}