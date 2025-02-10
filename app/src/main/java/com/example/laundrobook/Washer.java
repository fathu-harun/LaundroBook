package com.example.laundrobook;

public class Washer implements Machine{
    private final String name;

    public Washer(String name) {
        this.name = name;
    }

    @Override
    public String getMachineName() {
        return this.name;
    }
}








