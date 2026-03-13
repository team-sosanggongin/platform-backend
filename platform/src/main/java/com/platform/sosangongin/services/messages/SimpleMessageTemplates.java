package com.platform.sosangongin.services.messages;

import org.springframework.stereotype.Service;

@Service
public class SimpleMessageTemplates implements MessageTemplate{
    @Override
    public String getInvitationTemplate(String fromUser, String toUser, String code) {
        return fromUser+" invite "+toUser+" to the business with code "+code;
    }
}
