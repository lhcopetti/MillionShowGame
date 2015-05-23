/**
 * 
 */
package com.copetti.core.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.copetti.core.Question;
import com.copetti.core.dao.QuestionXMLDao;

/**
 * @author LuisCopetti
 *
 */
public class TestXMLDao {

	/**
	 * Test method for {@link com.copetti.core.dao.QuestionXMLDao#restoreAll()}.
	 */
	@Test
	public void testRestoreAll() {

		List<Question> questions = null;
		File tempFile = null;
		QuestionXMLDao qxd = null;

		try {
			tempFile = File.createTempFile("testFile", "xmldao");
			FileOutputStream fos = new FileOutputStream(tempFile);
			String xmlFile = "<milliongame>\r\n" + "\r\n" + "	<question>\r\n"
					+ "		<questionString>question1</questionString>\r\n"
					+ "		<answer>answer</answer>\r\n"
					+ "		<difficulty>EASY</difficulty>\r\n"
					+ "		<wrongOption>opt1</wrongOption>\r\n"
					+ "		<wrongOption>opt2</wrongOption>\r\n"
					+ "		<wrongOption>opt3</wrongOption>\r\n"
					+ "	</question>\r\n" + "\r\n" + "	<question>\r\n"
					+ "		<questionString>question2</questionString>\r\n"
					+ "		<answer>answer</answer>\r\n"
					+ "		<difficulty>EASY</difficulty>\r\n"
					+ "		<wrongOption>opt1</wrongOption>\r\n"
					+ "		<wrongOption>opt3</wrongOption>\r\n"
					+ "		<wrongOption>opt2</wrongOption>\r\n"
					+ "	</question>\r\n" + "</milliongame>";
			fos.write(xmlFile.getBytes(), 0, xmlFile.length());
			fos.flush();
			fos.close();
			qxd = new QuestionXMLDao(tempFile);

			questions = qxd.restoreAll();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Set<String> wrongOptions = new HashSet<String>();
		wrongOptions.add("opt1");
		wrongOptions.add("opt2");
		wrongOptions.add("opt3");

		String question1 = "question1";
		String question2 = "question2";

		String answer = "answer";
		String difficulty = "easy";

		Question q1 = new Question(question1, answer, wrongOptions, difficulty);
		Question q2 = new Question(question2, answer, wrongOptions, difficulty);

		questions.remove(q1);
		assertTrue(questions.size() == 1);

		questions.remove(q2);
		assertTrue(questions.isEmpty());
	}
}
