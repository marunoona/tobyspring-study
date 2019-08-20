package springbook.user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationTargetException;

/**
 * 스프링의 어드바이스 인터페이스 구현
 */
public class TransactionAdvice implements MethodInterceptor {

    private PlatformTransactionManager transactionManager;  //트랜잭셕 기능을 제공

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 타깃을 호줄하는 기능을 가진 콜백 오브젝트르 프록시부터 받는다.
     * 덕분에 어드바이스는 특정 타깃에 의존하지 않고 재사용이 가능하다.
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            //콜백을 호출해서 타깃의 메소드를 실행한다.
            // 타깃 메소드 호출 전후로 필요한 부가기능을 넣을 수 있다.
            Object ret = methodInvocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        }catch (RuntimeException e){
            //스프링의 MethodInvocation을 통한 타깃 호출은 예외가 포장되지 않고 타깃에서 보낸
            //그대로 전달된다.
            //예외가 발생하면 트랜잭션을 롤백한다.
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
