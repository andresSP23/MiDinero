package com.ansicode.Midinero.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");

    private  final String template;

    EmailTemplateName(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

}
