package zerobase.fintech.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.fintech.auth.PrincipalDetails;
import zerobase.fintech.service.PrincipalDetailsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final PrincipalDetailsService principalDetailsService;
  private static final long TOKEN_EXPIRE_DATE = 1000 * 60 * 60; //1시간
  private static final String KEY_ROLE = "role";

  @Value("${spring.jwt.secret}")
  private String secretKey;

  /**
   * 토근 생성
   *
   * @param username
   * @param role
   * @return
   */
  public String generateToken(String username, String role) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put(KEY_ROLE, role);

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_DATE);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String jwt){
    UserDetails userDetails = principalDetailsService.loadUserByUsername(getUsername(jwt));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean validateToken(String token){
    if(!StringUtils.hasText(token)){
      return false;
    }

    Claims claims = parseClaims(token);
    return !claims.getExpiration().before(new Date());
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}
