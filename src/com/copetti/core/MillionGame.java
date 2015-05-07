package com.copetti.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.copetti.core.Question.Difficulty;
import com.copetti.core.Question.Option;
import com.sun.xml.internal.txw2.IllegalSignatureException;


public class MillionGame implements MillionShowGame
{

	public static final int MAX_SKIP = 3;
	public static final int MAX_USECARDS = 1;

	private static final int INDEX_ERROR = 2;
	private static final int INDEX_STOP = 1;
	private static final int INDEX_SCORE = 0;

	private static final int DIFFICULTY_EASY_RANGE = 5;
	private static final int DIFFICULTY_MEDIUM_RANGE = 10;
	@SuppressWarnings("unused")
	private static final int DIFFICULTY_HARD_RANGE = 16;

	private QuestionPool questionPool;
	public Map<GameAction, Integer> availableActions;

	private int currentLevel;
	private Difficulty currentDifficulty;

	private boolean gameOver;
	private PrizeValue lastAction;

	private String reasonGameOver;

	private Question currentQuestion;

	// @formatter:off
	public static final int[][] prizes = { 
		{ 1000,    500,    0      },			
		{ 2000,    1000,   500    },			
		{ 3000,    2000,   1000   },		
		{ 4000,    3000,   1500   },		
		{ 5000,    4000,   2000   },		
		{ 1000,    5000,   2500   },		
		{ 20000,   10000,  5000   },		
		{ 30000,   20000,  10000  },		
		{ 40000,   30000,  15000  },		
		{ 50000,   40000,  20000  },
		{ 100000,  50000,  25000  },
		{ 200000,  100000, 50000  },
		{ 300000,  200000, 100000 },
		{ 400000,  300000, 150000 },
		{ 500000,  400000, 200000 },
		{ 1000000, 500000, 0      }};
	// @formatter:on

	public MillionGame(QuestionPool qp)
	{
		this.questionPool = qp;

		availableActions = new HashMap<MillionShowGame.GameAction, Integer>();
		availableActions.put(GameAction.SKIP, MAX_SKIP);
		availableActions.put(GameAction.USECARDS, MAX_USECARDS);

		currentLevel = 0;
		gameOver = false;

		currentQuestion = questionPool.getNext(Difficulty.EASY);
	}

	@Override
	public boolean canQuit()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void quit() throws IllegalStateException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int can(GameAction gameAction)
	{
		return availableActions.get(gameAction);
	}

	@Override
	public void skip()
	{
		if (can(GameAction.SKIP) > 0)
		{
			int skipRemaining = availableActions.get(GameAction.SKIP);
			availableActions.put(GameAction.SKIP, --skipRemaining);
		}
	}

	@Override
	public Set<Option> useCards()
	{

		if (can(GameAction.USECARDS) <= 0) { throw new IllegalStateException(
				"Cannot use cards anymore!"); }

		int useRemaining = availableActions.get(GameAction.USECARDS);
		availableActions.put(GameAction.USECARDS, --useRemaining);

		return getUseCardsOptions();
	}

	protected Set<Option> getUseCardsOptions()
	{
		Random random = new Random();
		int numberOfCardsToAdd = random.nextInt() % (Option.values().length);

		// Add the answer
		List<Option> options = Arrays.asList(getCurrentQuestion().getAnswer());

		List<Option> optionPool = Arrays.asList(Option.values());
		optionPool.remove(getCurrentQuestion().getAnswer());
		Collections.shuffle(optionPool);

		for( int i = 0; i < numberOfCardsToAdd; i++ )
			options.add(optionPool.get(i));

		return Collections.unmodifiableSet(new HashSet<Option>(options));
	}

	@Override
	public boolean answer(Option option)
	{

		if (isGameOver()) throw new IllegalStateException("Game is over!");

		boolean answeredRight = currentQuestion.getAnswer() == option;

		if (answeredRight)
		{

			currentLevel++;
			lastAction = PrizeValue.SCORE;

			if (currentLevel == prizes.length)
			{
				currentLevel--;
				setGameOver("You WIN the FINAL Prize! Congratulations!");
			}

		}
		else
		{
			lastAction = PrizeValue.ERROR;
			setGameOver("Wrong Answer!");

		}

		return answeredRight;
	}

	@Override
	public boolean isGameOver()
	{
		return gameOver;
	}

	@Override
	public int getGameOverPrize() throws IllegalStateException
	{
		if (!isGameOver())
			throw new IllegalStateException(
					"Cant get prize now! Game isn't over yet.");
		else
			return getPrizes().get(lastAction);
	}

	@Override
	public Question getCurrentQuestion()
	{

		return currentQuestion;
	}

	@Override
	public Map<PrizeValue, Integer> getPrizes()
	{

		int[] prizesArray = prizes[currentLevel];

		Map<PrizeValue, Integer> map = new HashMap<MillionShowGame.PrizeValue, Integer>();
		map.put(PrizeValue.ERROR, prizesArray[INDEX_ERROR]);
		map.put(PrizeValue.STOP, prizesArray[INDEX_STOP]);
		map.put(PrizeValue.SCORE, prizesArray[INDEX_SCORE]);
		return Collections.unmodifiableMap(map);
	}

	protected boolean goToLevel(int level)
	{

		if (level < 0 || level >= prizes.length) return false;

		currentLevel = level;

		return true;
	}

	private void setGameOver(String cause)
	{
		reasonGameOver = cause;
		gameOver = true;
	}

	public String getGameOverCause()
	{
		if (!isGameOver())
			throw new IllegalStateException("The Game is not over yet!");
		return reasonGameOver;
	}

	@Override
	public Option getCorrectAnswer() throws IllegalStateException
	{
		if (isGameOver()) return getCurrentQuestion().getAnswer();
		throw new IllegalSignatureException(
				"Cannot retrieve the answer before the game is over!");
	}

	public Difficulty difficultyFromLevel(int currentLevel)
	{
		if (currentLevel < DIFFICULTY_EASY_RANGE)
			return Difficulty.EASY;
		else
			if (currentLevel < DIFFICULTY_MEDIUM_RANGE)
				return Difficulty.MEDIUM;
			else
				return Difficulty.HARD;
	}

}
