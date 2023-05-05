package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository0 {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member0 member) {
        em.persist(member);
        return member.getId();
    }
    // 왜 member 를 반환 안 하고 id 만 반환하지? command 랑 query 를 분리해라.
    // 저장하고 나면 side effect 를 일으키는 command 성이기 때문에 return 값을 거의 안 만듦.
    // 대신 id 정도 있으면 조회할 수 있으니까 !

    public Member0 find(Long id) {
        return em.find(Member0.class, id);
    }

}
