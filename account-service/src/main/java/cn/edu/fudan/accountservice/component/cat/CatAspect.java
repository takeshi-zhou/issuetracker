package cn.edu.fudan.accountservice.component.cat;

import com.dianping.cat.Cat;

import com.dianping.cat.message.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;



@Aspect
public class CatAspect {

    @Around("@annotation(catTransaction)")
    public Object catTransactionProcess(ProceedingJoinPoint pjp, CatTransaction catTransaction) throws Throwable {
        String transName = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
        if (transName!=null && transName.length()>0) {
            transName = catTransaction.name();
        }
        Transaction t = Cat.newTransaction(catTransaction.type(), transName);
        try {
            Object result = pjp.proceed();
            t.setStatus(Transaction.SUCCESS);
            return result;
        } catch (Throwable e) {
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @After("@annotation(catHttpRequestTransaction)")
    public void catHttpRequestProcess(CatHttpRequestTransaction catHttpRequestTransaction) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(catHttpRequestTransaction.name()!= null && catHttpRequestTransaction.name().length()>0){
            String transName = catHttpRequestTransaction.name();
            request.setAttribute("cat-page-uri", transName);
        }
    }
}