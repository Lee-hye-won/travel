package com.travel.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.travel.auth.CustomOAuth2SuccessHandler;
import com.travel.service.PrincipalOauth2UserService;

@Configuration // bean 객체를 싱글톤으로 공유할 수 있게 해준다.
@EnableWebSecurity // spring security filterCahin이 자동으로 포함되게한다.
public class SecurityConfig {

	private final PrincipalOauth2UserService principalOauth2UserService;

	public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService) {
		this.principalOauth2UserService = principalOauth2UserService;
	}

	@Bean
	public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
		return new CustomOAuth2SuccessHandler();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 로그인에 대한설정
		http.authorizeHttpRequests(authorize -> authorize // 1.페이지 접근에 관한
				
				// 모든 사용자가 로그인(인증) 없이 접근할 수 있도록 설정
				.requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**", "/search-data/**").permitAll()
				.requestMatchers("/", "/members/**", "/planner/**", "/account/**", "/pay/**", "/account/pssearch/**", 
						"/adminInfo", "/adminAsk/asks", "/info/**",
						"/members/login/**", "/item/**", "/kakao/**", "/order/**", "/oauth2/code/**", "/load/**", "/community/**")
				.permitAll().requestMatchers("favicon.ico", "/error").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				// 그 외의 페이지는 모두 로그인(인증을 받아야한다.)
				.anyRequest().authenticated())		
		.formLogin(formLogin -> formLogin // 2.로그인에 관련된 설정
						.loginPage("/members/login") // 로그인 페이지 URL 설정
						.defaultSuccessUrl("/") // 로그인 성공시 이동할 페이지
						.usernameParameter("email") // 로그인시 id로 사용할 파라메터 이름
						.failureUrl("/members/login/error")) // 로그인 실패시 이동할 URL
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/members/login")
						.clientRegistrationRepository(clientRegistrationRepository())
						.successHandler(oauth2AuthenticationSuccessHandler())
						.failureUrl("/login/error")
						.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(principalOauth2UserService)))
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃시 이동할
						.logoutSuccessUrl("/") // 로그아웃 성공시 이동할 URL
				) // 로그아웃에 관련된 설정

				.exceptionHandling(handling -> handling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
//				.rememberMe(Customizer.withDefaults());

		; // 카카오 로그인 성공 핸들러 설정

		return http.build();
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		ClientRegistration kakaoClientRegistration = ClientRegistration.withRegistrationId("kakao")
				.clientId("98f390fb8a9c8c0429606de4259a885c")
				.clientSecret("0iVG2qqlKsohbvX0Sb8euy3hO2j5HPbn")
				.tokenUri("https://kauth.kakao.com/oauth/token")
				.authorizationUri("https://kauth.kakao.com/oauth/authorize")
				.userInfoUri("https://kapi.kakao.com/v2/user/me")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST) // 여기에서 인증 방식을 설정
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri("http://ec2-43-200-178-19.ap-northeast-2.compute.amazonaws.com/login/oauth2/code/kakao").userNameAttributeName("id").clientName("Kakao")
				.build();

		return new InMemoryClientRegistrationRepository(kakaoClientRegistration);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}