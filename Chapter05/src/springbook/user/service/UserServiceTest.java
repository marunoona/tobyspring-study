package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    List<User> users;    // test fixture

    @Before
    public void setUp() {
        //배열을 리스트로 만들어주는 메소드 이용
        //배열을 가변인자로 넣어주면 더욱 편리함
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for(User user : users)
            userDao.addUser(user);
        userService.upgradeLevels();

        /*checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);*/

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);  //레벨이 이미 지정된 user->레벨 초기화 X
        //레벨이 비어있는 사용자->로직에 따라 등록 중에 BASIC 레벨로 설정되어야 한다.
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.getUser(userWithLevel.getId());
        User userWithoutLevelRead = userDao.getUser(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    /**
     * DB에서 사용자 정보를 가져와 레벨을 확인하는 코드
     * @param user
     * @param expectedLevel
     */
    private void checkLevel(User user, Level expectedLevel){
        User userUpdate = userDao.getUser(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    private void checkLevel(User user, boolean upgraded){
        User userUpdate = userDao.getUser(user.getId());
        if(upgraded)    //업그레이드 발생했는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else    //업그레이드가 발생하지 않았는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }
}
