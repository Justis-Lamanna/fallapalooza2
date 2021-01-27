package com.github.lucbui.birb.obj;

public class Player {
    private final String name;
    private final String nameWithPronouns;

    public Player(String name, String nameWithPronouns) {
        this.name = name;
        this.nameWithPronouns = nameWithPronouns;
    }

    public String getName() {
        return name;
    }

    public String getNameWithPronouns() {
        return nameWithPronouns;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", nameWithPronouns='" + nameWithPronouns + '\'' +
                '}';
    }
}
