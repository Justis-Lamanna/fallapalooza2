package com.github.lucbui.birb.builder;

import com.github.lucbui.birb.ParserUtils;
import com.github.lucbui.birb.obj.TournamentRound;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoundsBuilder {
    public static TournamentRound fromExcel(List<ValueRange> ranges) {
        String s = ranges.stream()
                .map(ParserUtils::flatten)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        System.out.println("Round: " + s);
        return null;
    }
}
