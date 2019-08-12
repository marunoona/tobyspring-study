package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.SQLException;

/**
 * UserDaoTest는 UserDao와 ConnectionMaker 구현 클래스와의
 * 런타임 오브젝트 의존 관례를 설정하는 책임을 담당해야 한다.
 *
 * UserDao가 사용할 ConnectionMaker 클래스를 선정하는 책임
 * 이후 이 역할을 Factory가 할 수 있도록 수정한다.
 */
public class UserDaoTest {

    public static void main(String[] args) throws ClassNotFoundException,
            SQLException {

        // 1. UserDao가 사용할 ConnectionMaker 구현 클래스를 결정하고 오브젝트를 만든다.
        //ConnectionMaker connectionMaker = new DConnectionMaker();

        // 2.
        // 2-1. UserDao 생성, 2-1. 사용할 ConnectionMaker 타입의 오브젝트 제공.
        // 결국 두 오브젝트 사이의 의존관계 설정 효과
        //UserDao userDao = new UserDao(connectionMaker);

        //3. 팩토리를 사용하도록 수정
        UserDao userDao = new DaoFactory().userDao();

        User user = new User();
        user.setId("apple1");
        user.setName("백설공주");
        user.setPassword("a123");

        userDao.addUser(user);
        System.out.println(user.getId() +" 등록되었습니다.");

        User user2 = userDao.getUser(user.getId());
        System.out.println(user.getName());
        System.out.println(user.getPassword());

        System.out.println(user.getId() +" 정보 가져왔다.");
    }
}
