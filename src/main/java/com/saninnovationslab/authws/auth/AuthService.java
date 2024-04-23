package com.saninnovationslab.authws.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saninnovationslab.authws.userdetail.UserDetail;
import com.saninnovationslab.authws.userdetail.UserDetailRepository; 

@Service
public class AuthService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtility jwtUtility;

    public TokenPair authenticate(AuthenticationRequestModel model) {
        UserDetail userDetail = userDetailRepository.findByUsername(model.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!bCryptPasswordEncoder.matches(model.getPassword(), userDetail.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); 

        String refreshToken, accessToken;

        try {
            refreshToken = jwtUtility.generateToken(userDetail, AuthConstant.REFRESH_TOKEN_EXPIRY);
            accessToken = jwtUtility.generateToken(userDetail, AuthConstant.ACCESS_TOKEN_EXPIRY);
        } catch (JWTCreationException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new TokenPair(refreshToken, accessToken);
    }

    public String issueAccessToken(String refreshToken) {
        DecodedJWT decodedJWT;

        try {
            decodedJWT = jwtUtility.verifyToken(refreshToken);
        } catch (JWTVerificationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String accessToken;

        try {
            accessToken = jwtUtility.generateAccessToken(decodedJWT);
        } catch (JWTCreationException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return accessToken;
    }
}
