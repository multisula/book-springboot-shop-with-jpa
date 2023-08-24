package com.shop.entity;

import com.shop.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
public class MemberTest {
  @Autowired
  MemberRepository memberRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  @DisplayName("Auditing 테스트")
  @WithMockUser(username = "gildong", roles = "USER")
  public void auditingTest() {
    Member newMember = new Member();
    memberRepository.save(newMember);

    em.flush();
    em.clear();

    Member member = memberRepository.findById(newMember.getId())
        .orElseThrow(EntityNotFoundException::new);

    log.info("register time: {}", member.getRegTime());
    log.info("update time: {}", member.getUpdateTime());
    log.info("create member: {}", member.getCreatedBy());
    log.info("modify member: {}", member.getModifiedBy());

    assertThat(member.getCreatedBy()).isEqualTo("gildong");
    assertThat(member.getModifiedBy()).isEqualTo("gildong");
  }
}
