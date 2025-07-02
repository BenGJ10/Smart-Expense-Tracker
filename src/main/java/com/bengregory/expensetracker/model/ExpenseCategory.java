package com.bengregory.expensetracker.model;

public enum ExpenseCategory {
    FOOD("Food"),
    EDUCATIONAL("Educational"),
    TRANSPORT("Transport"),
    ENTERTAINMENT("Entertainment"),
    UTILITIES("Utilities"),
    SHOPPING("Shopping"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}