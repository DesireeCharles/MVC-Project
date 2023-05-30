package school.service;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {
	private final Key key;
	private final JwtParser parser;

	public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
		key = new SecretKeySpec(secret.getBytes(),SignatureAlgorithm.HS256.getJcaName());
		parser = Jwts.parserBuilder().setSigningKey(key).build();
	}

	public String generateToken(String userName, long validityInSeconds) {
		Claims claims = Jwts.claims();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + validityInSeconds * 1000))
				.signWith(key)
				.compact();
	}

	public Claims validateToken(String token) {
		return parser.parseClaimsJws(token).getBody();
	}

}

