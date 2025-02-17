package org.likelion.tm8.auth.application;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.likelion.tm8.auth.api.dto.Token;
import org.likelion.tm8.global.TokenProvider;
import org.likelion.tm8.user.domain.User;
import org.likelion.tm8.user.domain.UserInfo;
import org.likelion.tm8.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

    //콘솔에 Authorization code 출력
//    public void socialLogin(String code, String registrationId) {
//        System.out.println("code = " + code);
//        System.out.println("registrationId = " + registrationId);
//    }
    @Value("${client-id}")  // value import 할때 lombok으로 하면 안됨.
    private String GOOGLE_CLIENT_ID;

    @Value("${client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_REDIRECT_URI = "http://localhost:8080/callback";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
//        Map<String, String> params = Map.of(
//                "code", code,
//                "scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
//                "client_id", GOOGLE_CLIENT_ID,
//                "client_secret", GOOGLE_CLIENT_SECRET,
//                "redirect_uri", GOOGLE_REDIRECT_URI,
//                "grant_type", "authorization_code"
//        );

//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", GOOGLE_REDIRECT_URI);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> entity = null;
        entity = restTemplate.exchange(GOOGLE_TOKEN_URL, HttpMethod.POST, httpEntity, String.class);

        if (entity.getStatusCode().is2xxSuccessful()) {
            String json = entity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, Token.class)
                    .getAccessToken();
        }

        throw new RuntimeException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    public Token loginOrSignUp(String googleAccessToken) {
        UserInfo userInfo = getUserInfo(googleAccessToken);



        User user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .name(userInfo.getName())
                        .build())
        );

        return tokenProvider.createToken(user);
    }

    public UserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, UserInfo.class);
        }

        throw new RuntimeException("유저 정보를 가져오는데 실패했습니다.");
    }
}
