package com.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart extends BaseEntity {
  @Id
  @Column(name = "cart_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Builder
  public Cart(Member member) {
    this.member = member;
  }
}
