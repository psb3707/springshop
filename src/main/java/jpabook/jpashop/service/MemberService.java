package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //모든 데이터 변경은 Transaction안에서 수행되어야 함!, 이때 readOnly = true이면 조회 성능이 최적화됨
@RequiredArgsConstructor //final 필드를 가지고 생성자를 만들어줌
public class MemberService {
    private final MemberRepository memberRepository; //Spring 빈이 컴포넌트로 등록된 memberRepository 의존성 주입

//    public MemberService(MemberRepository memberRepository) { // 생성자 인젝션 @Autowired 어노테이션을 자동으로 붙여줌
//        this.memberRepository = memberRepository;
//    }

    //회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증 이때 정말 동시에 같은 이름으로 가입 요청이 들어왔을 시를 대비해 회원 이름에 유니크 제약조건을 걸어주는 것을 추천
        memberRepository.save(member);
        return member.getId(); //아직 DB에 들어간 시점이 아니더라도 PK가 존재
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //회원 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
