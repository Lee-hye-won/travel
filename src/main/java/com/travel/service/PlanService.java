package com.travel.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travel.Dto.PlanContentDto;
import com.travel.Dto.PlanFormDto;
import com.travel.Repository.MemberRepository;
import com.travel.Repository.PlanContentRepository;
import com.travel.Repository.PlanRepository;
import com.travel.entity.Member;
import com.travel.entity.Plan;
import com.travel.entity.PlanContent;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
	private final PlanContentRepository planContentRepository;
	private final PlanRepository planRepository;
	private final MemberRepository memberRepository;
	
	//플랜 만들기
	public Plan setPlan(String no, PlanFormDto planFormDto) {
		Member member = memberRepository.findByEmail(no);
		LocalDateTime now = LocalDateTime.now();
		String regDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
		
		Plan plan = new Plan();
		plan.setMember(member);
		plan.setPlanDate(planFormDto.getPlanDate());
		plan.setPlanTitle(planFormDto.getPlanTitle());
		plan.setRegDate(regDate);
		
		Plan planResult = planRepository.save(plan);
		
		return planResult;
		
	}
	
	
	//플랜 컨텐츠 추가하기
	public PlanContent setPlanContent(PlanContentDto planContentDto, Plan plan) {
		
		List<PlanContent> planContentList = new ArrayList<>();
		PlanContent planContent = PlanContent.createContent(planContentDto, plan);
		planContentList.add(planContent);
		PlanContent savePlanContent = planContentRepository.save(planContent);
		
		return savePlanContent;
		
	}
	
	
	
	//플랜 찾기
	public List<Plan> findPlan(Long memberId) {
		return planRepository.findByPlanId(memberId);
	}
	
	//email을 사용해서 플랜 찾기
	public List<Plan> findPlanByEmail(String email){
		return planRepository.findByMember_Email(email);
	}
	
	//최근 플랜 찾기
	public Plan findLastPlan(String memberNo, Pageable pageable){
	    Member member = memberRepository.findByEmail(memberNo);
	    if (member == null) {
	        return null; // Or throw an exception
	    }
	    Long memberId = member.getId();

	    List<Plan> plans = planRepository.findLatestByMemberId(memberId, pageable);
	    return plans.isEmpty() ? null : plans.get(0);
	}

	
	//플랜 컨텐츠 찾기
	public List<PlanContent> findPlanContentsByPlanId(Long planId){
		return planContentRepository.findByPlan_Id(planId);
	}
	
	
}
