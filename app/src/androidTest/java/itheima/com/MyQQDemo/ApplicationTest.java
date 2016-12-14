package itheima.com.MyQQDemo;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.test.MyQQ.util.StringUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testUsername(){
        assertEquals(false, StringUtil.matchUsername("a23wqreqgteqgewgfdhdfhfdkjhgjlkjklkjlhjkfdsgfsafdasfdasdfasfdasdf"));
    }
    public void testPas(){
        assertEquals(false,StringUtil.matchPas("1"));
    }
    public void testGetFisrstC(){
        StringUtil.getFirstC("");
    }
}