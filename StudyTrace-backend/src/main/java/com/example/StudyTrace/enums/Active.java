package com.example.StudyTrace.enums;

public enum Active {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED;

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canToggleByUser() {
        return this == ACTIVE || this == INACTIVE;
    }

    public Active toggleByUser() {
        if (!canToggleByUser()) {
            throw new IllegalStateException("User cannot toggle from: " + this);
        }
        return this == ACTIVE ? INACTIVE : ACTIVE;
    }
}


