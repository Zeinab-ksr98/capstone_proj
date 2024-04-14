package com.dgpad.thyme.model.enums;

public enum Distracts {

    Akkar("Akkar"),

    Tripoli("Tripoli"),
    Zgharta("Zgharta"),
    Batroun("Batroun"),
    Bsharre("Bsharre"),
    Koura("El Koura"),
    Dennie("El Minieh-Dennie"),

    Baalbek("Baalbek"),
    Hermel(" El Hermel"),


    Zahle("Zahle"),
    Bekaa("West Bekaa"),
    Rashaya("Rachaya"),


    Hasbaya("Hasbaya"),
    BintJbeil("Bint Jbeil"),
    Marjeyoun("Marjeyoun"),
    Nabatieh("El Nabatieh"),
    Jezzine("Jezzine"),
    Saida("Saida"),
    Tyre("Tyre"),


    Chouf("Al Chouf"),
    Aley("Aley"),
    Baabda("Baabda"),
    Beirut("Beirut"),
    Meten("El Meten"),
    Keserwan("Kesseroune"),
    Jbeil("Jbeil");


    private String value;

    Distracts(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
