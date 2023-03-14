package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

//    @Query(name = "Member.findByUsername") // Member.findByUsername 는 NamedQuery.
// 근데 이거 주석 처리 해도 된다. 엔티티 타입에 있는 명에 .을 찍고 namedQuery 를 먼저 찾는다! 있으면 실행하고 없으면 쿼리 메서드를 만듦.
    List<Member> findByUsername(@Param("username") String username);
    // @Param 은 언제 적냐! -> 명확하게 JPQL 이 있을 때. :username 이렇게 분명하게 작성했을 때! 그럼 그 username 으로 매칭시켜줌.

}