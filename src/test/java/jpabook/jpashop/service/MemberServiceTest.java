package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kang");

        // when
        Long savedId = memberService.join(member);

        // then
        assertThat(savedId).isEqualTo(member.getId());
        assertThat(member).isEqualTo(memberService.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("사카");
        member2.setName("사카");

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        }, "예외가 발생해야 합니다.");    // 예외 발생해야 함, 발생하지 않으면 "예외가 발생해야 합니다." 출력
    }
}