package com.travel.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.travel.Dto.MemberFormDto;
import com.travel.Dto.MemberKakaoDto;
import com.travel.constant.Division;
import com.travel.constant.Role;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
	
	@Id
	@Column(name="member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //멤버 아이디
	
	@Column(unique = true, length=255)
	private String email; //이메일
	
	@Column(unique = false, length=255)
	private String name; //이름
	
	@Column(nullable =false , length=255)
	private String password; // 비밀번호
	
	@Column(nullable = true)
	private String phoneNumber; //폰번호
	
	@CreatedDate
	@Column(updatable =  false)
	private LocalDateTime regtime;
	
	@Enumerated(EnumType.STRING)
	private Role role; //역할
	
	@Enumerated(EnumType.STRING)
	private Division division; //역할
	
	public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(memberFormDto.getPassword());
		
		Member member = new Member();
		member.setName(memberFormDto.getName());
		member.setEmail(memberFormDto.getEmail());
		member.setPhoneNumber(memberFormDto.getPhoneNumber());
		member.setPassword(password);
		member.setRole(Role.USER);
		member.setRegtime(memberFormDto.getRegtime());
		member.setDivision(Division.NORMAL);
		return member;
		
	}

	public  void updateMember(MemberFormDto memberFormDto) {
		this.password = memberFormDto.getPassword();
	}
	public String  updatePassword(String pass,PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(pass);
		this.password = password;

		return password;
	}
	
	public void  updatenamePhone(String name, String phone) {
		this.name = name;
		this.phoneNumber = phone;
	}
	
	public void updatepassword(String password) {
		this.password = password;
	}

	
    public static Member createKaKao(MemberKakaoDto memberKakaoDto) {
    	Member member = new Member();
    	
    	member.setName(memberKakaoDto.getName());
    	member.setEmail(memberKakaoDto.getEmail());
    	member.setRole(Role.USER);
		member.setRegtime(memberKakaoDto.getRegtime());
		member.setDivision(Division.KAKAO);
    	return member;
    }
    
    public static final String MAPPER = "ezen.dev.spring.kakao";
    
    // 정보 저장
    public void kakaoinsert(HashMap<String, Object> userInfo) {
        this.kakaoinsert(userInfo);
    }
	

}

