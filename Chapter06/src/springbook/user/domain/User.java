package springbook.user.domain;

public class User {

    private String id;
    private String name;
    private String password;
    private String email;
    private Level level;
    private int login;
    private int recommend;

    public User() {
    }

    public User(String id, String name, String password, String email, Level level,
                int login, int recommend) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.level = level;
        this.login = login;
        this.recommend = recommend;
    }

    /**
     * User의 내부 정보가 변경되는 것을
     * User 스스로 다루도록 하기 위한 메소드
     * UserService가 일일이 레벨 업그레이드 시에 User의 어떤 필드를 수정하는
     * 로직을 갖고있디 보다는 User에게 레벨 업그레이드를 해야하니
     * 정보를 변경하라고 요청하는 편이 낫기 때문이다.
     */
    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if (nextLevel == null)
            throw new IllegalArgumentException(this.level + "은 업그레이드 불가능");
        else
            this.level = nextLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
}
