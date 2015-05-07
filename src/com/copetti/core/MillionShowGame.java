package com.copetti.core;

import java.util.Map;
import java.util.Set;

import com.copetti.core.Question.Option;

public interface MillionShowGame {

	public enum PrizeValue {
		ERROR, STOP, SCORE
	}

	public enum GameAction {
		SKIP, USECARDS
	}

	/**
	 * Returns true if quitting is available. False, otherwise.
	 * 
	 * @return
	 */
	public boolean canQuit();

	/**
	 * If quitting is valid, quits the game. Setting the boolean flag `gameOver`
	 * and making the getPrize() function available.
	 * 
	 * @see #isGameOver()
	 * @see #getGameOverPrize()
	 * 
	 * @throws IllegalArgumentException
	 */
	public void quit() throws IllegalStateException;

	/**
	 * Helper method to avoid unnecessary exceptions.
	 * 
	 * @param gameAction
	 *            Which action should be probed.
	 * @return value > 0, if the action is available (value represents the
	 *         remaining amount). Any value less than or equal to zero means the
	 *         action is unavailable.
	 */
	public int can(GameAction gameAction);

	/**
	 * Skips the function if the object is in a state where skipping is valid.
	 */
	public void skip();

	/**
	 * Uses the Cards help if the object is in a state where using cards is
	 * valid.
	 * @param number TODO
	 */
	public Set<Option> useCards();

	/**
	 * Attempts to answer the question given by {@link #getCurrentQuestion()}.
	 * 
	 * @param option
	 *            The Option chosen
	 * @return True if the answer was correct. False, otherwise.
	 */
	public boolean answer(Option option);
	
	/**
	 * Returns the correct option if the game is over.
	 * @return
	 * @throws IllegalStateException
	 */
	public Option getCorrectAnswer() throws IllegalStateException;

	/**
	 * Returns true if the game is over. False, otherwise.
	 * 
	 * @return
	 */
	public boolean isGameOver();

	/**
	 * Returns the final prize if it is available.
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	public int getGameOverPrize() throws IllegalStateException;

	/**
	 * Returns the current Question.
	 * 
	 * @return current question
	 */
	public Question getCurrentQuestion();

	/**
	 * Get the Prize for the current Level, according to the action taken
	 * {@link PrizeValue}
	 * 
	 * @return Mapping of the values of the prizes with the action taken.
	 */
	public Map<PrizeValue, Integer> getPrizes();
}
