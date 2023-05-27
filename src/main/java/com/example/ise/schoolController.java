package com.example.ise;

import org.springframework.web.bind.annotation.GetMapping;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Connection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@SpringBootApplication
@Controller
public class schoolController {

    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    public static void main(String[] args) {
        SpringApplication.run(schoolController.class, args);
    }

    @GetMapping("/")
    public String registration(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            // User is already logged in, redirect to index page
            return "redirect:/index";
        }

        return "new_user";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            // User is already logged in, redirect to index page
            return "redirect:/index";
        }

        model.addAttribute("err", false);
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            // User is already logged in, redirect to index page
            return "redirect:/index";
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String passHash = getMD5Hash(password);

            String query = "SELECT L.password, U.* " +
                    "FROM SCHOOL.Login L " +
                    "LEFT JOIN SCHOOL.StudentRegistration U ON L.Login_id = U.Login_id " +
                    "WHERE LOWER(L.Email) = LOWER(?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    int isPending = result.getInt("is_pending");
                    int isApproved = result.getInt("is_approved");
                    String passwordHash = result.getString("password");

                    if (isPending == 0 && isApproved == 0) {
                        model.addAttribute("err",
                                "Your account was not approved. Contact Coordinator directly for more information.");
                    } else if (passHash.equals(passwordHash)) {
                        // Username and password are valid, so set user as logged in
                        session.setAttribute("username", username);
                        session.setAttribute("usertype", result.getInt("user_type_id"));
                        session.setAttribute("isapproved", isApproved);
                        session.setAttribute("ispending", isPending);
                        session.setAttribute("userid", result.getInt("User_id"));

                        return "redirect:/dash";
                    } else {
                        model.addAttribute("err", "Invalid username or password");
                    }
                } else {
                    model.addAttribute("err", "Invalid username or password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("err", "An error occurred while processing your request.");
        }

        return "login";
    }

    @GetMapping("/new_user")
    public String newUserAccount(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            // User is already logged in, redirect to index page
            return "redirect:/index";
        }

        return "new_user";
    }

    @PostMapping("/submit_new_user")
    public String submitNewUser(@RequestParam("fname") String firstName,
            @RequestParam("lname") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("address") String address,
            Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            // User is already logged in, redirect to index page
            return "redirect:/index";
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String passHash = getMD5Hash(password);

            String checkEmailQuery = "SELECT COUNT(*) FROM SCHOOL.Login WHERE Email = ?";
            String insertLoginQuery = "INSERT INTO SCHOOL.Login (Email, Password, Created, Updated) VALUES (?, ?, ?, ?)";
            String insertUserQuery = "INSERT INTO SCHOOL.StudentRegistration (First_name, Last_name, Student_id, Address, Telephone, Login_id, Created, Updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailQuery)) {
                checkEmailStatement.setString(1, email);
                ResultSet emailResult = checkEmailStatement.executeQuery();

                if (emailResult.next() && emailResult.getInt(1) == 0) {
                    try (PreparedStatement insertLoginStatement = connection.prepareStatement(insertLoginQuery,
                            Statement.RETURN_GENERATED_KEYS);
                            PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {

                        insertLoginStatement.setString(1, email);
                        insertLoginStatement.setString(2, passHash);
                        insertLoginStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                        insertLoginStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                        insertLoginStatement.executeUpdate();

                        ResultSet generatedKeys = insertLoginStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int loginId = generatedKeys.getInt(1);

                            insertUserStatement.setString(1, firstName);
                            insertUserStatement.setString(2, lastName);
                            // insertUserStatement.setInt(3,
                            // Integer.parseInt(request.getParameter("studentid")));
                            insertUserStatement.setString(4, address);
                            insertUserStatement.setString(5, phone);
                            insertUserStatement.setInt(6, loginId);
                            insertUserStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                            insertUserStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                            insertUserStatement.executeUpdate();
                        } else {
                            throw new SQLException("Failed to insert user login data. No generated key obtained.");
                        }

                        connection.commit();
                        return "after_submit";
                    }
                } else {
                    model.addAttribute("e", "Email already exists");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("e", "An error occurred while processing your request.");
        }

        return "new_user";
    }

    private static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
