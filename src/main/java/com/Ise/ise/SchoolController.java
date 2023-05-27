package com.Ise.ise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.sql.SQLException;


@Controller
public class SchoolController {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initialize() {
        System.out.println("start");
        jdbcTemplate = new JdbcTemplate(dataSource);
        System.out.println(jdbcTemplate);
        try {
            if (jdbcTemplate.getDataSource() != null && jdbcTemplate.getDataSource().getConnection() != null) {
                System.out.println("Connection is established!");
            } else {
                System.out.println("Failed to establish connection!");
            }
        } catch (SQLException e) {
            System.out.println("Error establishing connection: " + e.getMessage());
        }
        System.out.println("hello");

    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginCheck(@RequestParam String email, @RequestParam String password, Model model) {
        try {
            // Perform login check here
            String sql = "SELECT COUNT(*) FROM SCHOOL.USERS WHERE email = ? AND password = ?";
            int count = jdbcTemplate.queryForObject(sql, Integer.class, email, passwordEncoder.encode(password));
            if (count == 1) {
                model.addAttribute("message", "Login successful!");
                return "success";
            } else {
                model.addAttribute("message", "Invalid email or password");
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "An error occurred during login");
            return "error";
        }
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@RequestParam Map<String, String> requestParams, Model model) {
        try {
            String firstName = requestParams.get("firstName");
            String lastName = requestParams.get("lastName");
            String studentId = requestParams.get("studentId");
            String email = requestParams.get("email");
            String phoneNo = requestParams.get("phoneNo");
            String password = requestParams.get("password");

            // Hash the password
            String hashedPassword = passwordEncoder.encode(password);

            // Insert user into the database
            String sql = "INSERT INTO SCHOOL.USERS (first_name, last_name, student_id, email, telephone, password) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, firstName, lastName, studentId, email, phoneNo, hashedPassword);

            model.addAttribute("message", "Registration successful!");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "An error occurred during registration");
            return "error";
        }
    }
}
