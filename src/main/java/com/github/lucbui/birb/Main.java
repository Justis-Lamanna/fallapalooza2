package com.github.lucbui.birb;

import com.github.lucbui.birb.builder.BracketBuilder;
import com.github.lucbui.birb.config.ConfigService;
import com.github.lucbui.birb.config.FallapaloozaConfig;
import com.github.lucbui.birb.obj.Bracket;
import com.github.lucbui.birb.obj.Team;
import com.github.lucbui.birb.obj.TournamentRound;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String APPLICATION_NAME = "Secret Birb Project Mk III";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static final String DEFAULT_SPREADSHEET = "1j12zbCvJal-W-ZpTdwoD8nyGafMwVJ_iqM1cMM5F5DQ";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        Parser parser = new Parser();
        Saver saver = new Saver();

        Sheets sheets = createSheets();

        try {
            System.out.println("---Pulling Team Data---");
            List<Team> teams = parser.getTeams(sheets);
            System.out.println("---Pulling Bracket Data---");
            List<TournamentRound> rounds = parser.getTournamentRounds(sheets);
            System.out.println("---Outputting to Files---");
            Bracket bracket = BracketBuilder.process(teams, rounds);
            bracket.getRounds().forEach(round -> {
                System.out.println("----" + round.getName() + "----");
                round.getMatchups().forEach(System.out::println);
            });

            saver.initialize();
            saver.save(teams);
            saver.save(bracket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Sheets createSheets() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH))
                .createScoped(SCOPES);
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
