package com.travel.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.travel.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	Member findByEmail(String name);
	
	
	Member findByNameAndPhoneNumber(String name, String  phoneNumber);
	
	Member findByNameAndPhoneNumberAndEmail(String name, String  phoneNumber, String email);
	Member findByNameAndPhoneNumberAndEmailAndPassword(String name, String  phoneNumber, String email , String password);
	
	Member findByRole(String role);
	
	 
	

//	Optional<Member> findbyEmailAndProvider(String email, String provider);
	

}
