package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 repository;

    public void accountTransfer(String fromId, String toId, int payment) {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(fromId, fromMember.getMoney() - payment);
        validation(toMember);
        repository.update(toId, fromMember.getMoney() + payment);
    }

    private void validation(Member toMember) {
        if ( toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
