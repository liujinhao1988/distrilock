package com.ljh.distrilock.controller;

import com.ljh.distrilock.entity.PhoneInfo;
import com.ljh.distrilock.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/phone")
public class PhoneController {
    @Autowired
    PhoneService phoneService;







    @GetMapping("/findSpecsByPhoneId/{phoneId}")
    public PhoneInfo findSpecsByPhoneId(@PathVariable("phoneId") Integer phoneId){
        PhoneInfo specsByPhoneId = phoneService.findSpecsByPhoneId(phoneId);

        return specsByPhoneId;
    }
}