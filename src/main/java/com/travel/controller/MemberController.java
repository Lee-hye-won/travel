package com.travel.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.travel.Dto.MemberFormDto;
import com.travel.Dto.PasswordDto;
import com.travel.auth.PrincipalDetails;
import com.travel.constant.Division;
import com.travel.entity.Member;
import com.travel.entity.Plan;
import com.travel.entity.PlanCommunity;
import com.travel.service.CommunityService;
import com.travel.service.MemberService;
import com.travel.service.PlanService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberservice;
	private final PlanService planService;
	private final CommunityService communityService;
	private final PasswordEncoder passwordEncoder;

	// 로그인 화면
	@GetMapping(value = "/members/login")
	public String loginmember() {
		return "member/memberLoginForm";
	}

	// 회원가입 화면
	@GetMapping(value = "/members/new")
	public String memberForme(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/memberForm";
	}

	// 회원가입
	@PostMapping(value = "/members/new")
	public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model,	RedirectAttributes rttr) {
		if (bindingResult.hasErrors()) {
			return "member/memberForm";
		}

		try {
			Member member = Member.createMember(memberFormDto, passwordEncoder);

		    String message ="회원가입이 완료 되었습니다.";
		    rttr.addAttribute("message", message);
			memberservice.saveMember(member);

			
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "member/memberForm";
		}
		return "redirect:/members/login";
	}

	// 로크인에러
	@GetMapping(value = "/members/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");

		return "member/memberLoginForm";
	}

	// 아이디 찾기
	@GetMapping(value = "/account/search")
	public String search_id(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/LoginForm";
	}

	@PostMapping("/account/search")
	@ResponseBody
	public HashMap<String, String> members(@RequestBody Map<String, Object> data) {
		String name = (String) data.get("memberName");
		String phone = (String) data.get("memberPhoneNumber");

		HashMap<String, String> msg = new HashMap<>();
		String email = memberservice.emailFind(name, phone);

		msg.put("message", email);
		return msg;
	}

	// 비번찾기
	@GetMapping(value = "/account/pssearch")
	public String search_ps(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());

		return "member/psLoginForm";
	}

	// 비밀번호 찾고 난수생성기로 랜덤비밀번호 생성
	@PostMapping("/account/pssearch")
	@ResponseBody
	public HashMap<String, String> memberps(@RequestBody Map<String, Object> psdata) {
		String email = (String) psdata.get("memberEmail");
		
		Member member = memberservice.findByEmail(email);
		HashMap<String, String> msg = new HashMap<>();

		try {
				if(email == ""){
					 throw new IllegalStateException("이메일을 입력해주세요.");
				}
				else if (member == null) {
		            throw new IllegalStateException("존재하지 않은 계정입니다. 아이디 찾기 혹은 회원가입 후 이용해주세요.");
		        }
			if (member.getDivision() == Division.NORMAL) {
				String pass = memberservice.passwordFind(email);

				// pass 암호화된 비밀번호
				String ramdomps = memberservice.getRamdomPassword(12);

				// ramdomps 를 view에 출력
				String password = memberservice.updatePassword(ramdomps, email, passwordEncoder);
				memberservice.sendEmail(email, "새로운 비밀번호", "새로운 비밀번호: " + ramdomps);
				String asd = "이메일로 임시 비밀번호가 발송되었습니다.";
				msg.put("message", asd);
			} else if (member.getDivision() == Division.KAKAO) {
				String asd = "현재 계정은 카카오 계정이므로 변경이 불가능합니다..";
				msg.put("message", asd);
			} else {
				String asd = "존재하지 않은 계정입니다. 아이디 찾기 혹은 회원가입 후 이용해주세요";
				msg.put("message", asd);
			}
		} catch (IllegalStateException e) {
		    String errorMessage = e.getMessage(); // 예외 메시지 가져오기
	        msg.put("message", errorMessage);
	        e.printStackTrace();
			
		}

		return msg;
	}

	//마이페이지
	@GetMapping(value = "/member/mypage")
	public String mainMypage( Model model, Authentication authentication) {

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		
		Member principalDetail = principalDetails.getMember();

		model.addAttribute("member", principalDetail);
		
		String memberId = principalDetail.getEmail();
		
		
		List<Plan> plans = planService.findPlanTopByEmail(memberId);
		
		
		List<PlanCommunity> planCommunity = communityService.getTop3RecentCommunitiesByMemberEmail(memberId);
		
		model.addAttribute("plan", plans);
		model.addAttribute("community", planCommunity);
		
		
		
		
		return "member/MyPage";
	} 
	
	
	

	// 내 정보 수정
	@GetMapping(value = "/member/mypageupdate")
	public String mypageupdate(Principal principal, Model model, Authentication authentication) {
	
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			Member principalDetail = principalDetails.getMember();
			Member member = memberservice.findByEmail(principalDetail.getEmail());
			model.addAttribute("member", member);

		return "member/MypageupdateForm";
	}

	// 카카오 이용자는 내 정보 수정이 불가능하여서 넣지 않아서 Authentication를 넣지 않음
	@PostMapping("/member/mypageupdate")
	public String mypageupdate(@Valid String name, @Valid String phoneNumber, Model model, Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		Member principalDetail = principalDetails.getMember();
		Member member = memberservice.findByEmail(principalDetail.getEmail());
		memberservice.updateNamePhone(member.getEmail(), name, phoneNumber);

		return "redirect:/";
	}
	
	@GetMapping("/member/checkPwd")
	@NotBlank
	public String checkPwdView(Model model) {
		model.addAttribute("passwordDto", new PasswordDto());

		return "member/checkPwd";
	}

	// 회원 수정 전 비밀번호 확인 
	@PostMapping(value = "/member/checkPwd")
	public String checkPwd(@Valid PasswordDto passwordDto, Principal principal, Model model) {

		  if (passwordDto.getPassword() == null || passwordDto.getPassword().trim().isEmpty()) {
		        model.addAttribute("errorMessage", "비밀번호 값이 입력되어 있지 않습니다..");
		        return "member/checkPwd";
		    }
		Member member = memberservice.findByEmail(principal.getName());

		boolean result = passwordEncoder.matches(passwordDto.getPassword(), member.getPassword());

		if (!result) {
			model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
			return "member/checkPwd";
		}

		return "member/EditMember";
	}

	// 내 비밀번호수정 (마이페이지에서)
	@GetMapping(value = "/member/EditMember")
	public String passwordupdate(Principal principal, Model model) {
		Member member = memberservice.memberMypage(principal.getName());
		model.addAttribute("member", member);
		return "member/EditMember";
	}

	@PostMapping("/member/EditMember")
	public String passwordupdate(@RequestParam String password, Model model, Principal principal, Member member) {
		Member members = memberservice.memberMypage(principal.getName());
		   if (password == null || password.trim().isEmpty()) {
		        model.addAttribute("errorMessage", "비밀번호 값이 없습니다.");
		        model.addAttribute("member", member);
		        return "member/EditMember";
		    }
		    
		
		if (passwordEncoder.matches(password, members.getPassword()) == true) {
			model.addAttribute("errorMessage", "기존 비밀번호와 같습니다.");
			model.addAttribute("member", member);
			return "member/EditMember";
		}
		
		else {
			memberservice.updatepassword(principal.getName(), passwordEncoder.encode(password), passwordEncoder);
			return "redirect:/member/mypage";
		}

	}

	// 탈퇴하기
	@DeleteMapping(value = "/member/{memberId}/delete")
	public @ResponseBody ResponseEntity deleteMember(@RequestBody @PathVariable("memberId") Long memberId,
			Principal principal) {

		memberservice.deleteMember(memberId);

		return new ResponseEntity<Long>(memberId, HttpStatus.OK);
	}

}