package it.polimi.gma.entities;

public enum Section {
    MARKETING(true),
    STATISTICAL(false);

    private final boolean mandatory;

    Section(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
