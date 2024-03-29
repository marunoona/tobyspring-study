package springbook.user.dao;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의
 * 런타임 오브젝트 의존 관례를 설정하는 책임을 담당해야 한다.
 * <p>
 * UserDao가 사용할 ConnectionMaker 클래스를 선정하는 책임
 * 이후 이 역할을 Factory가 할 수 있도록 수정한다.
 */

//스프링의 테스트 컨텍스트 프레임워크의 Junit 확장 가능 지정
@RunWith(SpringJUnit4ClassRunner.class)
//테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트 위치 지정
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {
    //애플리케이션 컨텍스트가 갖고 잇는 빈을 DI 받는 것
    @Autowired
    private UserDao userDao;
    @Autowired
    private DataSource dataSource;

    private User user1;
    private User user2;
    private User user3;

    /**
     * 중복 코드를 넣을 메소드
     *
     * @Test가 실행되기 전에 먼저 실행되어야 하는 메소드를 정의
     */
    @Before
    public void setUp() {
        this.user1 = new User("gyumee", "박성철", "springno1");
        this.user2 = new User("leegw700", "이길원", "springno2");
        this.user3 = new User("bumjin", "박범진", "springno3");
    }

    @Test
    public void addAndGetTest() {
        userDao.deleteAll();
        assertThat(userDao.getCount(), is(0));

        userDao.addUser(user1);
        userDao.addUser(user2);
        assertThat(userDao.getCount(), is(2));

        User userget1 = userDao.getUser(user1.getId());
        assertThat(userget1.getName(), CoreMatchers.is(user1.getName()));
        assertThat(userget1.getPassword(), CoreMatchers.is(user1.getPassword()));

        User userget2 = userDao.getUser(user2.getId());
        assertThat(userget2.getName(), CoreMatchers.is(user2.getName()));
        assertThat(userget2.getPassword(), CoreMatchers.is(user2.getPassword()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {
        userDao.deleteAll();
        assertThat(userDao.getCount(), CoreMatchers.is(0));

        userDao.getUser("unknown_id");
    }

    @Test
    public void count() {
        userDao.deleteAll();
        assertThat(userDao.getCount(), CoreMatchers.is(0));

        userDao.addUser(user1);
        assertThat(userDao.getCount(), CoreMatchers.is(1));

        userDao.addUser(user2);
        assertThat(userDao.getCount(), CoreMatchers.is(2));

        userDao.addUser(user3);
        assertThat(userDao.getCount(), CoreMatchers.is(3));
    }

    @Test
    public void getAll() {
        userDao.deleteAll();

        List<User> users0 = userDao.getAllUsers();
        assertThat(users0.size(), CoreMatchers.is(0));

        userDao.addUser(user1);
        List<User> users1 = userDao.getAllUsers();
        assertThat(users1.size(), CoreMatchers.is(1));
        checkSameUser(user1, users1.get(0));

        userDao.addUser(user2);
        List<User> users2 = userDao.getAllUsers();
        assertThat(users2.size(), CoreMatchers.is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDao.addUser(user3);
        List<User> users3 = userDao.getAllUsers();
        assertThat(users3.size(), CoreMatchers.is(3));
        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), CoreMatchers.is(user2.getId()));
        assertThat(user1.getName(), CoreMatchers.is(user2.getName()));
        assertThat(user1.getPassword(), CoreMatchers.is(user2.getPassword()));
    }

    @Test(expected= DuplicateKeyException.class)
    public void duplciateKey() {
        userDao.deleteAll();

        userDao.addUser(user1);
        userDao.addUser(user1);
    }

    @Test
    public void sqlExceptionTranslate() {
        userDao.deleteAll();

        try {
            userDao.addUser(user1);
            userDao.addUser(user1);
        }
        catch(DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException)ex.getCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            DataAccessException transEx = set.translate(null, null, sqlEx);
            assertThat(transEx, CoreMatchers.is(DuplicateKeyException.class));
        }
    }
}
