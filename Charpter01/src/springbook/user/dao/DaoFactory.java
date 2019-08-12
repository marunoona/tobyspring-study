package springbook.user.dao;

/**
 * 팩토리의 역할
 * 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것
 * 단지 오브젝트를 생성하는 쪽과 생성된 오브젝트를 사용하는 쪽의
 * 역할과 책임을 깔끔하게 분리하려는 목적으로 사용하는 것
 */
public class DaoFactory {

    public UserDao userDao(){
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        return userDao;
    }
}
