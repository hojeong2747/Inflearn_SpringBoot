package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 공통으로 만들 수 있는 영역이 아니라,  검색 조건이 들어간 문제는 어떻게 해결하지? -> 스프링 데이터 jpa 는 쿼리 메소드 기능을 제공한다!
//    List<Member> findByUsername(String username);
    // 이걸 스프링 데이터 jpa 가 만들어준다.

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // By 뒤에 조건을 안 넣어주면? 전체 조회 -> Member 다 조회
    // Top3 넣을 수도 있음 -> from
    //        member member0_ limit ?
    List<Member> findTop3HelloBy();
}