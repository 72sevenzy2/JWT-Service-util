/* 
 * import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
 */
// those are the imports that you would require.

// @Service
// you would also need this annotation to declare this as an service method

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
// importing these so i get less errors x

public class JwtServiceHandler {
    private final String secretKey = "6d8fdd28e2ba7c5c4bca8a91a73a8c7f8e8cc55a76e8a213e35bca7f89345b21";
    // you may change this key 

    public String extractUsername(/* your actual token here, ill put one for a demo */ token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String generateToken(/* be sure to put ur actual user details here */ UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername() /*  or whatever method you have to get the username */)
        .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 40 * 24))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256 /* algorithm type */).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] keybytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Key.hmacShaKeyFor(keybytes);
    }
}