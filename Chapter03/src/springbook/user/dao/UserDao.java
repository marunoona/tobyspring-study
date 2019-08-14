package springbook.user.dao;

import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private DataSource dataSource;

    //JdbcContext 사용
    private JdbcContext jdbcContext;

    //JdbcContext를 DI 받도록 만듬
    /*public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }*/

    // 수정자를 이용한 의존관계 주입 - datasource 사용
    // 수정자 메소드이면서 JdbcContext에 대한 생성, DI 작업을 동시에 수행
    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();   //JdbcContext 생성(IoC)
        this.jdbcContext.setDataSource(dataSource);     //의존 오브젝트 주입(DI)
        this.dataSource = dataSource;   //아직 JdbcContext를 적용하지 않은 메소드를 위해 저장
    }

    public void addUser(User user) throws SQLException {
        this.jdbcContext.workWithSatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement(
                                "insert into users(id, name, password) values(?,?,?)");
                        ps.setString(1, user.getId());
                        ps.setString(2, user.getName());
                        ps.setString(3, user.getPassword());

                        return ps;
                    }
                }
        );
    }

    public User getUser(String id) throws SQLException {
        //Connection connection = this.connectionMaker.makeConnection();
        Connection connection = this.dataSource.getConnection();

        PreparedStatement ps = connection.prepareStatement(
                "select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        connection.close();

        return user;
    }

    public void deleteAll() throws SQLException{
         //익명 내부 클래스를 적용
        this.jdbcContext.workWithSatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        return c.prepareStatement("delete from users");
                    }
                }
        );
    }

    public int getCount() throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select count(*) from users");

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();

        return count;
    }
}
