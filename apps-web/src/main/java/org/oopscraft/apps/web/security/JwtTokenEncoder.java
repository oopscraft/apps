package org.oopscraft.apps.web.security;

import io.jsonwebtoken.*;
import org.oopscraft.apps.core.support.JsonObjectMapper;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtTokenEncoder {

    /**
     * encode
     * @param userDetails
     * @return
     */
    public String encode(UserDetails userDetails, String secretKey, int expireMinutes) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.claim("userDetails", JsonObjectMapper.toJson(userDetails));
        if(expireMinutes > 0) {
            jwtBuilder.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(expireMinutes).toInstant()));
        }
        jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey);
        jwtBuilder.compressWith(CompressionCodecs.GZIP);
        return jwtBuilder.compact();
    }

    /**
     * decode
     * @param jwtToken
     * @return
     */
    public UserDetails decode(String jwtToken, String secretKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken).getBody();
        String userDetailsJson = (String)claims.get("userDetails");
        UserDetails userDetails = JsonObjectMapper.toObject(userDetailsJson, UserDetails.class);
        return userDetails;
    }


}
