package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    // 전체 조회 -> jpa 가 제공하는 JPQL 기술 사용해야 함. Member 가 엔티티. 테이블 대상이 아니라 객체를 대상으로 하고 모양은 sql 과 똑같음.
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList(); // JPQL, 반환 타입
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id); // id 로 Member 조회
        return Optional.ofNullable(member); // member 가 null 일 수도 있다는 것을 Optional 타입으로 한 번 감싸서 밖에 제공. java 8 기능
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult(); // single 이면 하나만 반환
    }

    // 단건 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 쿼리 메소드 기능 확인 전 순수 jpa 로 검색 조건 들어간 조회
    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
    // m.username 잘못 입력하면, 고객이 이거 누르는 순간 오류가 발생한다. 문법 오류인지 아닌지 파싱이 안 돼서 모른다.

    // JPA NamedQuery -> 구현하기 귀찮다!
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    // 순수 JPA 페이징과 정렬
    public List<Member> findByPage(int age, int offset, int limit) { // 나이로 자르고, 몇 번째부터 시작해서 몇 개 가져와라!
        // 나이가 10살이면서 이름으로 내림차순 페이징 쿼리
        return em.createQuery("select m from Member m where m.age = :age order By m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) // 어디서부터 가져올 건지 그냥 파라미터로 주면 된다.
                .setMaxResults(limit) // 몇 개 가져올지도
                .getResultList();
    }
    // 만약 DB 가 바뀌어도 문제 없다. JPA 는 페이징 쿼리에 대해 현재 DB 에 맞는 쿼리가 나간다.

    // 보통 페이징 쿼리를 짜면, 현재 내 페이지가 몇 번째 페이지인지. totalCount 를 갖고 온다.
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
        // sorting 컨디션이 들어갈 필요가 없다.
    }


    // 회원의 나이를 한 번에 변경하는 쿼리
    // 순수 JPA 를 사용한 벌크성 수정 쿼리
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }

}