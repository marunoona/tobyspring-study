package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;

    // 수정자를 이용한 의존관계 주입 - datasource 사용
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> rowMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(resultSet.getString("id"));
                    user.setName(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("password"));
                    user.setLevel(Level.valueOf(resultSet.getInt("level")));
                    user.setLogin(resultSet.getInt("login"));
                    user.setRecommend(resultSet.getInt("recommend"));
                    return user;
                }
            };

    public void addUser(final User user) {
        //JdbcTemplate 사용
        this.jdbcTemplate.update(
                "insert into users(id, name, password, level, login, recommend) " +
                        "values(?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend()
        );
    }

    public User getUser(String id) {
        //JdbcTemplate 사용
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id}, this.rowMapper);
    }

    public List<User> getAllUsers() {
        //JdbcTemplate 사용
        return this.jdbcTemplate.query("select * from users order by id", this.rowMapper);
    }

    public void deleteAll() {
        //JdbcTemplate 사용
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        //JdbcTemplate 사용
        return this.jdbcTemplate.queryForInt("select count(*) from users");
    }

    @Override
    public void updateUser(User user) {
        this.jdbcTemplate.update(
                "update users set name = ?, password = ?, level = ?, login = ?," +
                        "recommend = ? where id = ?", user.getName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
    }
}
