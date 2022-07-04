package org.oopscraft.apps.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WebConfig {

    private List<String> locales = new ArrayList<>();

    private String secretKey;

    private List<String> defaultAuthorities = new ArrayList<>();

}
