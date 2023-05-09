package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 2개가 있는데 spring 이 제공하는 거 쓰는 것을 권장 (여기선 readOnly 가 default)
@RequiredArgsConstructor // final 필드만 갖고 생성자를 만들어서 주입해준다.
public class MemberService {

    private final MemberRepository memberRepository; // final 과 @RequiredArgsConstructor 조합으로 쓰기

//     생성자 주입
//    @Autowired // 자동으로 스프링이 해준다.
//    @AllArgsConstructor // 이게 밑 생성자를 만들어준다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 증복 회원 검증 비즈니스 로직 추가
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단권 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
