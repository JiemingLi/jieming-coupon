package com.jieming.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.jieming.coupon.constant.Constant;
import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 * Created by Jieming.
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    private JavaMailSender javaMailSender;

    /** CouponTemplate Dao */
    @Autowired
    private  CouponTemplateDao templateDao;

    /** 注入 Redis 模板类 */
    @Autowired
    private  StringRedisTemplate redisTemplate;

    public AsyncServiceImpl(){}

    /**
     * <h2>根据模板异步的创建优惠券码，并且发送邮箱通知</h2>
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        // extc: imooc_coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());

        log.info("Push CouponCode To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        // 设置优惠券模板为可利用状态，也就是 优惠券模板创建成功并且优惠券码保存到redis中
        template.setAvailable(true);
        // 把优惠券模板同步到本地数据库
        templateDao.save(template);
        // 结束计时
        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        // TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
        sendMail("优惠券模板","创建成功");
    }

    // 发送邮件
    public void sendMail(String title,String content){
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo("18778331309@163.com");//收件人邮箱user.getMail()
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
     * <h2>构造优惠券码</h2>
     * 优惠券码(对应于每一张优惠券, 18位)
     *  前四位: 产品线 + 类型
     *  中间六位: 日期随机(190101)
     *  后八位: 0 ~ 9 随机数构成
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     * */
    @SuppressWarnings("all")
    public Set<String> buildCouponCode(CouponTemplate template) {

        // 因为生成优惠券码会比较慢，所以需要进行计时进行日志处理。
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());

        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();

        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    @SuppressWarnings("all")
    public Set<String> buildCouponCode(CouponTemplate template,Integer count) {
        Set<String> result = new HashSet<>(count);
        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());
        for (int i = 0; i != count; ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < count) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        return result;
    }

    @SuppressWarnings("all")
    public Set<String> addCouponCode(CouponTemplate template,Integer count) {
        Set<String> result = new HashSet<>(count);
        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());
        for (int i = 0; i != count; ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < count) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        return result;
    }

    /**
     * <h2>构造优惠券码的后 14 位</h2>
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     * */
    public String buildCouponCodeSuffix14(String date) {

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 中间六位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars); /* 洗牌算法*/
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());

        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
