package com.webquiz.contact.validate;

import com.webquiz.contact.enums.QuestionType;

import java.util.List;

public class QuestionValidator {

    public static void validate(QuestionType type,
                                List<?> options,
                                String correctAnswer,
                                List<String> correctAnswerKeys) {
        switch (type) {
            case SINGLE_CHOICE -> {
                if (options == null || options.isEmpty())
                    throw new RuntimeException("Phải có danh sách đáp án");

                if (correctAnswer == null || correctAnswer.isBlank())
                    throw new RuntimeException("Phải có đáp án đúng");
            }
            case MULTIPLE_CHOICE -> {
                if (options == null || options.isEmpty())
                    throw new RuntimeException("Phải có danh sách đáp án");

                if (correctAnswerKeys == null || correctAnswerKeys.size() < 2)
                    throw new RuntimeException("Phải có ít nhất 2 đáp án đúng");
            }
            case TRUE_FALSE -> {
                if (correctAnswer == null ||
                    (!correctAnswer.equalsIgnoreCase("A") &&
                     !correctAnswer.equalsIgnoreCase("B"))) {

                    throw new RuntimeException("Chỉ 2 đáp án A và B");
                }
            }
        }
    }
}