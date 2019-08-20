package springbook.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicProxyTest {
    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

        //프록시를 통해 타깃 오브젝트에 접근하도록 구성
        //Hello proxiedHello = new HelloUppercase(new HelloTarget());

        //다이나믹 프록시로 수정
        //생성된 다이나믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로
        //Hello 타입으로 캐스팅해도 안전하다
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),    //동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{Hello.class},     //구현할 인터페이스
                new UppercaseHandler(new HelloTarget()));   //부가기능과 위임 코드를 담은 InvocationHandler

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));

    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        //타켓 설정
        pfBean.setTarget(new HelloTarget());
        //부가기능을 담은 어드바이스 추가
        //여러개 추가 가능
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean이므로 getObject()로 생성된 프록시를 가져옴

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        //타켓 설정
        pfBean.setTarget(new HelloTarget());

        //메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        //이름 비교조건 설정
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject(); //FactoryBean이므로 getObject()로 생성된 프록시를 가져옴

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        //메소드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가기능이 적용되지 않음
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            //리플렉션의 Method와 달리 메소드 실행 시 타겟 오브젝트를 전달할 필요가 없다.
            //MethodInvocation은 메소드 정보와 함께 타겟 오브젝트를 알고있기 때문이다.
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }

    //이렇게 만들면 인터페이스의 모든 메소드를 구현해 위임하도록 해야하며
    //부가기능인 리턴값을 대문자로 바꾸는 기능이 모든 메소드에 중복되어 나타난다.
    static class HelloUppercase implements Hello {
        Hello hello;

        public HelloUppercase(Hello hello) {
            this.hello = hello;
        }

        public String sayHello(String name) {
            return hello.sayHello(name).toUpperCase();
        }

        public String sayHi(String name) {
            return hello.sayHi(name).toUpperCase();
        }

        public String sayThankYou(String name) {
            return hello.sayThankYou(name).toUpperCase();
        }

    }

    static class UppercaseHandler implements InvocationHandler {
        Object target;

        //어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 Object 타입으로 수정
        private UppercaseHandler(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            //타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다.
            Object ret = method.invoke(target, args);
            //리턴타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용한다.
            if (ret instanceof String && method.getName().startsWith("say")) {
                return ((String) ret).toUpperCase();
            } else {
                return ret;
            }
        }
    }

    static interface Hello {
        String sayHello(String name);

        String sayHi(String name);

        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {
        public String sayHello(String name) {
            return "Hello " + name;
        }

        public String sayHi(String name) {
            return "Hi " + name;
        }

        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }
}
