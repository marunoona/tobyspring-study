package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private PlatformTransactionManager transactionManager;
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }



    public void upgradeLevels() {
        //DI받은 트랜잭션 매니저를 공유해서 사용한다.
        //멀티스레드 환경에서도 안전
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<User> users = userDao.getAllUsers();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    /**
     * 레벨 업그레이드 가능한지 확인하는 메소드
     *
     * @param user
     * @return
     */
    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            //레벨별로 구분해서 조건을 판단
            case BASIC:
                return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:
                return false;
            //현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킴
            default:
                throw new IllegalArgumentException("Unknown level: " + currentLevel);
        }
    }

    /**
     * 레벨 업그레이드 작업 메소드
     *
     * @param user
     */
    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.updateUser(user);
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);
        userDao.addUser(user);
    }
}
