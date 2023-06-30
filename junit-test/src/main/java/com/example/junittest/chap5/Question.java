/***
 * Excerpted from "Pragmatic Unit Testing in Java with JUnit",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/utj2 for more book information.
 ***/
package com.example.junittest.chap5;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "Question")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Question implements Serializable, Persistable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Integer id;

    @Column
    private String text;

    @Column
    private Instant instant;

    public Question() {
    }

    public Question(String text) {
        this.text = text;
    }

    abstract public List<String> getAnswerChoices();

    abstract public boolean match(int expected, int actual);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Question #" + getId() + ": " + getText());
        getAnswerChoices().stream().forEach((choice) -> s.append("\t" + choice));
        return s.toString();
    }

    public boolean match(Answer answer) {
        return false;
    }

    public String getAnswerChoice(int i) {
        return getAnswerChoices().get(i);
    }

    public int indexOf(String matchingAnswerChoice) {
        for (int i = 0; i < getAnswerChoices().size(); i++) {
            if (getAnswerChoice(i).equals(matchingAnswerChoice)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Instant getCreateTimestamp() {
        return instant;
    }

    @Override
    public void setCreateTimestamp(Instant instant) {
        this.instant = instant;
    }
}
