package com.example.junittest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ScoreCollectionTest {

    @Test
    void answersArithmeticMeanOfTwoNumbers() {
        ScoreCollection collection = new ScoreCollection();
        collection.add(() -> 5);
        collection.add(() -> 7);

        int actualResult = collection.arithmeticMean();

        Assertions.assertThat(actualResult).isEqualTo(6);
    }
}