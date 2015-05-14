package com.copetti.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.copetti.core.Question.Difficulty;
import com.copetti.core.dao.AbstractDAO;

public class QuestionPool {

	private Map<Difficulty, Set<Question>> questions;

	public QuestionPool(AbstractDAO<Question> questionDAO,
			int mininumNumberOfQuestionsPerDiff) {

		questions = new HashMap<Question.Difficulty, Set<Question>>();

		restoreAll(questionDAO.restoreAll(), mininumNumberOfQuestionsPerDiff);
	}

	private void restoreAll(List<Question> questionsDao,
			int mininumNumberOfQuestionsPerDiff) {

		// Shuffle the entire collection so that the removal can be sequential.
		Collections.shuffle(questionsDao);

		for (Question q : questionsDao) {
			addToMap(questions, q);
		}

		// Validate at least the mininum number of questions/difficulty
		for (Difficulty d : Difficulty.values()) {
			if (questions.get(d) == null
					|| questions.get(d).size() < mininumNumberOfQuestionsPerDiff) {
				Set<Question> set = questions.get(d);

				StringBuilder sb = new StringBuilder();
				sb.append("Invalid set of Data! Not enough questions/difficulty supplied. ");
				sb.append("Difficulty: " + d.toString());
				sb.append(" - Questions: " + set == null ? "null" : "Size: "
						+ set.size());
				sb.append("- Minimum: " + mininumNumberOfQuestionsPerDiff);
				throw new IllegalArgumentException(sb.toString());
			}

		}
	}

	private void addToMap(Map<Difficulty, Set<Question>> questions, Question q) {

		Difficulty diff = q.getDifficulty();

		if (questions.get(diff) == null)
			questions.put(diff, new HashSet<Question>());

		questions.get(diff).add(q);
	}

	public boolean hasNext(Difficulty d) {
		Set<Question> set = questions.get(d);

		return set != null && set.size() > 0;
	}

	public Question getNext(Difficulty d) {

		Set<Question> set = questions.get(d);
		if (set == null || set.size() < 1)
			throw new IllegalStateException(
					"There are no questions left to be queried!");

		Question q = (Question) set.toArray()[0];
		set.remove(q);
		return q;
	}

}
