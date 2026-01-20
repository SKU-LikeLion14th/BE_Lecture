package com.example.demo.repository;

import com.example.demo.domain.Member;

import java.util.List;

public interface MemberRepository {
    void save(Member member);
    Member findOne(Long id);
    List<Member> findAll();
    void remove(Long id);
    Member findByUsername(String username);
}
