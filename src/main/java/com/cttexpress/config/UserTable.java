package com.cttexpress.config;

import com.cttexpress.persistence.ApiClientItem;

import java.util.HashMap;
import java.util.Map;

public class UserTable {

    protected static final HashMap<String , User> USERS_HM = new HashMap<String , User>() {{
        put("admin", new User("admin", "changeit", "ADMINISTRATOR"));
        put("devel1", new User("devel1", "changeit", "SYSTEM_USER"));
    }};

    public static HashMap<String, User> getUsersHM() {
        return USERS_HM;
    }
}
