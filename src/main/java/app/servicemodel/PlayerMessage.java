package app.servicemodel;

import java.util.List;

public class PlayerMessage {
    public List<String> roles;
    public String message;
    public List<String> args;
    public List<MentionedUser> mentionedUsers;

    public static class MentionedUser {
        public String id;
        public String username;
        public List<String> roles;
    }
}
