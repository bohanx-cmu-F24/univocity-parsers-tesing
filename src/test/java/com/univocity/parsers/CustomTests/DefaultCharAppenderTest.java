import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCharAppenderTest {

    private DefaultCharAppender appender;

    @BeforeEach
    void setUp() {
        appender = new DefaultCharAppender(10, "", 0);
    }

    @Test
    void testAppendIgnoringPadding() {
        appender.appendIgnoringPadding('a', ' ');
        assertEquals(1, appender.length());
        appender.appendIgnoringPadding(' ', ' ');
        assertEquals(2, appender.length());
    }

    @Test
    void testAppendIgnoringWhitespaceAndPadding() {
        appender.appendIgnoringWhitespaceAndPadding('a', ' ');
        appender.appendIgnoringWhitespaceAndPadding(' ', ' ');
        appender.appendIgnoringWhitespaceAndPadding('\t', ' ');
        assertEquals(3, appender.length());
    }

    @Test
    void testAppendIgnoringWhitespace() {
        appender.appendIgnoringWhitespace('a');
        appender.appendIgnoringWhitespace(' ');
        assertEquals(2, appender.length());
    }

    @Test
    void testIndexOf() {
        appender.append('a');
        appender.append('b');
        appender.append('a');
        assertEquals(0, appender.indexOf('a', 0));
        assertEquals(2, appender.indexOf('a', 1));
        assertEquals(-1, appender.indexOf('c', 0));
    }

    @Test
    void testIndexOfAny() {
        appender.append('a');
        appender.append('b');
        appender.append('c');
        char[] chars = {'c', 'd'};
        assertEquals(2, appender.indexOfAny(chars, 0));
    }

    @Test
    void testSubstring() {
        appender.append('a');
        appender.append('b');
        appender.append('c');
        assertEquals("ab", appender.substring(0, 2));
    }

    @Test
    void testRemove() {
        appender.append('a');
        appender.append('b');
        appender.append('c');
        appender.remove(1, 1);
        assertEquals("ac", appender.toString());
    }

    @Test
    void testAppendChar() {
        appender.append('a');
        assertEquals("a", appender.toString());
    }

    @Test
    void testAppendObject() {
        appender.append("Hello");
        assertEquals("Hello", appender.toString());
    }

    @Test
    void testAppendInt() {
        appender.append(65); // 'A'
        assertEquals("A", appender.toString());
    }

    @Test
    void testAppendIntArray() {
        int[] chars = {65, 66}; // 'A' and 'B'
        appender.append(chars);
        assertEquals("AB", appender.toString());
    }

    @Test
    void testGetAndReset() {
        appender.append('a');
        String result = appender.getAndReset();
        assertEquals("a", result);
        assertEquals(0, appender.length());
    }

    @Test
    void testToString() {
        appender.append('a');
        assertEquals("a", appender.toString());
    }

    @Test
    void testLength() {
        appender.append('a');
        appender.append('b');
        assertEquals(2, appender.length());
    }

    @Test
    void testGetCharsAndReset() {
        appender.append('a');
        appender.append('b');
        char[] result = appender.getCharsAndReset();
        assertArrayEquals(new char[]{'a', 'b'}, result);
        assertEquals(0, appender.length());
    }

    @Test
    void testWhitespaceCount() {
        appender.appendIgnoringPadding(' ', ' ');
        appender.appendIgnoringPadding('a', ' ');
        assertEquals(1, appender.whitespaceCount());
    }

    @Test
    void testReset() {
        appender.append('a');
        appender.reset();
        assertEquals(0, appender.length());
    }

    @Test
    void testAppendAppender() {
        DefaultCharAppender anotherAppender = new DefaultCharAppender(10, "", 0);
        anotherAppender.append('a');
        anotherAppender.append('b');
        appender.append(anotherAppender);
        assertEquals("ab", appender.toString());
        assertEquals(0, anotherAppender.length()); // Ensure `anotherAppender` is reset
    }

    @Test
    void testResetWhitespaceCount() {
        appender.appendIgnoringWhitespace(' ');
        appender.resetWhitespaceCount();
        assertEquals(0, appender.whitespaceCount());
    }

    @Test
    void testGetChars() {
        appender.append('a');
        appender.append('b');
        char[] chars = appender.getChars();
        assertEquals('a', chars[0]);
        assertEquals('b', chars[1]);
    }

    @Test
    void testFill() {
        appender.fill('x', 3);
        assertEquals("xxx", appender.toString());
    }

    @Test
    void testPrependChar() {
        appender.append('b');
        appender.prepend('a');
        assertEquals("ab", appender.toString());
    }

    @Test
    void testPrependTwoChars() {
        appender.append('c');
        appender.prepend('a', 'b');
        assertEquals("abc", appender.toString());
    }

    @Test
    void testPrependCharArray() {
        appender.append('d');
        char[] chars = {'a', 'b', 'c'};
        appender.prepend(chars);
        assertEquals("abcd", appender.toString());
    }

    @Test
    void testUpdateWhitespace() {
        appender.append('a');
        appender.append(' ');
        appender.updateWhitespace();
        assertEquals(1, appender.whitespaceCount());
    }

    @Test
    void testAppendUntilSingleStopChar() {
        CharInput input = new CharInput("abc!"); // Custom CharInput mock class
        char result = appender.appendUntil('a', input, '!');
        assertEquals("abc", appender.toString());
        assertEquals('!', result);
    }

    @Test
    void testAppendUntilTwoStopChars() {
        CharInput input = new CharInput("abc#"); // Custom CharInput mock class
        char result = appender.appendUntil('a', input, '!', '#');
        assertEquals("abc", appender.toString());
        assertEquals('#', result);
    }

    @Test
    void testAppendUntilThreeStopChars() {
        CharInput input = new CharInput("abc$"); // Custom CharInput mock class
        char result = appender.appendUntil('a', input, '!', '#', '$');
        assertEquals("abc", appender.toString());
        assertEquals('$', result);
    }

    @Test
    void testAppendCharArrayWithRange() {
        char[] chars = {'x', 'y', 'z'};
        appender.append(chars, 1, 2);
        assertEquals("yz", appender.toString());
    }

    @Test
    void testAppendFullCharArray() {
        char[] chars = {'x', 'y', 'z'};
        appender.append(chars);
        assertEquals("xyz", appender.toString());
    }

    @Test
    void testAppendStringWithRange() {
        String str = "hello";
        appender.append(str, 1, 4);
        assertEquals("ell", appender.toString());
    }

    @Test
    void testAppendFullString() {
        appender.append("hello");
        assertEquals("hello", appender.toString());
    }

    @Test
    void testCharAt() {
        appender.append("hello");
        assertEquals('e', appender.charAt(1));
    }

    @Test
    void testSubSequence() {
        appender.append("hello");
        assertEquals("ell", appender.subSequence(1, 4));
    }

    @Test
    void testIgnore() {
        appender.ignore(3);
        assertEquals(3, appender.whitespaceCount());
    }

    @Test
    void testDelete() {
        appender.append("hello");
        appender.delete(3);
        assertEquals("he", appender.toString());
        appender.delete(5); // Test deleting more than length
        assertEquals("", appender.toString());
    }
}
