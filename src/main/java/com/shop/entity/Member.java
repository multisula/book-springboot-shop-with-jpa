package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "member")
@Entity
public class Member extends BaseEntity {
  @Id
  @Column(name="member_id")
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;

  private String name;

  @Column(unique = true)
  private String email;

  private String password;

  private String address;

  @Enumerated(EnumType.STRING)
  private Role role;

  public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
    return new Member().builder()
        .name(memberFormDto.getName())
        .email(memberFormDto.getEmail())
        .address(memberFormDto.getAddress())
        .password(passwordEncoder.encode(memberFormDto.getPassword()))
        .role(Role.USER)
        .build();
  }

  public static Member createAdminMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
    return new Member().builder()
        .name(memberFormDto.getName())
        .email(memberFormDto.getEmail())
        .address(memberFormDto.getAddress())
        .password(passwordEncoder.encode(memberFormDto.getPassword()))
        .role(Role.ADMIN)
        .build();
  }

  @Builder
  public Member(String name, String email, String password, String address, Role role) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.address = address;
    this.role = role;
  }
}
