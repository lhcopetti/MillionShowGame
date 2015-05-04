package com.copetti.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Question {

	public enum Option {
		A, B, C, D;
	}

	public enum Difficulty {
		EASY, MEDIUM, HARD;

		public static Difficulty fromString(String s) {
			for (Difficulty d : Difficulty.values())
				if (d.toString().equalsIgnoreCase(s))
					return d;
			throw new IllegalArgumentException("The value: " + s
					+ " is not a valid Difficulty.");
		}
	}

	private String questionString;
	private Map<Option, String> options;
	private Option answer;
	private Difficulty difficulty;

	public Question(String question, String answer, Set<String> wrongOptions,
			String difficulty) {

		if (wrongOptions.size() < 3)
			throw new IllegalArgumentException(
					"The set of Options must have at least 3 different options. Size: "
							+ wrongOptions.size());

		if (wrongOptions.contains(answer))
			throw new IllegalArgumentException(
					"It is not valid to have the answer contained in the wrongOptions Set. Answer: "
							+ answer);

		if (question == null || question.isEmpty() || answer == null
				|| answer.isEmpty())
			throw new IllegalArgumentException(
					"Neither questionString nor answer can be null or empty");

		this.questionString = question;

		// Randomize the array
		List<String> randomizedOptions = new ArrayList<String>();
		randomizedOptions.add(answer);
		randomizedOptions.addAll(wrongOptions);
		Collections.shuffle(randomizedOptions);

		// Add the randomized options to Map
		this.options = new HashMap<Option, String>();
		for (int i = 0; i < randomizedOptions.size(); i++) {
			options.put(Option.values()[i], randomizedOptions.get(i));
		}

		// Find the correct Option and save it as the answer
		for (Entry<Option, String> entry : options.entrySet())
			if (entry.getValue().equals(answer)) {
				this.answer = entry.getKey();
				break;
			}

		this.difficulty = Difficulty.fromString(difficulty);
	}

	public String getQuestion() {
		return questionString;
	}

	public Map<Option, String> getOptions() {
		return Collections.unmodifiableMap(options);
	}

	protected Option getAnswer() {
		return answer;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Question))
			return false;

		Question q = (Question) obj;

		return q.getQuestion().equals(getQuestion());

	}
}
