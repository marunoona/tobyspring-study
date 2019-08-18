package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    MailSender mailSender;
    @Autowired
    PlatformTransactionManager transactionManager;

    List<User> users;    // test fixture

    @Before
    public void setUp() {
        //배열을 리스트로 만들어주는 메소드 이용
        //배열을 가변인자로 넣어주면 더욱 편리함
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", "aaa@naver.com"
                        , Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("joytouch", "강명성", "p2", "bbb@naver.com"
                        , Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", "ccc@naver.com"
                        , Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4", "ddd@naver.com"
                        , Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", "eee@naver.com"
                        , Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for (User user : users) userDao.addUser(user);

        //메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어
        //UserService의 의존 오브젝트로 주입해준다.
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        //업그레이드 테스트. 메일 발송이 일어나면
        //MockMailSender 오브젝트의 리스트에 그 결과가 저장된다.
        userService.upgradeLevels();
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        //목 오브젝트에 저장된 메일 수신자 목록을 가져와서
        //업그레이드 대상과 일치하는지 확인한다.
        List<String> request = mockMailSender.getRequest();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    @Test
    public void add() {
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
     *
     * @param user
     * @param expectedLevel
     */
    private void checkLevelUpgraded(User user, Level expectedLevel) {
        User userUpdate = userDao.getUser(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.getUser(user.getId());
        if (upgraded)    //업그레이드 발생했는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else    //업그레이드가 발생하지 않았는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(this.mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for (User user : users) userDao.addUser(user);

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), false);
    }


    static class TestUserService extends UserServiceImpl {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }

    static class MockUserDao implements UserDao {
        private List<User> users;   //레벨 업그레이드 후보 User 오브젝트 목록
        private List<User> updated = new ArrayList();   //업그레이드 대상 오브젝트를 저장해둘 목록

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        //스텁 기능 제공
        @Override
        public List<User> getAllUsers() {
            return this.users;
        }

        //목 오브젝트 기능 제공
        @Override
        public void updateUser(User user) {
            updated.add(user);
        }

        //사용되지 않는 메소드들
        @Override
        public void addUser(User user) { throw new UnsupportedOperationException(); }
        @Override
        public User getUser(String id) { throw new UnsupportedOperationException(); }
        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }
        @Override
        public int getCount() { throw new UnsupportedOperationException(); }
    }

    static class MockMailSender implements MailSender {

        //UserService로부터 전송 요청을 받은 메일 주소를
        //저장해두고 이를 읽을 수 있게 한다.
        private List<String> request = new ArrayList<>();

        public List<String> getRequest() {
            return request;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            //전송 요청을 받은 이메일 주소를 저장해준다. 간단하게 첫 번재 수신자의 메일 주소만 저장
            request.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

        }
    }
}
