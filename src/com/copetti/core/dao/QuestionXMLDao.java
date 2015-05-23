package com.copetti.core.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.copetti.core.Question;

public class QuestionXMLDao implements AbstractDAO<Question> {

	public enum QuestionXML {
		QUESTION {
			@Override
			public String toString() {
				return "questionString";
			}
		},
		ANSWER {
			@Override
			public String toString() {
				return "answer";
			}
		},
		DIFFICULTY {
			@Override
			public String toString() {
				return "difficulty";
			}
		},
		WRONGOPTION {
			@Override
			public String toString() {
				return "wrongOption";
			}
		};

		public static QuestionXML fromString(String s) {
			for (QuestionXML c : QuestionXML.values())
				if (c.toString().equals(s))
					return c;
			throw new IllegalArgumentException("Invalid parameter: " + s);
		}
	}

	private InputStream inputStream;
	private DefaultHandler defaultHandler;
	private StringBuilder accumulator;

	private boolean openQuestionTag;
	private boolean openTag;

	private List<Question> questions;

	public String question;
	public String answer;
	public Set<String> wrongOptions = new HashSet<String>();
	public String difficulty;

	public QuestionXMLDao(InputStream is) {

		questions = new ArrayList<Question>();
		accumulator = new StringBuilder();
		this.inputStream = is;

		defaultHandler = new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				// TODO Auto-generated method stub
				super.startElement(uri, localName, qName, attributes);

				if (qName.equals("question")) {
					openQuestionTag = true;
					wrongOptions.clear();
					return;
				}

				openTag = true;
				accumulator.setLength(0);
			}

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				// TODO Auto-generated method stub
				super.characters(ch, start, length);

				if (openTag)
					accumulator.append(new String(ch, start, length));

			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				// TODO Auto-generated method stub
				super.endElement(uri, localName, qName);

				if (qName.equals("question")) {
					openQuestionTag = false;
					questions.add(new Question(QuestionXMLDao.this.question,
							QuestionXMLDao.this.answer,
							QuestionXMLDao.this.wrongOptions,
							QuestionXMLDao.this.difficulty));
					return;
				}

				if (openQuestionTag) {

					String value = accumulator.toString();

					switch (QuestionXML.fromString(qName)) {
					case QUESTION:
						QuestionXMLDao.this.question = value;
						break;
					case ANSWER:
						QuestionXMLDao.this.answer = value;
						break;
					case WRONGOPTION:
						QuestionXMLDao.this.wrongOptions.add(value);
						break;
					case DIFFICULTY:
						QuestionXMLDao.this.difficulty = value;
						break;

					}
				}

			}
		};
	}

	public QuestionXMLDao(File xmlFile) throws IOException {

		this(new FileInputStream(xmlFile));
	}

	@Override
	public List<Question> restoreAll() {

		questions.clear();

		try {

			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			xmlReader.setContentHandler(defaultHandler);
			xmlReader.parse(new InputSource(inputStream));
			return questions;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err
					.println("It was not possible to load any question. Reason is: "
							+ e.getMessage());

			return Collections.emptyList();
		}
	}

}
