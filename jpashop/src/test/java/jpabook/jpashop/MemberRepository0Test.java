package jpabook.jpashop;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepository0Test {

    @Autowired
    MemberRepository0 memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception {
        // given
        Member0 member = new Member0();
        member.setUsername("memberAA");

        // when
        Long saveId = memberRepository.save(member);
        Member0 findMember = memberRepository.find(saveId);

        // then
        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
        Assertions.assertEquals(findMember, member); // 저장한 거랑 조회한 거랑 같을까 다를까? true !!
        // 같은 영속성 컨텍스트 안에서 id 값이 같으면 같은 엔티티로 식별한다.

        // entityManager 를 통한 모든 데이터 변경은 항상 transaction 안에서 이루어져야 함.

        // test 는 test 후에 DB rollback 을 해버림. 그래서 DB 결과 보면 제대로 안 보인다. -> @Rollback(false)

        // test 오류 났던 거 ID annotation 때문임. application run failed 에 영향을 끼치는 요소 중 하나임을 기억하기.
    }

}