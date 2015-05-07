package com.copetti.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.copetti.core.Question.Difficulty;
import com.copetti.core.dao.AbstractDAO;

public class QuestionPoolTest {

	QuestionPool qp;
	AbstractDAO<Question> myDAO;

	@Before
	public void setUp() throws Exception {

		myDAO = new AbstractDAO<Question>() {

			@Override
			public List<Question> restoreAll() {

				Set<String> wrongOptions = new HashSet<>(Arrays.asList("B",
						"C", "D"));

				Question questionEj = new Question("J", "answer", wrongOptions,
						Difficulty.EASY);
				Question questionEk = new Question("K", "answer", wrongOptions,
						Difficulty.EASY);
				Question questionMl = new Question("L", "answer", wrongOptions,
						Difficulty.MEDIUM);
				Question questionMm = new Question("M", "answer", wrongOptions,
						Difficulty.MEDIUM);
				Question questionHn = new Question("N", "answer", wrongOptions,
						Difficulty.HARD);
				Question questionHo = new Question("O", "answer", wrongOptions,
						Difficulty.HARD);
				Question questionHp = new Question("P", "answer", wrongOptions,
						Difficulty.HARD);

				Question[] questions = new Question[] { questionEj, questionEk,
						questionMl, questionMm, questionHn, questionHo,
						questionHp };

				return Arrays.asList(questions);
			}
		};

		qp = new QuestionPool(myDAO, 2);
	}

	@Test
	public void testQuestionPool() {
		try {
			@SuppressWarnings("unused")
			QuestionPool qp = new QuestionPool(myDAO, 3);
			fail();
		} catch (IllegalArgumentException e) {

		}

	}

	@Test
	public void testNext() {

		assertTrue(qp.hasNext(Difficulty.EASY));
		assertNotNull(qp.getNext(Difficulty.EASY));
		assertNotNull(qp.getNext(Difficulty.EASY));
		assertFalse(qp.hasNext(Difficulty.EASY));

		assertTrue(qp.hasNext(Difficulty.MEDIUM));
		assertNotNull(qp.getNext(Difficulty.MEDIUM));
		assertNotNull(qp.getNext(Difficulty.MEDIUM));
		assertFalse(qp.hasNext(Difficulty.MEDIUM));

		assertTrue(qp.hasNext(Difficulty.HARD));
		assertNotNull(qp.getNext(Difficulty.HARD));
		assertNotNull(qp.getNext(Difficulty.HARD));
		assertTrue(qp.hasNext(Difficulty.HARD));
		assertNotNull(qp.getNext(Difficulty.HARD));
		assertFalse(qp.hasNext(Difficulty.HARD));

	}

}
