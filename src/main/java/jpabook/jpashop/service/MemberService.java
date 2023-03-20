package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    // 중복이면 예외
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findAllByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 회원입니다.")
        );
    }

    // 업데이트
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("해당 회원이 존재하지 않아요"));
        member.setName(name);
    }
}
