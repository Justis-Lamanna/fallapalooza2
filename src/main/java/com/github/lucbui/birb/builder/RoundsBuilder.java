package com.github.lucbui.birb.builder;

import com.github.lucbui.birb.ParserUtils;
import com.github.lucbui.birb.obj.TournamentMatchup;
import com.github.lucbui.birb.obj.TournamentRound;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoundsBuilder {
    public static TournamentRound fromExcel(List<ValueRange> ranges) {
        Iterator<ValueRange> iter = ranges.iterator();
        List<TournamentMatchup> matchups = new ArrayList<>();
        while(iter.hasNext()) {
            String teamOne = ParserUtils.getSingleValue(iter.next());
            String teamTwo = iter.hasNext() ? ParserUtils.getSingleValue(iter.next()) : null;
            matchups.add(new TournamentMatchup(teamOne, teamTwo));
        }
        return new TournamentRound(matchups);
    }
}
