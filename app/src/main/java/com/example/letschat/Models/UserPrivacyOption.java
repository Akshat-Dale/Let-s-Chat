package com.example.letschat.Models;

public class UserPrivacyOption
{
    boolean DarkMode;
    boolean LastSeenHome;
    boolean PresenceStatusChat;
    boolean PresenceStatusHome;
    boolean ProfilePicture;

    public boolean isDarkMode() {
        return DarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        DarkMode = darkMode;
    }

    public boolean isLastSeenHome() {
        return LastSeenHome;
    }

    public void setLastSeenHome(boolean lastSeenHome) {
        LastSeenHome = lastSeenHome;
    }

    public boolean isPresenceStatusChat() {
        return PresenceStatusChat;
    }

    public void setPresenceStatusChat(boolean presenceStatusChat) {
        PresenceStatusChat = presenceStatusChat;
    }

    public boolean isPresenceStatusHome() {
        return PresenceStatusHome;
    }

    public void setPresenceStatusHome(boolean presenceStatusHome) {
        PresenceStatusHome = presenceStatusHome;
    }

    public boolean isProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(boolean profilePicture) {
        ProfilePicture = profilePicture;
    }
}
