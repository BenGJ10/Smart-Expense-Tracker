package com.bengregory.expensetracker.model;

public enum IncomeSource {
    SALARY("Salary"),
    FREELANCE("Freelance"),
    INVESTMENTS("Investments"),
    GIFTS("Gifts"),
    OTHER("Other");

    private final String displayName;

    IncomeSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}