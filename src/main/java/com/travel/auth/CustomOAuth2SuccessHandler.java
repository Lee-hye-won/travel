package com.travel.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.travel.Repository.MemberRepository;
import com.travel.entity.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Member member = principal.getMember();
        
        
        String targetUrl = determineTargetUrl(member);
        response.sendRedirect(targetUrl);
	}
	
    private String determineTargetUrl(Member member) {
    	
    	Member findMember = memberRepository.findByEmail(member.getEmail());
    	
        if (findMember == null) {
            return "oauth2/code/kakao";
        } else {
            return "/";
        }
    }}
