package springbook.learningtest.spring.factorybean;

public class Message {
    String text;
    //셍성자를 private으로 선언하여 외부에서 생성자를 통해 오브젝트 생성 불가능
    private Message(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }

    //생성자 대신 사용할 수 있는 스태틱 팩토리 메소드 제공
    public static Message newMessage(String text){
        return new Message(text);
    }
}
