package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.domain.User;

public class UserServiceTx implements UserService {
    //UserService를 구현한 다른 오브젝트를 DI 받는다.
    UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    //DI받은 UserService 오브젝트에 모든 기능을 위임한다.
    @Override
    public void add(User user) {
        //트랜잭션 적용
        TransactionStatus status = this.transactionManager
                .getTransaction(new DefaultTransactionDefinition());
        try {
            userService.add(user);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public void upgradeLevels() {
        userService.upgradeLevels();
    }
}
