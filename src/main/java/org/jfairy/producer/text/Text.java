/*
 * Copyright (c) 2013 Codearte
 */
package org.jfairy.producer.text;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.jfairy.producer.BaseProducer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.jfairy.producer.text.TextUtils.joinWithSpace;
import static org.apache.commons.lang3.StringUtils.*;

public class Text {

	private static final int DEFAULT_WORD_COUNT = 3;
	private static final int DEFAULT_WORD_COUNT_IN_SENTENCE = 3;
	private static final int DEFAULT_SENTENCE_COUNT = 3;
	private static final int WORD_COUNT_PRECISION_IN_SENTENCE = 6;
	private static final int SENTENCE_COUNT_PRECISION_MIN = 1;
	private static final int SENTENCE_COUNT_PRECISION_MAX = 3;

	private final TextProducer textProducer;
	private final BaseProducer baseProducer;

	private int limit = 0;

	@Inject
	public Text(TextProducer textProducer, BaseProducer baseProducer) {
		this.textProducer = textProducer;
		this.baseProducer = baseProducer;
	}

	public Text limitedTo(int limit) {
		this.limit = limit;
		return this;
	}

	public String result(String result) {
		if (limit > 0) {
			return left(result, limit);
		} else {
			return result;
		}
	}

	public String loremIpsum() {
		return result(textProducer.getLoremIpsum());
	}

	public String word() {
		return result(word(DEFAULT_WORD_COUNT));
	}

	public String word(int count) {
		return result(textProducer.cleanWords(count));
	}

	public String sentence() {
		return result(sentence(DEFAULT_WORD_COUNT_IN_SENTENCE));
	}

	public String sentence(int wordCount) {
		String randomWords = textProducer.rawWords(wordCount, WORD_COUNT_PRECISION_IN_SENTENCE);
		List<String> sentences = newArrayList();
		for (String sentence : Splitter.on(". ").split(randomWords)) {
			sentences.add(capitalize(sentence));
		}
		String sentence = capitalize(Joiner.on(". ").join(sentences));
		sentence = removeEnd(sentence, ",");
		if (!endsWith(sentence, ".")) {
			sentence += ".";
		}
		return result(sentence);
	}

	private List<String> sentences(int sentenceCount) {
		List<String> sentences = new ArrayList<String>(sentenceCount);
		for (int i = 0; i < sentenceCount; i++) {
			sentences.add(sentence());
		}
		return sentences;
	}

	public String paragraph() {
		return result(paragraph(DEFAULT_SENTENCE_COUNT));
	}

	public String paragraph(int sentenceCount) {
		return result(joinWithSpace(sentences(sentenceCount +
				baseProducer.randomBetween(SENTENCE_COUNT_PRECISION_MIN, SENTENCE_COUNT_PRECISION_MAX))));
	}

	/**
	 * Generates random string with desired length
	 * @param charsCount string length
	 * @return random string
	 */
	public String randomString(int charsCount) {
		return textProducer.randomString(charsCount);
	}
}
