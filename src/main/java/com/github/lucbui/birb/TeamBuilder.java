package com.github.lucbui.birb;

import com.github.lucbui.birb.obj.Team;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeamBuilder {
    public static Team fromExcel(List<ValueRange> ranges) {
        String s = ranges.stream()
                .map(ParserUtils::flatten)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        System.out.println("Team: " + s);
        return null;
    }
}
