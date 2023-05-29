package school.service;

import java.security.Key;
import java.util.Base64;
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
	// we could generate the secret here, instead of getting it from outside, 
	// as this app is for demonstrational purpose only
	// this way every restart of the app would invalidate the issued tokens
	// private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	private final Key key;
	private final JwtParser parser;

	public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
		byte[] secretBytes = Base64.getUrlEncoder().encode(secret.getBytes());
		key = new SecretKeySpec(secretBytes,SignatureAlgorithm.HS256.getJcaName());
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

