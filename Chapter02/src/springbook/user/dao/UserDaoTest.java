package springbook.user.dao;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의
 * 런타임 오브젝트 의존 관례를 설정하는 책임을 담당해야 한다.
 * <p>
 * UserDao가 사용할 ConnectionMaker 클래스를 선정하는 책임
 * 이후 이 역할을 Factory가 할 수 있도록 수정한다.
 */
public class UserDaoTest {

    private UserDao userDao;

    private User user1;
    private User user2;
    private User user3;

    /**
     * 중복 코드를 넣을 메소드
     * @Test가 실행되기 전에 먼저 실행되어야 하는 메소드를 정의
     */
    @Before
    public void setUp(){
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        this.userDao = context.getBean("userDao", UserDao.class);

        this.user1 = new User("gyumee", "박성철", "springno1");
        this.user2 = new User("leegw700", "이길원", "springno2");
        this.user3 = new User("bumjin", "박범진", "springno3");
    }


    @Test
    public void addAndGetTest() throws SQLException {
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//        UserDao userDao = context.getBean("userDao", UserDao.class);
//
//        User user1 = new User("apple", "곽수아", "jejudo");
//        User user2 = new User("dlwldms", "이지은", "marado");

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

    @Test
    public void count() throws SQLException {
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//
//        UserDao userDao = context.getBean("userDao", UserDao.class);
//        User user1 = new User("gyumee", "박성철", "springno1");
//        User user2 = new User("leegw700", "이길원", "springno2");
//        User user3 = new User("bumjin", "박범진", "springno3");

        userDao.deleteAll();
        assertThat(userDao.getCount(), CoreMatchers.is(0));

        userDao.addUser(user1);
        assertThat(userDao.getCount(), CoreMatchers.is(1));

        userDao.addUser(user2);
        assertThat(userDao.getCount(), CoreMatchers.is(2));

        userDao.addUser(user3);
        assertThat(userDao.getCount(), CoreMatchers.is(3));
    }
}
