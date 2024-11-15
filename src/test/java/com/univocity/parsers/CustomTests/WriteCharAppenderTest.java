
package com.univocity.parsers.CustomTests;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.*;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.univocity.parsers.common.input.WriterCharAppender;

public class WriteCharAppenderTest{

    public WriterCharAppender appender;
    public WriterCharAppender appender2;
    StringWriter writer;
    CsvWriterSettings settings;
    @Before
    public void setUp() throws Exception {
        settings = new CsvWriterSettings();
        settings.getFormat().setLineSeparator(",");
        settings.getFormat().setNormalizedNewline('\n');
        settings.getFormat().setDelimiter('\t');
        settings.setMaxCharsPerColumn(16); // note default max length before expanding
        settings.getFormat().setQuote('"');
        settings.getFormat().setQuoteEscape('"');
        settings.setIgnoreLeadingWhitespaces(false);
        settings.setIgnoreTrailingWhitespaces(false);
        settings.setQuoteAllFields(true);
        settings.setEmptyValue("");

        appender = new WriterCharAppender(4,"",0, settings.getFormat());
        writer = new StringWriter();

        settings.getFormat().setLineSeparator(",|");
        appender2 = new WriterCharAppender(4,"",0, settings.getFormat());

    }

    @After
    public void tearDown() throws Exception {
        appender.reset();
    }

    @Test
    public void testWhiteSpaceIsIgnored() throws Exception {
        String x = "SantaClaus  ";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespace(x.charAt(i));
        }
        assertEquals("SantaClaus", appender.getAndReset());
    }

    @Test
    public void testWhiteSpaceIsIgnoredWithNewLine() throws Exception {
        String x = "Santa\nClaus  ";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespace(x.charAt(i));
        }
        assertEquals("Santa,Claus", appender.getAndReset());
    }

    @Test
    public void testWhiteSpaceIsIgnoredWithNewLineButNoDenomarlization() throws Exception {
        String x = "Santa\nClaus  ";
        appender.enableDenormalizedLineEndings(false);
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespace(x.charAt(i));
        }
        assertEquals("Santa\nClaus", appender.getAndReset());
    }

    @Test
    public void testWhiteSpaceIsIgnoredWithNewLineWithSecondOperator() throws Exception {
        String x = "Santa\nClaus  ";
        for (int i = 0; i < x.length(); i++) {
            appender2.appendIgnoringWhitespace(x.charAt(i));
        }
        assertEquals("Santa,|Claus", appender2.getAndReset());
    }


    @Test
    public void testPaddingIsIgnored() throws Exception {
        String x = "rotten tomatoesZZZ";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringPadding(x.charAt(i),'Z');
        }

        assertEquals("rotten tomatoes", appender.getAndReset());
    }

    @Test
    public void testPaddingIsIgnoredWithNewLineWithSecondOperator() throws Exception {
        String x = "rotten\n tomatoesZZZ";
        for (int i = 0; i < x.length(); i++) {
            appender2.appendIgnoringPadding(x.charAt(i),'Z');
        }

        assertEquals("rotten,| tomatoes", appender2.getAndReset());
    }

    @Test
    public void testPaddingIsIgnoredWithNewLineButNoDenomarlization() throws Exception {
        String x = "Santa\nClausZZZZZZZ";
        appender.enableDenormalizedLineEndings(false);
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringPadding(x.charAt(i),'Z');
        }
        assertEquals("Santa\nClaus", appender.getAndReset());
    }

    @Test
    public void testPaddingIsIgnoredWithNewLine() throws Exception {
        String x = "rotten\n tomatoesZZZ";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringPadding(x.charAt(i),'Z');
        }
        assertEquals("rotten, tomatoes", appender.getAndReset());
    }

    @Test
    public void TestappendIgnoringWhitespaceandPadding() throws Exception {
        String x = "rotten tomatoes Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        assertEquals("rotten tomatoes", appender.getAndReset());
    }

    @Test
    public void TestappendIgnoringWhitespaceandPaddingWithNewLine() throws Exception {
        String x = "rotten \ntomatoes Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        assertEquals("rotten ,tomatoes", appender.getAndReset());
    }

    @Test
    public void testAppendIgnoringWhitespaceandPaddingButNoDenomarlization() throws Exception {
        String x = "Santa\nClausZZ ZZ ZZ Z";
        appender.enableDenormalizedLineEndings(false);
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        assertEquals("Santa\nClaus", appender.getAndReset());
    }

    @Test
    public void TestappendIgnoringWhitespaceandPaddingWithNewLineAndSecondOperator() throws Exception {
        String x = "rotten \ntomatoes Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            appender2.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        assertEquals("rotten ,|tomatoes", appender2.getAndReset());
    }


    @Test
    public void AppendNewLineFunctionsCorrectly() throws Exception {
        String x = "rotten tomatoes";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        appender.appendNewLine();
        assertEquals("rotten tomatoes,", appender.getAndReset());
    }

    @Test
    public void AppendNewLineFunctionsCorrectlyWith2ndSeparator() throws Exception {
        String x = "rotten tomatoes";
        for (int i = 0; i < x.length(); i++) {
            appender2.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        appender2.appendNewLine();
        assertEquals("rotten tomatoes,|", appender2.getAndReset());
    }

    @Test
    public void appendWithGeneralCharacter() throws Exception {
        String x = "rotten tomatoes";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        appender.append('?');
        assertEquals("rotten tomatoes?", appender.getAndReset());
    }

    @Test
    public void appendWithNewLineChar() throws Exception {
        String x = "rotten tomatoes";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        appender.append('\n');
        assertEquals("rotten tomatoes,", appender.getAndReset());
    }


    @Test
    public void WritingWithGeneralIndex() throws Exception {
        String x = "rotten \ntomatoes Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            appender.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        appender.writeCharsAndReset(writer);
        assertEquals("rotten ,tomatoes", writer.toString());
    }

    @Test
    public void WritingWithGeneralIndexWithNoneNull() throws Exception {
        WriterCharAppender mini = new WriterCharAppender(4,"badadabada",0, settings.getFormat());
        String x = " Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            mini.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        mini.writeCharsAndReset(writer);
        assertEquals("badadabada", writer.toString());
    }

    @Test
    public void WritingWithGeneralIndexWithNull() throws Exception {
        WriterCharAppender mini = new WriterCharAppender(4,null,0, settings.getFormat());
        StringWriter writerSpy = spy(writer);
        String x = " Z Z  Z";
        for (int i = 0; i < x.length(); i++) {
            mini.appendIgnoringWhitespaceAndPadding(x.charAt(i),'Z');
        }
        mini.writeCharsAndReset(writerSpy);
        verify(writerSpy, never()).write((char[]) any(),anyInt(),anyInt());
    }




}