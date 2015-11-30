package com.interview.leah.boggle.model;

/**
 * Created by leah on 11/29/15.
 * This is a model for the dice in the board
 */
public class Die {
    String[] faces;
    String faceShowing;

    public Die(String... faces) {
        this.faces = faces;
    }

    public void roll() {
        int index = (int) (6. * Math.random());
        faceShowing = faces[index];
    }
}
