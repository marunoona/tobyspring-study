package springbook.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
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
        userDao.updateUser(user);   //수정된 사용자 정보를 DB에 반영하다.
        sendUpgradedEmail(user);
    }

    private void sendUpgradedEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("admin@marunoona.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급 : " + user.getLevel().name());
        this.mailSender.send(mailMessage);
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);
        userDao.addUser(user);
    }
}
