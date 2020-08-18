package com.ljh.distrilock.service;

import com.ljh.distrilock.entity.PhoneInfo;

public interface PhoneService {
    public PhoneInfo findSpecsByPhoneId(Integer phoneId);


}