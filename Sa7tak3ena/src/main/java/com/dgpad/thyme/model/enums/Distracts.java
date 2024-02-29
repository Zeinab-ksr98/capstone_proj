package com.dgpad.thyme.model.enums;

public enum Distracts {
    Akkar("Halba"),
    Baalbek("Baalbek"),
    Hermel("Hermel"),
    Rashaya("Rashaya"),
    Zahlé("Zahlé"),
    Byblos("Byblos"),
    Keserwan("Jounieh"),
    Aley("Aley"),
    Baabda("Baabda"),
    Chouf("Beiteddine"),
    Matn("Jdeideh"),
    BintJbeil("Bint Jbeil"),
    Hasbaya("Hasbaya"),
    Marjeyoun("Marjeyoun"),
    Nabatieh("Nabatieh"),
    Batroun("Batroun"),
    Bsharri("Bsharri"),
    Koura("Koura"),
    Tripoli("Tripoli"),
    Zgharta("Zgharta"),
    Sidon("Sidon"),
    Jezzine("Jezzine"),
    Tyre("Tyre");

    private String value;

    Distracts(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
