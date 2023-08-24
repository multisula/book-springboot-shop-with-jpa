package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));

    return User.builder()
        .username(member.getEmail())
        .password(member.getPassword())
        .roles(member.getRole().toString())
        .build();
  }

  public Member saveMember(Member member) {
    validateDuplicateMember(member);
    return memberRepository.save(member);
  }

  private void validateDuplicateMember(Member member) {
    Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
    if(findMember.isPresent()) {
      throw new IllegalStateException("이미 가입된 회원입니다.");
    }
  }
}
