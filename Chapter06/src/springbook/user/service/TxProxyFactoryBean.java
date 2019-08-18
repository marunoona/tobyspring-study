package springbook.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {
    //아래 세개 변수는 TransactionHandler 생성핳 때 필요
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;
    //다이나믹 프록시를 생성할 때 필요하다.
    // UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다
    Class<?> serviceInterface;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);
        return Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler
        );
    }

    //팩토리 빈이 생성하는 오브젝트 타입은 DI받은 인터페이스 타입에 따라 달라진다.
    //따라서 다양한 타입의 프록시 오브젝트 생성에 재사용할 수 있다.
    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
