package springbook.user.dao;

import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private DataSource dataSource;

    // 수정자를 이용한 의존관계 주입 - datasource 사용
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //jdbc try/catch/finally 컨텍스를 메소드로 분리
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws
            SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) {} }
            if (c != null) { try {c.close(); } catch (SQLException e) {} }
        }
    }

    public void addUser(User user) throws SQLException {
        //Connection connection = this.connectionMaker.makeConnection();
//        Connection connection = null;
//        PreparedStatement ps = null;
//        try {
//            connection = this.dataSource.getConnection();
//
//            //변하는 부분
//            ps = connection.prepareStatement(
//                    "insert into users(id, name, password) values(?,?,?)");
//            ps.setString(1, user.getId());
//            ps.setString(2, user.getName());
//            ps.setString(3, user.getPassword());
//
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        //전략 패턴 사용으로 변경
        StatementStrategy stmt = new AddStatement(user);
        jdbcContextWithStatementStrategy(stmt);
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
        /*StatementStrategy stmt = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(stmt);*/

        //익명 내부 클래스를 적용
        jdbcContextWithStatementStrategy(
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
