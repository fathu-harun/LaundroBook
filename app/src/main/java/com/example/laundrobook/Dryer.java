package com.example.laundrobook;

public class Dryer implements Machine{
    private final String name;

    public Dryer(String name) {
        this.name = name;
    }

    @Override
    public String getMachineName() {
        return this.name;
    }
}







