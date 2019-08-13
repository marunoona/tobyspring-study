package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * 팩토리의 역할
 * 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것
 * 단지 오브젝트를 생성하는 쪽과 생성된 오브젝트를 사용하는 쪽의
 * 역할과 책임을 깔끔하게 분리하려는 목적으로 사용하는 것
 * <p>
 * 과심을 분리하고 책임으르 나누고 유연하게 확장 가능한 구조로
 * 만들기 위해 DaoFactory를 도입했던 과정이 바로 IoC를 적용하는 작업이었다.
 * <p>
 * IoC를 적용함으로써 설계가 깔끔해지고 유여성이 증가하며 확장성이 좋아진다.
 * <p>
 * <p>
 * 이 클래스를 @Configuration이용해 빈 팩토리로 만들것이다.
 * 빈 팩토리: 스프링 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트를 일컫음
 * 보통 빈 팩토리보다 이를 좀 더 확장한 애플리케이션 컨텍스트를 사용함
 * <p>
 * 오브젝트를 만들어주는 메소드에는 @Bean을 붙여서 빈으로 만들것이다.
 */


@Configuration
public class DaoFactory {

    /**
     * 자바에서 DB커넥션을 가져오는 오브젝트의 기능을 추상화해서
     * 비슷한 용도로 사용할 수 있게 만들어진 DataSource 이용
     *
     * @return
     */
    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC");
        dataSource.setUsername("marunoona");
        dataSource.setPassword("maru1");

        return dataSource;
    }

    @Bean
    public UserDao userDao() {
        //ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao userDao = new UserDao();
        //수정자 메소드 DI를 사용
        userDao.setDataSource(dataSource());
        return userDao;
    }
}
