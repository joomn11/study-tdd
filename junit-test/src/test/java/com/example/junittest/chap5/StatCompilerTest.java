package com.example.junittest.chap5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StatCompilerTest {

    @Test
    void responsesByQuestionAnswersCountsByQuestionText() {
        StatCompiler stat = new StatCompiler();
        List<BooleanAnswer> answers = new ArrayList<>();
        answers.add(new BooleanAnswer(1, true));
        answers.add(new BooleanAnswer(1, true));
        answers.add(new BooleanAnswer(1, true));
        answers.add(new BooleanAnswer(1, false));
        answers.add(new BooleanAnswer(2, true));
        answers.add(new BooleanAnswer(2, true));

        Map<Integer, String> questions = new HashMap<>();
        questions.put(1, "tuition reimbursement?");
        questions.put(2, "Relocation package?");

        Map<String, Map<Boolean, AtomicInteger>> responses = stat.responsesByQuestion(answers, questions);

        Assertions.assertThat(responses.get("tuition reimbursement?").get(Boolean.TRUE).get()).isEqualTo(3);
        Assertions.assertThat(responses.get("tuition reimbursement?").get(Boolean.FALSE).get()).isEqualTo(1);
        Assertions.assertThat(responses.get("Relocation package?").get(Boolean.TRUE).get()).isEqualTo(2);
        Assertions.assertThat(responses.get("Relocation package?").get(Boolean.FALSE).get()).isEqualTo(0);


    }
}