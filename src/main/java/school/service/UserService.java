package school.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import school.model.Role;
import school.model.User;
import school.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository repo;
    @Autowired
    private JwtTokenUtil tokenUtil;
    
    public User createStudent(User user) {
        User userByEmail = repo.findByEmail(user.getEmail());
        if (userByEmail != null) {
            return new User();
        }
        user.setId(null);
        user.setRole(Role.STUDENT);
        user.setPassword(getMD5Hash(user.getPassword()));
        return repo.save(user);
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

    public String authenticate(String username, String password, long validityInSeconds) {
        String passHash = getMD5Hash(password);
        User user = repo.findByEmailAndPassword(username, passHash);
        if (user==null)
            throw new IllegalAccessError("Authentication was not successful");
        return tokenUtil.generateToken(username, validityInSeconds);
    }

    public Claims validateToken(String token) {
        if (token!=null && !token.isBlank()) {
            try {
                return tokenUtil.validateToken(token);
            } catch (Exception e) {
                throw new IllegalAccessError("Token is not valid!");
            }
        } else {
            throw new IllegalAccessError("No token provided!");
        }
    }
}
