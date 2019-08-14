package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    //private DataSource dataSource;

    //JdbcContext 사용 -> 이후 스프링이 제공하는 JDBC Template 으로 변경
    //private JdbcContext jdbcContext;

    //JdbcContext를 DI 받도록 만듬
    /*public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }*/

    private JdbcTemplate jdbcTemplate;

    // 수정자를 이용한 의존관계 주입 - datasource 사용
    // 수정자 메소드이면서 JdbcContext에 대한 생성, DI 작업을 동시에 수행
    public void setDataSource(DataSource dataSource) {
        //this.jdbcContext = new JdbcContext();   //JdbcContext 생성(IoC)
        //this.jdbcContext.setDataSource(dataSource);     //의존 오브젝트 주입(DI)
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        //this.dataSource = dataSource;   //아직 JdbcContext를 적용하지 않은 메소드를 위해 저장
    }

    private RowMapper<User> rowMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(resultSet.getString("id"));
                    user.setName(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("password"));
                    return user;
                }
            };

    public void addUser(final User user) throws SQLException {
//        this.jdbcContext.workWithSatementStrategy(
//                new StatementStrategy() {
//                    @Override
//                    public PreparedStatement makePreparedStatement(Connection c) throws
//                    SQLException {
//                        PreparedStatement ps = c.prepareStatement(
//                                "insert into users(id, name, password) values(?,?,?)");
//                        ps.setString(1, user.getId());
//                        ps.setString(2, user.getName());
//                        ps.setString(3, user.getPassword());
//
//                        return ps;
//                    }
//                }
//        );

        //JdbcTemplate 사용
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User getUser(String id) throws SQLException {
        //Connection connection = this.connectionMaker.makeConnection();
//        Connection connection = this.dataSource.getConnection();
//
//        PreparedStatement ps = connection.prepareStatement(
//                "select * from users where id = ?");
//        ps.setString(1, id);
//
//        ResultSet rs = ps.executeQuery();
//        rs.next();
//        User user = new User();
//        user.setId(rs.getString("id"));
//        user.setName(rs.getString("name"));
//        user.setPassword(rs.getString("password"));
//
//        rs.close();
//        ps.close();
//        connection.close();
//
//        return user;
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id}, this.rowMapper);
    }

    public void deleteAll() throws SQLException {
        /*this.jdbcContext.workWithSatementStrategy(
                //익명 내부 클래스를 적용
                //변하지 않는 콜백 클래스 정의와 오브젝트 생성할 부분
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws
                    SQLException {
                        return c.prepareStatement("delete from users");
                    }
                }
        );*/
        //변하지 않는 부분을 분리시킴
        //this.jdbcContext.executeSql("delete from users");

        //JdbcTemplate 사용
        //내장 콜백을 사용하는 update()로 변경
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        //JdbcTemplate 사용
        return this.jdbcTemplate.queryForInt("select count(*) from users");
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.rowMapper);
    }
}
