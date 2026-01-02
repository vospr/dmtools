package com.github.istin.dmtools.atlassian.jira;

import com.github.istin.dmtools.atlassian.jira.model.Fields;
import com.github.istin.dmtools.atlassian.jira.model.Ticket;
import com.github.istin.dmtools.common.model.ITicket;
import com.github.istin.dmtools.common.tracker.TrackerClient;
import com.github.istin.dmtools.common.utils.PropertyReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class BasicJiraClient extends JiraClient<Ticket> {

    public static final String BASE_PATH;
    public static final String TOKEN;
    public static final String AUTH_TYPE;

    private static final boolean IS_JIRA_LOGGING_ENABLED;
    private static final boolean IS_JIRA_CLEAR_CACHE;

    private static final boolean IS_JIRA_WAIT_BEFORE_PERFORM;

    private static final Long SLEEP_TIME_REQUEST;

    private static final String[] JIRA_EXTRA_FIELDS;

    public static final String JIRA_EXTRA_FIELDS_PROJECT;

    private static final int JIRA_SEARCH_MAX_RESULTS;

    public static final String[] DEFAULT_QUERY_FIELDS = {
            Fields.SUMMARY,
            Fields.STATUS,
            Fields.ATTACHMENT,
            Fields.UPDATED,
            Fields.CREATED,
            Fields.CREATOR,
            Fields.REPORTER,
            Fields.COMPONENTS,
            Fields.ISSUETYPE,
            Fields.FIXVERSIONS,
            Fields.STORY_POINTS,
            Fields.LABELS,
            Fields.PRIORITY,
            Fields.PARENT
    };
    public static final String[] EXTENDED_QUERY_FIELDS = {
            Fields.DESCRIPTION,
            Fields.ISSUE_LINKS
    };

    static {
        PropertyReader propertyReader = new PropertyReader();
        BASE_PATH = propertyReader.getJiraBasePath();
        System.out.println("[BasicJiraClient] Static init - BASE_PATH: " + (BASE_PATH != null ? BASE_PATH : "NULL"));
        String jiraLoginPassToken = propertyReader.getJiraLoginPassToken();
        if (jiraLoginPassToken == null || jiraLoginPassToken.isEmpty()) {
            String email = propertyReader.getJiraEmail();
            String token = propertyReader.getJiraApiToken();
            System.out.println("[BasicJiraClient] Static init - Email: " + (email != null ? email : "NULL") + ", Token present: " + (token != null ? "YES" : "NO"));
            if (email != null && token != null) {
                String credentials = email.trim() + ":" + token.trim();
                TOKEN = Base64.getEncoder().encodeToString(credentials.getBytes());
                System.out.println("[BasicJiraClient] Static init - TOKEN created from email:token (length: " + TOKEN.length() + ")");
            } else {
                TOKEN = jiraLoginPassToken;
                System.out.println("[BasicJiraClient] Static init - TOKEN from JIRA_LOGIN_PASS_TOKEN: " + (TOKEN != null ? "present" : "NULL"));
            }
        } else {
            TOKEN = jiraLoginPassToken;
            System.out.println("[BasicJiraClient] Static init - TOKEN from JIRA_LOGIN_PASS_TOKEN: " + (TOKEN != null ? "present (length: " + TOKEN.length() + ")" : "NULL"));
        }
        AUTH_TYPE = propertyReader.getJiraAuthType();
        System.out.println("[BasicJiraClient] Static init - AUTH_TYPE: " + AUTH_TYPE);
        IS_JIRA_LOGGING_ENABLED = propertyReader.isJiraLoggingEnabled();
        IS_JIRA_CLEAR_CACHE = propertyReader.isJiraClearCache();
        IS_JIRA_WAIT_BEFORE_PERFORM = propertyReader.isJiraWaitBeforePerform();
        SLEEP_TIME_REQUEST = propertyReader.getSleepTimeRequest();
        JIRA_EXTRA_FIELDS = propertyReader.getJiraExtraFields();
        JIRA_EXTRA_FIELDS_PROJECT = propertyReader.getJiraExtraFieldsProject();
        JIRA_SEARCH_MAX_RESULTS = propertyReader.getJiraMaxSearchResults();
    }


    private static BasicJiraClient instance;
    private final String[] defaultJiraFields;
    private final String[] extendedJiraFields;
    private final String[] customCodesOfConfigFields;

    public static TrackerClient<? extends ITicket> getInstance() throws IOException {
        if (instance == null) {
            if (BASE_PATH == null || BASE_PATH.isEmpty()) {
                return null;
            }
            instance = new BasicJiraClient();
        }
        return instance;
    }

    public BasicJiraClient() throws IOException {
        super(BASE_PATH, TOKEN, JIRA_SEARCH_MAX_RESULTS);
        
        // #region agent log
        try {
            String logLine = String.format("{\"timestamp\":%d,\"location\":\"BasicJiraClient.java:101\",\"message\":\"Constructor after super\",\"data\":{\"basePath\":%s,\"tokenPresent\":%s,\"tokenLength\":%d,\"hypothesisId\":\"C,D\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\"}\n", System.currentTimeMillis(), BASE_PATH != null ? "\"" + BASE_PATH + "\"" : "null", TOKEN != null ? "true" : "false", TOKEN != null ? TOKEN.length() : 0);
            Files.write(Paths.get("c:\\.cursor\\debug.log"), logLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion agent log
        
        if (AUTH_TYPE != null) {
            // #region agent log
            try {
                String logLine = String.format("{\"timestamp\":%d,\"location\":\"BasicJiraClient.java:114\",\"message\":\"Setting AUTH_TYPE\",\"data\":{\"authType\":%s,\"hypothesisId\":\"D\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\"}\n", System.currentTimeMillis(), AUTH_TYPE != null ? "\"" + AUTH_TYPE + "\"" : "null");
                Files.write(Paths.get("c:/.cursor/debug.log"), logLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) { e.printStackTrace(); }
            // #endregion agent log
            
            // Ensure AUTH_TYPE is capitalized (HTTP standard requires "Basic" not "basic")
            String normalizedAuthType = AUTH_TYPE.trim();
            if (normalizedAuthType.equalsIgnoreCase("basic")) {
                normalizedAuthType = "Basic";
            }
            setAuthType(normalizedAuthType);
            
            // #region agent log
            try {
                String logLine = String.format("{\"timestamp\":%d,\"location\":\"BasicJiraClient.java:123\",\"message\":\"AUTH_TYPE normalized\",\"data\":{\"original\":%s,\"normalized\":%s,\"hypothesisId\":\"D\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\"}\n", System.currentTimeMillis(), AUTH_TYPE != null ? "\"" + AUTH_TYPE + "\"" : "null", normalizedAuthType != null ? "\"" + normalizedAuthType + "\"" : "null");
                Files.write(Paths.get("c:/.cursor/debug.log"), logLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) { e.printStackTrace(); }
            // #endregion agent log
        }
        setLogEnabled(IS_JIRA_LOGGING_ENABLED);
        setWaitBeforePerform(IS_JIRA_WAIT_BEFORE_PERFORM);
        setSleepTimeRequest(SLEEP_TIME_REQUEST);
        setClearCache(IS_JIRA_CLEAR_CACHE);

        List<String> defaultFields = new ArrayList<>(Arrays.asList(DEFAULT_QUERY_FIELDS));

        if (JIRA_EXTRA_FIELDS_PROJECT != null && JIRA_EXTRA_FIELDS != null) {
            customCodesOfConfigFields = new String[JIRA_EXTRA_FIELDS.length];
            for (int i = 0; i < JIRA_EXTRA_FIELDS.length; i++) {
                String extraField = JIRA_EXTRA_FIELDS[i];
                String fieldCustomCode = getFieldCustomCode(JIRA_EXTRA_FIELDS_PROJECT, extraField);
                customCodesOfConfigFields[i] = fieldCustomCode;
                defaultFields.add(fieldCustomCode);
            }
        } else {
            customCodesOfConfigFields = null;
        }

        defaultJiraFields = defaultFields.toArray(new String[0]);

        List<String> extendedFields = new ArrayList<>();
        extendedFields.addAll(Arrays.asList(EXTENDED_QUERY_FIELDS));
        extendedFields.addAll(defaultFields);

        extendedJiraFields = extendedFields.toArray(new String[0]);
    }



    public String getTextFieldsOnly(ITicket ticket) {
        StringBuilder ticketDescription = null;
        try {
            ticketDescription = new StringBuilder(ticket.getTicketTitle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ticketDescription.append("\n").append(ticket.getTicketDescription());
        if (customCodesOfConfigFields != null) {
            for (String customField : customCodesOfConfigFields) {
                if (customField != null) {
                    String value = ticket.getFields().getString(customField);
                    if (value != null) {
                        ticketDescription.append("\n").append(value);
                    }
                }
            }
        }
        return ticketDescription.toString();
    }

    @Override
    public void deleteCommentIfExists(String ticketKey, String comment) throws IOException {

    }

    @Override
    public String[] getDefaultQueryFields() {
        return defaultJiraFields;
    }

    @Override
    public String[] getExtendedQueryFields() {
        return extendedJiraFields;
    }

    @Override
    public List<? extends ITicket> getTestCases(ITicket ticket) throws IOException {
        return Collections.emptyList();
    }

    @Override
    public TextType getTextType() {
        return TextType.MARKDOWN;
    }
}
