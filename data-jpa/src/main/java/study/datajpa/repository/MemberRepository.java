package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
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

    // @Query, 리포지토리 메소드에 쿼리 정의하기 -> 이 기능을 실무에서 많이 쓴다!
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    // m.username 잘못 적으면 앱 로딩 시점에 오류난다. 정적 쿼리를 모두 파싱해서 SQL 을 만들어놓는다! 장점이고 되게 막강한 기능이다.
    // 간단한 쿼리일 땐 쿼리 메소드 쓰고, 복잡한 정적 쿼리의 경우 이렇게 쓴다.

    // 동적 쿼리는 ? QueryDSL


    // @Query, 값 조회하기 : 지금까지는 엔티티 타입 조회였는데 단순 값, DTO 어떻게 조회하지?
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // @Query, DTO 조회하기 : JPQL 조인, DTO 조회할 때는 new operation(JPQL 에서 제공, 객체 생성해서 반환하는 것 같은 문법) 을 꼭 써줘야 한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
}