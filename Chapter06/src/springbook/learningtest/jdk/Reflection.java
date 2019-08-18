package springbook.learningtest.jdk;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Reflection {
    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length
        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name), is(6));

        // toUpperCase
        assertThat(name.charAt(0), is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name, 0), is('S'));
    }

    @Test
    public void createObject() throws Exception {
        Date now = (Date) Class.forName("java.util.Date").newInstance();
    }
}
