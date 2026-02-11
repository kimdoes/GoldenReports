package com.example.GoldenReport.Repository;

import com.example.GoldenReport.Domain.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member,String> {
}
