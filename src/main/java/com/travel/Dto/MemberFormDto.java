package com.travel.Dto;

import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import com.travel.entity.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFormDto {
	private Long id;
	
	@NotBlank(message = "이름은 필수 입력 값입니다.")
	private String name;
	
	
	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	private String email;
	
	@NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
	@Length(min = 8 , max = 16 , message = "비밀번호 8자~ 16자 사이로 입력해주세요.")
	private String password;
	
	@NotBlank(message = "전화번호는 필수 입력 값 입니다.")
	private String phoneNumber;
private static ModelMapper modelMapper = new ModelMapper();
	
	
	public Member createMember() {
		return modelMapper.map(this , Member.class);
	}

	//entity -> dto로 바꿈
	public static MemberFormDto of(Member member) {
		return modelMapper.map(member, MemberFormDto.class);
	}
	
	
}