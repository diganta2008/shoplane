package com.shoplane.auth;

import com.shoplane.config.ShopLaneProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final ShopLaneProperties props;
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtService(ShopLaneProperties props) {
        this.props = props;
        this.accessKey  = Keys.hmacShaKeyFor(pad(props.jwt().accessSecret()));
        this.refreshKey = Keys.hmacShaKeyFor(pad(props.jwt().refreshSecret()));
    }

    /** HS256 requires at least 256-bit keys; pad short dev secrets. */
    private static byte[] pad(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        if (b.length >= 32) return b;
        byte[] out = new byte[32];
        System.arraycopy(b, 0, out, 0, b.length);
        return out;
    }

    public String signAccess(Long userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.jwt().issuer())
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(props.jwt().accessTtlMinutes()))))
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public String signRefresh(Long userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.jwt().issuer())
                .subject(String.valueOf(userId))
                .claims(Map.of("email", email, "typ", "refresh"))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofDays(props.jwt().refreshTtlDays()))))
                .signWith(refreshKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseAccess(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(accessKey)
                .requireIssuer(props.jwt().issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRefresh(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(refreshKey)
                .requireIssuer(props.jwt().issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
