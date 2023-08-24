package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static com.shop.entity.Member.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {
  private final MemberService memberService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping(value = "/new")
  public String memberForm(Model model){
    model.addAttribute("memberFormDto", new MemberFormDto());
    return "member/memberForm";
  }

  @PostMapping(value = "/new")
  public String newMember(@Valid MemberFormDto memberFormDto,
                          BindingResult bindingResult, Model model) {
    log.info("MemberController - newMember");
    if(bindingResult.hasErrors()) {
      return "member/memberForm";
    }
    log.info("MemberController - newMember - after if");
    try {
      Member member = createMember(memberFormDto, passwordEncoder);
      memberService.saveMember(member);
    } catch (IllegalStateException e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "member/memberForm";
    }
    log.info("MemberController - newMember - before redirect");
    return "redirect:/";
  }

  @GetMapping(value = "/login")
  public String loginMember() {
    return "/member/memberLoginForm";
  }

  @GetMapping(value = "/login/error")
  public String loginError(Model model) {
    model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
    return "/emember/memberLoginForm";
  }
}
