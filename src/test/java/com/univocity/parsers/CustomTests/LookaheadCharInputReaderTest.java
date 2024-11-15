/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.input;

import org.testng.annotations.*;

import java.io.*;

import static org.junit.Assert.*;

public class LookaheadCharInputReaderTest {

	@Test
	public void correctInputSyntaxResultsInExpectedFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(3);
		assertTrue(reader.matches(current, new char[]{'a', 'j'}, '?'));
	}

	@Test
	public void emptyCurrentCharacterDoesNotAffectExpectedFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("\0jax"));
		reader.nextChar();
		char current = '\0';
		reader.lookahead(2);
		assertTrue(reader.matches(current, new char[]{'\0', 'j'}, '?'));
	}

	@Test
	public void escapeCurrentCharacterDoesNotAffectExpectedFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("\njax"));
		reader.nextChar();
		char current = '\n';
		reader.lookahead(2);
		assertTrue(reader.matches(current, new char[]{'\n', 'j'}, '?'));
	}

	@Test
	public void nullSequenceResultsInThrowingNullPointerException() {
		final LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		char[] sequence = null;
		reader.lookahead(1);
		assertThrows(NullPointerException.class, () -> reader.matches(current, sequence, '?'));
	}

	@Test
	public void emptySequenceResultsInArrayIndexOutOfBoundsException() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		char[] sequence = {};
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> reader.matches(current, sequence, '?'));
	}

	@Test
	public void sequenceWithSingleCharacterDoesNotAffectNormalFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(1);
		char[] sequence = {'a'};
		assertTrue(reader.matches(current, sequence, '?'));
	}

	@Test
	public void nullWildcardDoesNotAffectNormalFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("a\0ax"));
		char current = reader.nextChar();
		reader.lookahead(2);
		char[] sequence = {'a', '\0'};
		assertTrue(reader.matches(current, sequence, '\0'));
	}

	@Test
	public void escapeCharWildcardDoesNotAffectNormalFunctionality() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("a\nax"));
		char current = reader.nextChar();
		reader.lookahead(2);
		char[] sequence = {'a', '\n'};
		assertTrue(reader.matches(current, sequence, '\n'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferCanMatchAllCharacters() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a'};
		assertTrue(reader.matches(sequence, '?'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferCanFullyMatchWithWildcards() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a', '?', '?'};
		assertTrue(reader.matches(sequence, '?'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferPartiallyMatchStillFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a', '?', 'x'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferFullyMismatchingFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'x', 'y', 'z'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void equallyLongSequenceWithLookaheadBufferCanMatchAllCharacters() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a', 'j', 'a', 'x'};
		assertTrue(reader.matches(sequence, '?'));
	}

	@Test
	public void equallyLongSequenceWithLookaheadBufferCanFullyMatchWithWildcards() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a', '?', '?', 'x'};
		assertTrue(reader.matches(sequence, '?'));
	}

	@Test
	public void equallyLongSequenceWithLookaheadBufferPartiallyMatchStillFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'a', '?', 'x', 'x'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void equallyLongSequenceWithLookaheadBufferFullyMismatchingFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char[] sequence = {'x', 'y', 'z', 'a'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void longerSequenceWithLookaheadBufferPartiallyMatchingFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(2);
		char[] sequence = {'a', '?', 'a', 'x'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void longerSequenceWithLookaheadBufferFullyMismatchingFailsMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(2);
		char[] sequence = {'x', 'y', 'z', 'a'};
		assertFalse(reader.matches(sequence, '?'));
	}

	@Test
	public void equallyLongSequenceWithLookaheadBufferAndMatchingFirstCharacterCanMatchAllCharacters() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(2);
		assertTrue(reader.matches(current, new char[]{'a', 'j'}, '?'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferAndMatchingFirstCharacterCanFullyMatchWithWildcards() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(3);
		assertTrue(reader.matches(current, new char[]{'?', '?'}, '?'));
	}

	@Test
	public void shorterSequenceWithLookaheadBufferAndMatchingFirstCharacterCanPartiallyMatchAndFailMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(3);
		assertFalse(reader.matches(current, new char[]{'a', 'x'}, '?'));
	}

	@Test
	public void positioningStartPointerAtStartCanGetNextCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char result = reader.nextChar();
		assertEquals('a', result);
	}

	@Test
	public void positioningStartPointerAtTheMiddleOfBufferCanGetNextCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		char result = reader.nextChar();
		assertEquals('j', result);
	}

	@Test
	public void positioningStartPointerAtTheEndOfBufferCanGetNextCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		reader.nextChar();
		reader.nextChar();
		char result = reader.nextChar();
		assertEquals('x', result);
	}

	@Test
	public void positioningStartPointerBeyondBufferAndAskingForNextCharacterResultsInEOFException() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		reader.nextChar();
		reader.nextChar();
		reader.nextChar();
		assertThrows(EOFException.class, reader::nextChar);
	}

	@Test
	public void positioningStartPointerAtStartCanGetCurrentCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		assertEquals('a', reader.getChar());
	}

	@Test
	public void positioningStartPointerAtTheMiddleOfBufferCanGetCurrentCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		reader.nextChar();
		assertEquals('j', reader.getChar());
	}

	@Test
	public void positioningStartPointerAtTheEndOfBufferCanGetCurrentCharacter() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		reader.nextChar();
		reader.nextChar();
		reader.nextChar();
		reader.nextChar();
		assertEquals('x', reader.getChar());
	}

	@Test
	public void whitespaceCharacterCanBeSkippedAndResultsInReturningTheFollowingNonWhitespaceCharacters() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader(" ajax"));
		reader.lookahead(5);
		char result = reader.skipWhitespace(' ', '\0', '\0');
		assertEquals('a', result);
	}

	@Test
	public void newLineCharacterCannotBeSkippedAndResultsInStalling() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader(" \najax"));
		reader.lookahead(6);
		char result = reader.skipWhitespace(' ', '\0', '\n');
		assertEquals('\n', result);
	}

	@Test
	public void escapeCharacterCannotBeSkippedAndResultsInStalling() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader(" \n\tajax"));
		reader.lookahead(7);
		reader.skipWhitespace(' ', '\t', '\n');
		char result = reader.skipWhitespace(' ', '\t', '\n');
		assertEquals('\t', result);
	}

	@Test
	public void nonWhitespaceCharacterCannotBeSkippedAndResultsInStalling() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(4);
		char result = reader.skipWhitespace(' ', '\0', '\0');
		assertEquals('a', result);
	}

	@Test
	public void whitespaceOutOfWhitespaceStartRangeResultsInStallingAtWhitespace() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', 32);
		reader.start(new StringReader(" ajax"));
		reader.lookahead(5);
		char result = reader.skipWhitespace(' ', '\0', '\0');
		assertEquals(' ', result);
	}

	@Test
	public void startPointerPositionBeyondLookaheadBufferResultsInFailingInSkippingWhitespace() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', -1);
		reader.start(new StringReader("   "));
		reader.lookahead(3);
		reader.skipWhitespace(' ', '\0', '\0');
		assertThrows(EOFException.class, reader::nextChar);
	}

	@Test
	public void lookaheadZeroCharacterDoesNotHaveEffect() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', -1);
		reader.start(new StringReader(""));
		reader.lookahead(0);
		assertEquals("", reader.getLookahead());
	}

	@Test
	public void lookaheadLessCharacterThanBufferResultsInIncompleteContent() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		reader.lookahead(2);
		assertEquals("aj", reader.getLookahead());
	}

	@Test
	public void lookaheadSameAmountOfCharacterThanBufferResultsInCompleteContent() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', -1);
		String matchResult = "ajax";
		reader.start(new StringReader(matchResult));
		reader.lookahead(matchResult.length());
		assertEquals(matchResult, reader.getLookahead());
	}

	@Test
	public void lookaheadMoreCharacterThanBufferResultsInCompleteContent() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, 32, true), '\n', -1);
		String matchResult = "ajax";
		reader.start(new StringReader(matchResult));
		reader.lookahead(matchResult.length() + 2);
		assertEquals(matchResult, reader.getLookahead());
	}

	// structural testing: discovering 3 new testings
	@Test
	public void longerSequenceCannotAchieveFullMatching() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(6);
		char[] sequence = {'a', 'j', 'a', 'x', 'o', 'k'};
		assertFalse(reader.matches(current, sequence, '?'));
	}

	@Test
	public void sequenceWithUnmatchedFirstCharacterDoesNotMatch() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("bjax"));
		char current = reader.nextChar();
		reader.lookahead(6);
		char[] sequence = {'a', 'j', 'a', 'x', 'o', 'k'};
		assertFalse(reader.matches(current, sequence, '?'));
	}

	@Test
	public void sequenceWithUnmatchedFirstCharacterWithWildcardDoesNotMatch() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);
		reader.start(new StringReader("ajax"));
		char current = reader.nextChar();
		reader.lookahead(6);
		char[] sequence = {'\n', 'j', 'a', 'x', 'o', 'k'};
		assertFalse(reader.matches(current, sequence, '\0'));
	}


	/** original test **/
	@Test
	public void testLookahead() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);

		reader.start(new StringReader("abcdefgh"));

		assertEquals(reader.nextChar(), 'a');

		reader.lookahead(1);
		reader.lookahead(1);
		assertTrue(reader.matches(new char[]{'b', 'c'}, '?'));
		assertTrue(reader.matches(new char[]{'b'}, '?'));
		assertFalse(reader.matches(new char[]{'c'}, '?'));
		assertFalse(reader.matches(new char[]{'a', 'b'}, '?'));
		assertFalse(reader.matches(new char[]{'c', 'd'}, '?'));

		assertEquals(reader.nextChar(), 'b');

		assertFalse(reader.matches(new char[]{'b'}, '?'));
		assertTrue(reader.matches(new char[]{'c'}, '?'));
		assertEquals(reader.nextChar(), 'c');
		assertFalse(reader.matches(new char[]{'c'}, '?'));
		assertFalse(reader.matches(new char[]{'d'}, '?'));
		assertEquals(reader.nextChar(), 'd');
		assertEquals(reader.nextChar(), 'e');

		reader.lookahead(5);
		assertTrue(reader.matches(new char[]{'f', 'g', 'h'}, '?'));
		assertTrue(reader.matches(new char[]{'f', 'g'}, '?'));
		assertTrue(reader.matches(new char[]{'f'}, '?'));

		assertEquals(reader.nextChar(), 'f');
		assertEquals(reader.nextChar(), 'g');
		assertTrue(reader.matches(new char[]{'h'}, '?'));
		assertEquals(reader.nextChar(), 'h');
		assertFalse(reader.matches(new char[]{'f'}, '?'));

		try {
			char ch = reader.nextChar();
			fail("Expected EOFException after end of the input. Got char: " + ch);
		} catch (EOFException ex) {
			//pass
		}
	}
}
