package com.example.junittest.chap2;

import com.example.junittest.chap2.copyed.Answer;
import com.example.junittest.chap2.copyed.Bool;
import com.example.junittest.chap2.copyed.BooleanQuestion;
import com.example.junittest.chap2.copyed.Criteria;
import com.example.junittest.chap2.copyed.Criterion;
import com.example.junittest.chap2.copyed.Weight;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @BeforeEach
    void create() {
        profile = new Profile("Bull Hockey, Inc.");
        question = new BooleanQuestion(1, "Got bonuses ? ");
        criteria = new Criteria();
    }

    @Test
    void matchAnswerFalseWhenMustMatchCriteriaNotMet() {

        criteria.add(new Criterion(new Answer(question, Bool.TRUE),
                                   Weight.MustMatch));
        profile.add(new Answer(question, Bool.FALSE));

        boolean matches = profile.matches(criteria);

        Assertions.assertThat(matches).isFalse();
    }

    @Test()
    void matchAnswerTrueForAnyDontCareCriteria() {

        criteria.add(new Criterion(new Answer(question, Bool.TRUE),
                                   Weight.DontCare));
        profile.add(new Answer(question, Bool.FALSE));

        boolean matches = profile.matches(criteria);

        Assertions.assertThat(matches).isTrue();
        Assertions.assertThat(2.32 * 3).isCloseTo(2.32d, Percentage.withPercentage(0.0005d));
//        Assertions.assertThat("asdfasd",matches)
    }
}