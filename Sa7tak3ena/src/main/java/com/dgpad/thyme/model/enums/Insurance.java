package com.dgpad.thyme.model.enums;

public enum Insurance {
    Non("No Insurance"),
    privateInsurance("Private Insurance"),
    تامين("تامين"),
    هيئةصحية("هيئة صحية"),
    تعاونية_موظفي_الدولة("تعاونية موظفي الدولة"),
    شركات_الضمان_الرئيسية_المحلية_والدولية("شركات الضمان الرئيسية المحلية والدولية"),
    منظمات_الإنسانية("منظمات الإنسانية"),
    الضمان_الاجتماعي("الضمان الاجتماعي"),
    وزارة_الصحة("وزارة الصحة"),
    الجيش("الجيش"),
    قوى_الأمن_الداخلي("قوى الأمن الداخلي"),
    الجمارك("الجمارك"),
    الأمن_العام_وأمن_الدولة("الأمن العام وأمن الدولة"),
    بلدية_بيروت("بلدية بيروت");

    private String description;

    Insurance(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
