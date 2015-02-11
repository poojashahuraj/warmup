import junit.framework.TestCase;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.util.Collection;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;

/**
 * Created by parallels on 2/9/15.
 */
public class mainTest {
    private Collection collection;

    @BeforeClass
    public static void oneTimeSetUp() {
        System.out.println("In BeforeClass");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        System.out.println("In AfterClass ");
    }

    @Before
    public void setUp() {
        System.out.println("Before - setUp");
    }

    @After
    public void tearDown() {
        System.out.println("After - tearDown");
    }

    @Test
    @Category(MainClass.class)
    public void doTest() {

        MainClass mainClass = new MainClass(3);
        //  Iterator i= (Iterator) mock(MainClass.class);

        TestCase.assertTrue(mainClass.put(1, "one"));
        TestCase.assertTrue(mainClass.put(2, "two"));
        TestCase.assertTrue(mainClass.put(3, "three"));
        TestCase.assertTrue(mainClass.put(4, "four"));

        int expectedSize = 3;
        int actualSize = mainClass.sizeMap();

        for (int it : mainClass.map.keySet()) {
            Assert.assertNotNull(mainClass.map.get(it));
            System.out.println("Key:"+it+" Value:"+mainClass.get(it));
        }
        TestCase.assertTrue(mainClass.put(1, "one"));
        assertEquals(expectedSize, actualSize);
        assertSame("one", mainClass.get(1));
        boolean addSuccess = mainClass.put(1, "one");
        assertTrue(addSuccess);
    }
}
