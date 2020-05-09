package com.jieming.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.constant.Constant;
import com.jieming.coupon.constant.CouponStatus;
import com.jieming.coupon.dao.CouponDao;
import com.jieming.coupon.entity.Coupon;
import com.jieming.coupon.service.IKafkaService;
import com.jieming.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

/**
 * <h1>Kafka 相关的服务接口实现</h1>
 * 核心思想: 是将 Cache 中的 Coupon 的状态变化同步到 DB 中
 * Created by Jiemig.
 */
@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

    @Autowired
    private JavaMailSender javaMailSender;

    /** Coupon Dao */
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * <h2>消费优惠券 Kafka 消息</h2>
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    processUsableCoupons(couponInfo,status);
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }
    }


    private void processUsableCoupons(CouponKafkaMessage kafkaMessage,CouponStatus status){
        processCouponsByStatus(kafkaMessage,status);
    }
    /**
     * <h2>处理已使用的用户优惠券</h2>
     * */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status) {
        processCouponsByStatus(kafkaMessage, status);
    }

    /**
     * <h2>处理过期的用户优惠券</h2>
     * */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage,
                                       CouponStatus status) {
        // TODO 给用户发送推送
        processCouponsByStatus(kafkaMessage, status);
        String content = "你有" + kafkaMessage.getIds().size() + "张优惠券已经过期";
        sendMail("优惠券过期",content);
    }

    // 发送邮件
    public void sendMail(String title,String content){
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo("18778331309@163.com");//收件人邮箱
            mimeMessageHelper.setFrom("1207734468@qq.com");//发件人邮箱
            mimeMessage.setSubject(title);
            mimeMessageHelper.setText(content,true);
            javaMailSender.send(mimeMessage);
            System.err.println("发送成功");
        }catch (Exception e){
            System.err.println("发送失败");
        }
    }

    /**
     * <h2>根据状态处理优惠券信息</h2>
     * */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage,
                                        CouponStatus status) {

        List<Coupon> coupons = couponDao.findAllById(
                kafkaMessage.getIds()
        );
        if (CollectionUtils.isEmpty(coupons)
                || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}",
                    JSON.toJSONString(kafkaMessage));
            return;
        }

        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }
}
