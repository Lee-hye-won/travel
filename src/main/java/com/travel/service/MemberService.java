package com.travel.service;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travel.Dto.MemberFormDto;
import com.travel.Repository.MemberRepository;
import com.travel.auth.PrincipalDetails;
import com.travel.constant.Division;
import com.travel.entity.Member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // 퀘리문 오류시 이전데이터 롤백
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;

	public Member saveMember(Member member) {
		validateDuplicatMember(member);
		Member savedMember = memberRepository.save(member);

		return savedMember;
	}

	private void validateDuplicatMember(Member member) {
		Member findMember = memberRepository.findByEmail(member.getEmail());

		if (findMember != null) {
			throw new IllegalStateException("이미 사용중인 Email 입니다");
		}
	}

	public Member findByEmail(String email) {
		Member member = memberRepository.findByEmail(email);
	    return member;
	}
	
	public String emailFind(String name, String phone) {
		Member member = memberRepository.findByNameAndPhoneNumber(name, phone);

		if (member == null) {
			return "일치하는 사용자가 없습니다";
		}
		return "고객님의 아이디는 " + member.getEmail() + " 입니다";
	}

	public void updateNamePhone(String email, String name, String phone) {

		Member member = memberRepository.findByEmail(email);

		member.updatenamePhone(name, phone);

	}

	public void updatepassword(String email, String password, PasswordEncoder passwordEncoder) {
		Member member = memberRepository.findByEmail(email);

		if (passwordEncoder.matches(password, member.getPassword()) == true) {
			throw new IllegalStateException("기존 비밀번호와 동일합니다.");
		} else {
			member.updatepassword(password);
		}
	}

	public String getRamdomPassword(int size) {
		char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
				'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
				'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', '!', '@', '#', '$', '%', '^', '&' };

		StringBuffer sb = new StringBuffer();
		SecureRandom sr = new SecureRandom();
		sr.setSeed(new Date().getTime());

		int idx = 0;
		int len = charSet.length;
		for (int i = 0; i < size; i++) {
			// idx = (int) (len * Math.random());
			idx = sr.nextInt(len); // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
			sb.append(charSet[idx]);
		}

		return sb.toString();
	}

	public String updatePassword(String pass, String email, PasswordEncoder passwordEncoder) {
		Member member = memberRepository.findByEmail(email);

		String password = member.updatePassword(pass, passwordEncoder);

		return password;
	}

	public String passwordFind(String email) {


		
		
		if (memberRepository.findByEmail(email) == null) {
			return "일치하는 사용자가 없습니다";
		}
		Member member = memberRepository.findByEmail(email);
		
		if(member.getDivision() ==Division.KAKAO ){
			return "카카오 사용자입니다";
		}

		return member.getPassword();
	}


	
	  private final JavaMailSender javaMailSender;
	  
	  
	  
	  public void sendEmail(String to, String subject, String text) {
	  SimpleMailMessage message = new SimpleMailMessage(); message.setTo(to);
	  message.setSubject(subject); message.setText(text);
	  
	  javaMailSender.send(message);
	  
	  }
	 
	  
	

	// 회원 상세정보
	@Transactional(readOnly = true)
	public Member getmemberDts(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);

		return member;
	}

	// 회원 상세정보
	@Transactional(readOnly = true)
	public MemberFormDto getmemberDtl(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
		MemberFormDto memberFormDto = MemberFormDto.of(member);

		return memberFormDto;
	}

	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);

		memberRepository.delete(member);
	}

	public Member memberMypage(String email) {
		Member member = memberRepository.findByEmail(email);

		return member;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// 사용자가 입력한 email이 DB에 있는지 쿼리문을 사용한다.
		Member member = memberRepository.findByEmail(email);

		if (member == null) {
			throw new UsernameNotFoundException(email);
		}
		
		
		 return new PrincipalDetails(member);
	}

	@Transactional(readOnly = true)
	public Page<Member> getAdminlistPage(Pageable pageable) {
		return memberRepository.findAll(pageable);
	}



}
