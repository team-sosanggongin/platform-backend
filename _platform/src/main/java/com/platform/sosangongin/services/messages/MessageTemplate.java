package com.platform.sosangongin.services.messages;

public interface MessageTemplate {
    String getInvitationTemplate(String fromUser, String toUser, String code);
}
