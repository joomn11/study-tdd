package com.example.junittest.chap2;

import com.example.junittest.chap2.copyed.Answer;
import com.example.junittest.chap2.copyed.Criteria;
import com.example.junittest.chap2.copyed.Criterion;
import com.example.junittest.chap2.copyed.Weight;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class Profile {

    private Map<String, Answer> answers = new HashMap<>();
    private int score;
    private String name;

    public Profile(String name) {
        this.name = name;
    }

    public void add(Answer answer) {
        answers.put(answer.getQuestionText(), answer);
    }

    public boolean matches(Criteria criteria) {
        score = 0;

        boolean kill = false;
        boolean anyMatches = false;

        for (Criterion criterion : criteria) {
            Answer answer = answers.get(criterion.getAnswer().getQuestionText());
            boolean match =
                criterion.getWeight().equals(Weight.DontCare) || answer.match(criterion.getAnswer());

            if (!match && criterion.getWeight().equals(Weight.MustMatch)) {
                kill = true;
            }

            if (match) {
                score += criterion.getWeight().getValue();
            }

            anyMatches |= match;
        }

        if (kill) {
            return false;
        }
        return anyMatches;
    }
}
