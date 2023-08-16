package com.travel.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
	Member findByEmail(String email);
	
	
	Member findByRole(String role);
	
	Member findByNameAndPhoneNumber(String name, String  phoneNumber);
	
	Member findByNameAndPhoneNumberAndEmail(String name, String  phoneNumber, String email);
	
	Page<Member> findAll(Pageable pageable);
	
	

}
