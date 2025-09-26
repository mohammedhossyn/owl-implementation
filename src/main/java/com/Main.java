package com;


import owl.automaton.Automaton;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Automaton<?, ?>> automatons =
                CommandRunner.rebecaToNba("DiningPhilosophers.rebeca",
                        "DiningPhilosophers.property", null);
    }
}