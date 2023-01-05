package com.jsh.erp.annotation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author: origindoris
 * @Title: PageConversionAspect
 * @Description:
 * @date: 2022/12/28 15:18
 */
@Aspect
@Component
public class PageConversionAspect {
    @Pointcut("@annotation(com.jsh.erp.annotation.PageConversion)")
    public void methodPointcut() {
    }


    @Around("methodPointcut()")
    public Object enhance(ProceedingJoinPoint call) throws Throwable  {

        Object proceed = call.proceed();
        if (!(proceed instanceof Page)) {
            return proceed;
        }
        Page page = (Page) proceed;
        ErpPage<Object> erpPage = new ErpPage<>();
        BeanUtils.copyProperties(page, erpPage);
        erpPage.setRows(page.getRecords());
        return erpPage;
    }
}
