package com.copetti.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.copetti.core.MillionShowGame.GameAction;
import com.copetti.core.MillionShowGame.PrizeValue;
import com.copetti.core.Question.Difficulty;
import com.copetti.core.Question.Option;
import com.copetti.core.dao.AbstractDAO;


public class MillionGameTest
{

	private enum Answer
	{
		RIGHT, WRONG;
	}

	private MillionGame mg;

	@Before
	public void setUp()
	{
		// Build 20 questions and add to an instance of MillionGame
		QuestionPool qp = new QuestionPool(new AbstractDAO<Question>()
		{

			@Override
			public List<Question> restoreAll()
			{
				Set<String> wrongOptions = new HashSet<>(Arrays.asList("B",
						"C", "D"));
				List<Question> questions = new ArrayList<Question>();
				Difficulty[] diffs = Difficulty.values();

				for( int i = 0; i <= 21 /* questions */; i++ )
				{
					questions.add(new Question("A" + i, "answer", wrongOptions,
							diffs[i % diffs.length]));
				}

				return questions;

			}
		}, 7);

		mg = new MillionGame(qp);
	}

	@Test
	public void testCanQuit()
	{
		// Can quit the game anytime!
		for( int i = 0; i < mg.getLastLevel(); i++ )
		{
			answer(mg, Answer.RIGHT);
			assertTrue(mg.canQuit());
		}

		// Prize for quitting the last level is 0.
		assertTrue(mg.getPrizes().get(PrizeValue.ERROR) == 0);
	}

	@Test
	public void testQuit()
	{
		for( int i = 0; i <= mg.getLastLevel(); i++ )
		{
			mg.quit();
			assertTrue(mg.isGameOver());
			assertTrue(mg.getPrizes().get(PrizeValue.STOP) == mg
					.getGameOverPrize());
		}
	}

	@Test
	public void testCan()
	{
		// The game has just started
		assertTrue(mg.can(GameAction.USECARDS) > 0);

		while (mg.can(GameAction.USECARDS) > 0)
			mg.useCards();

		assertTrue(mg.can(GameAction.USECARDS) == 0);

		try
		{
			mg.useCards();
			throw new AssertionError();
		}
		catch (IllegalStateException e)
		{
			// IGNORE
		}

		// ********* //

		assertTrue(mg.can(GameAction.SKIP) > 0);
		while (mg.can(GameAction.SKIP) > 0)
			mg.skip();

		assertTrue(mg.can(GameAction.SKIP) == 0);

		try
		{
			mg.skip();
			throw new AssertionError();
		}
		catch (IllegalStateException e)
		{
			// IGNORE
		}
	}

	@Test
	public void testSkip()
	{
		// The level must be the same after each skip.
		// The question should be different.
		// You should have a limited amount of skips.
		Question q = mg.getCurrentQuestion();
		int currentLevel = mg.getCurrentLevel();

		while (mg.can(GameAction.SKIP) > 0)
		{
			assertTrue(mg.can(GameAction.SKIP) > 0);
			mg.skip();
			Question q2 = mg.getCurrentQuestion();
			int currentLevel2 = mg.getCurrentLevel();

			assertFalse(q.equals(q2));
			assertTrue(currentLevel == currentLevel2);
			assertTrue(q.getDifficulty() == q2.getDifficulty());

			q = q2;
		}
	}

	@Test
	public void testUseCards()
	{
		// The Set returned must be equal to the numberOfCardsToAdd.
		// The Set MUST contain the correct answer.
		// The question should not change
		// The currentLevel should not change

		Set<Option> optionsLeft = mg.useCards();
		assertTrue(optionsLeft.contains(mg.getCurrentQuestion().getAnswer()));
	}

	@Test
	public void testAnswerCorrect()
	{
		// Change currentLevel
		// Change currentQuestion
		//
		Question cQ = mg.getCurrentQuestion();
		int prizeScore = mg.getPrizes().get(PrizeValue.SCORE);
		mg.answer(cQ.getAnswer());
		Question secondQ = mg.getCurrentQuestion();
		assertFalse(cQ.equals(secondQ));
		assertTrue(mg.getPrizes().get(PrizeValue.SCORE) > prizeScore);
	}

	@Test
	public void testAnswerWrong()
	{
		Question cQ = mg.getCurrentQuestion();
		int prizeError = mg.getPrizes().get(PrizeValue.ERROR);
		answer(mg, Answer.WRONG);
		Question secondQ = mg.getCurrentQuestion();

		assertTrue(cQ.equals(secondQ));
		assertTrue(mg.isGameOver());
		assertTrue(mg.getGameOverPrize() == prizeError);

		assertTrue(mg.getPrizes().get(PrizeValue.SCORE) > prizeError);
	}

	@Test
	public void testIsGameOver()
	{
		Option opt = mg.getCurrentQuestion().getAnswer();
		mg.answer(Option.A == opt ? Option.B : Option.A);
		assertTrue(mg.isGameOver());
	}

	@Test
	public void testGetGameOverPrize()
	{
		// Answer correctly 10 questions
		for( int i = 0; i < 10; i++ )
		{
			mg.answer(mg.getCurrentQuestion().getAnswer());
		}
		int finalPrize = mg.getPrizes().get(PrizeValue.ERROR);
		answer(mg, Answer.WRONG);
		assertTrue(mg.getPrizes().get(PrizeValue.ERROR) == finalPrize);
		assertTrue(finalPrize == mg.getGameOverPrize());
	}

	private static void answer(MillionGame mg, Answer ans)
	{
		if (ans == Answer.RIGHT)
			mg.answer(mg.getCurrentQuestion().getAnswer());
		else
			mg.answer(mg.getCurrentQuestion().getAnswer() == Option.A ? Option.B
					: Option.A);
	}

	@Test
	public void testGetCurrentQuestion()
	{
		List<Question> questions = new ArrayList<Question>();
		while (!mg.isGameOver())
		{
			assertFalse(questions.contains(mg.getCurrentQuestion()));
			questions.add(mg.getCurrentQuestion());
			mg.answer(mg.getCurrentQuestion().getAnswer());
		}

		assertTrue(mg.getCurrentLevel() == mg.getLastLevel());
	}

	@Test
	public void testGetPrizes()
	{
		Map<PrizeValue, Integer> lastPrize = mg.getPrizes();
		while (true)
		{
			answer(mg, Answer.RIGHT);

			if (mg.isGameOver()) break;

			assertTrue(mg.getPrizes().get(PrizeValue.SCORE) > lastPrize
					.get(PrizeValue.SCORE));
			assertTrue(mg.getPrizes().get(PrizeValue.STOP) > lastPrize
					.get(PrizeValue.STOP));
			if (mg.getLastLevel() != mg.getCurrentLevel()) // The prize is zero
															// if it gets the
															// answer wrong in
															// the last
															// question.
				assertTrue(mg.getPrizes().get(PrizeValue.ERROR) > lastPrize
						.get(PrizeValue.ERROR));

			lastPrize = mg.getPrizes();
		}

		assertTrue(mg.getCurrentLevel() == mg.getLastLevel());
	}

}
