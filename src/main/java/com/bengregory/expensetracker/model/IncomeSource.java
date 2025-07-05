package com.bengregory.expensetracker.model;

// Enums for Income
// Enums define a fixed set of constants, useful for fields with limited valid values

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