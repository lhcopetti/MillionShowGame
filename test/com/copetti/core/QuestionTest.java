package com.copetti.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.copetti.core.Question.Option;

public class QuestionTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQuestionInvalidSetSize() {

		String question = "q";
		String answer = "a";
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");

		@SuppressWarnings("unused")
		Question q = new Question(question, answer, mySet, "EASY");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQuestionInvalidDifficulty() {

		String question = "q";
		String answer = "a";
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		@SuppressWarnings("unused")
		Question q = new Question(question, answer, mySet, "MEDIUN");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQuestionInvalidQuestion() {

		String question = "";
		String answer = "a";
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		@SuppressWarnings("unused")
		Question q = new Question(question, answer, mySet, "HARD");
	}

	@Test
	public void testGetOptions() {
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		Question q = new Question("question", "answer", mySet, "EAsy");

		Map<Option, String> map = q.getOptions();
		for (Option op : Option.values())
			assertNotNull(map.get(op));
	}

	@Test
	public void testGetAnswer() {

		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		String answer = "myAnswer";

		Question q = new Question("question", answer, mySet, "EAsy");

		Option optionAnswer = q.getAnswer();
		assertTrue(q.getOptions().get(optionAnswer).equals(answer));
	}

	@Test
	public void testEqualsTrue() {
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		String answer = "myAnswer";

		Question question1 = new Question(new String("question"), answer,
				mySet, "EAsy");

		Set<String> mySet2 = new HashSet<String>();

		mySet2.add("wrongOptionA");
		mySet2.add("wrongOptionB");
		mySet2.add("wrongOptionC");
		Question question2 = new Question(new String("question"), answer,
				mySet2, "easy");

		assertTrue(question1.equals(question2));

	}

	@Test
	public void testEqualsFalse() {
		Set<String> mySet = new HashSet<String>();

		mySet.add("wrongOptionA");
		mySet.add("wrongOptionB");
		mySet.add("wrongOptionC");

		String answer = "myAnswer";

		Question question1 = new Question("question2", answer, mySet, "EAsy");

		Set<String> mySet2 = new HashSet<String>();

		mySet2.add("wrongOptionA");
		mySet2.add("wrongOptionB");
		mySet2.add("wrongOptionC");
		Question question2 = new Question(new String("question"), answer,
				mySet2, "easy");

		assertFalse(question1.equals(question2));

	}

}
