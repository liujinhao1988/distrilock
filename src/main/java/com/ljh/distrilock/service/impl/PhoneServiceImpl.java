package com.ljh.distrilock.service.impl;

import com.alibaba.fastjson.JSON;
import com.ljh.distrilock.entity.PhoneInfo;
import com.ljh.distrilock.repository.PhoneInfoRepository;
import com.ljh.distrilock.service.PhoneService;
import com.ljh.distrilock.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class PhoneServiceImpl implements PhoneService {


    @Autowired
    RedisUtil redisUtil;
    @Autowired
    PhoneInfoRepository phoneInfoRepository;

    @Autowired
    RedissonClient redissonClient;






    @Override
    public PhoneInfo findSpecsByPhoneId(Integer phoneId) {
        PhoneInfo phoneInfo=new PhoneInfo();
        Jedis jedis = redisUtil.getJedis();
        String phoneKey = "sku:" + phoneId + ":info";
        String phoneLock="sku:" + phoneId + ":lock";
        String phoneInfoJson = jedis.get(phoneKey);
        if (StringUtils.isNotBlank(phoneInfoJson)) {
            System.out.println(phoneId+"redis");

            phoneInfo = JSON.parseObject(phoneInfoJson, PhoneInfo.class);
            return phoneInfo;

        }else{
            //如果缓存中没有 查询mysql
            //设置分布式锁
            RLock lock = redissonClient.getLock(phoneLock);


            try{
                // 1. 最常见的使用方法
                //lock.lock();
                // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
                //lock.lock(10, TimeUnit.SECONDS);
                // 3. 尝试加锁，最多等待2秒，上锁以后8秒自动解锁
                boolean res = lock.tryLock(2, 8, TimeUnit.SECONDS);

                if (res) { //成功
                    //处理业务

                    phoneInfo= phoneInfoRepository.findByPhoneId(phoneId);
                    if (phoneInfo != null) {
                        //mysql查询结果存入redis
                        jedis.set("sku:" + phoneId + ":info", JSON.toJSONString(phoneInfo));
                    } else {
                        //数据库中不存在该sku
                        //为了防止缓存穿透，将null或者空字符串设置给redis
                        jedis.setex("sku:" + phoneId + ":info", 60 * 3, JSON.toJSONString(""));//三分钟不会有数据打到数据库上
                    }

                }else {
                    //设置失败 自旋（该线程在睡眠几秒后，重新尝试访问）
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return findSpecsByPhoneId(phoneId);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //释放锁
                lock.unlock();
            }








            System.out.println(phoneId+"DB");

            return phoneInfo;



        }





    }




}