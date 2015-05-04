package com.copetti.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TestXMLSax extends DefaultHandler {

	static boolean question;
	static boolean openTag;

	public static void main(String[] args) throws Exception {

		StringBuffer accumulator = new StringBuffer();
		Map<String, String> map = new HashMap<String, String>();

		DefaultHandler dh = new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				// TODO Auto-generated method stub
				// super.startElement(uri, localName, qName, attributes);
				if (qName.equals("question"))
					question = true;

				openTag = true;
				accumulator.setLength(0);
			}

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				// TODO Auto-generated method stub
				super.characters(ch, start, length);

				if (openTag)
					accumulator.append(ch, start, length);

			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				// TODO Auto-generated method stub
				super.endElement(uri, localName, qName);

				if (qName.equals("question")) {
					question = false;

					for (String s : map.keySet()) {
						System.out.printf("Key: %s Value: %s\n", s, map.get(s));
					}
					return;
				}

				if (question) {
					map.put(qName, accumulator.toString());
					accumulator.setLength(0);

				}

				openTag = false;
			}
		};

		SAXParserFactory spf = SAXParserFactory.newInstance();
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();
		xmlReader.setContentHandler(dh);
		xmlReader.parse(new File("./teste.xml").getAbsolutePath());

	}
}
