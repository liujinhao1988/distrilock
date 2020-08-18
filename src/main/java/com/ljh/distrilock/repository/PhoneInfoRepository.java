package com.ljh.distrilock.repository;

import com.ljh.distrilock.entity.PhoneInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneInfoRepository extends JpaRepository<PhoneInfo,Integer> {


    PhoneInfo findByPhoneId(Integer phoneId);
}