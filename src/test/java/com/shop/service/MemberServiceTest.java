package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  PasswordEncoder passwordEncoder;

  public Member createMember() {
    MemberFormDto memberFormDto = MemberFormDto.builder()
        .email("test@email.com")
        .name("홍길동")
        .address("서울시 마포구 합정동")
        .password("1234")
        .build();
    return Member.createMember(memberFormDto, passwordEncoder);
  }

//  @BeforeEach
//  public void beforeEach() {
//
//  }

  @Test
  @DisplayName("회원가입 테스트")
  public void saveMemberTest() {
    Member member = createMember();
    Member savedMember = memberService.saveMember(member);

    assertThat(member.getEmail()).isEqualTo(savedMember.getEmail());
    assertThat(member.getName()).isEqualTo(savedMember.getName());
    assertThat(member.getAddress()).isEqualTo(savedMember.getAddress());
    assertThat(member.getPassword()).isEqualTo(savedMember.getPassword());
    assertThat(member.getRole()).isEqualTo(savedMember.getRole());
  }

  @Test
  @DisplayName("중복 회원 가입 테스트")
  public void saveDuplicateMemberTest() {
    Member member1 = createMember();
    Member member2 = createMember();
    memberService.saveMember(member1);

//    Throwable e = Assertions.assertThrows();
    assertThatThrownBy(() -> memberService.saveMember(member2))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("이미 가입된 회원입니다.");
  }
}