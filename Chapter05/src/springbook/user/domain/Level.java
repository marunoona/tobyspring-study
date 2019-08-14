package springbook.user.domain;

public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    /**
     * 값을 가져오는 메소드
     * @return
     */
    public int intValue(){
        return value;
    }

    /**
     * 값으로부터 Level 타입 오브젝트를 가져오도록
     * 만든 스태틱 메소드
     * @param value
     * @return
     */
    public static Level valueOf(int value) {
        switch (value) {
            case 1:
                return BASIC;
            case 2:
                return SILVER;
            case 3:
                return GOLD;
            default:
                throw new AssertionError("Unknown value: " + value);
        }
    }
}
