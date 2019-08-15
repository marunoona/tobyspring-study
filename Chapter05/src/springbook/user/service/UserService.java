package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            if (userLevelUpgradePolicy.canUpgradeLevel(user))
                userLevelUpgradePolicy.upgradeLevel(user);
        }
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);
        userDao.addUser(user);
    }
}
