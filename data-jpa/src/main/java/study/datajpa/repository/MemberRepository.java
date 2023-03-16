package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    // 컬렉션 파라미터 바인딩 -> 많이 쓰는 기능. in 절로 여러 개를 조회하고 싶을 때!
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names); // List 보다 상위의 Collection 을 적는 게 나음.


    // 스프링 데이터 JPA 는 유연한 반환 타입 지원
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional


    // 스프링 데이터 JPA 의 페이징 쿼리 + 쿼리 메소드(메소드 이름으로 쿼리 만들기)
    // 1.
//    Page<Member> findByAge(int age, Pageable pageable);
    // 반환 타입으로 Pageable 넣고(쿼리에 대한 조건. 현재 1페이지 2페이지) 반환 타입을 Page 라고 하면, totalCount 쿼리를 알아서 같이 날린다!

    // 2.
//    Slice<Member> findByAge(int age, Pageable pageable);

    // 3.
//    List<Member> findByAge(int age, Pageable pageable);


    // 페이징 쿼리를 왜 안 쓰려고 하냐면, totalCount 쿼리가 DB의 모든 데이터를 count 해야함. 이 카운트 자체 성능이 보통 느린 것이다.
    // 그래서 totalCount 쿼리를 되게 잘 짜야할 때가 있다. left outer join 을 다 한다고 가정하면, 카운트 쿼리 할 때는 join 할 필요가 없다.
    // -> 카운트 쿼리를 분리하는 방법을 제공한다!
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m) from Member m")
//    Page<Member> findByAge(int age, Pageable pageable);


    @Query(value = "select m from Member m")
    Page<Member> findByAge(int age, Pageable pageable);


    // 회원의 나이를 한 번에 변경하는 쿼리
    // 스프링 데이터 JPA 를 사용한 벌크성 수정 쿼리
    @Modifying(clearAutomatically = true) // 이게 있어야 executeUpdate() 를 실행한다. 이게 없으면 resultList 나 singleResult 를 호출함. 뒤 옵션은 em.clear
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    // fetch join : 'fetch' 키워드를 주면, Member 를 조회할 때 연관된 Team 을 한 번에 다 끌고 옴!
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    // 가짜 프록시 객체가 아니라 진짜 Team 객체에 값이 다 들어있음!

    // 근데 fetch join 을 하려면 무조건 JPQL 을 써야 한다? 간단한 거 할 때는 그게 좀 귀찮음.
    // -> 그래서 @EntityGraph : 메서드 이름으로 해결해야 하는데, fetch join 도 하고 싶다 !!
    // = 기존 findAll() 은 Member 만 조회하는 건데, Team 까지 조회하고 싶어 + 근데 JPQL 시러 !
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL 도 짜고 fetch join 도 하고 싶다면? JPQL + @EntityGraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 회원 조회할 때 회원 데이터만 쓰면 @EntityGraph 가져올 필요 없는데, 팀 쓸 때가 많아서.
    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // @NamedEntityGraph
//    @EntityGraph("Member.all") // 바로 @NamedEntityGraph 실행 됨.
//    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 정리: 스프링 데이터가 중요한 게 아니라 JPA 자체를 잘 알아야 한다.
    // @EntityGraph 는 fetch join 해주는 것이다. 간단한 거 할 때 이거 쓰고 좀 복잡해지면 JPQL + fetch join 쓴다!


    // JPA Hint
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) // 내부적으로 성능 최적화. 이 정도만 쓰고 많이 안 쓰긴 함.
    Member findReadOnlyByUsername(String username);
    // 근데 사실 우리 수준에서 조회 성능 최적화 돼봤자고, 모자라면 진작 캐시를 썼어야 한다. 조금씩 튜닝해보면 좋을 것 같을 때 사용해도 되고, 처음부터 튜닝을 다 깐다? 좋지 않다. 그렇게 안 해도 성능 좋게 나옴.

    // JPA Lock
    // select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


}