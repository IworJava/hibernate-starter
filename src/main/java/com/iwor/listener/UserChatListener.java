package com.iwor.listener;

import com.iwor.entity.UserChat;

import javax.persistence.PostRemove;
import javax.persistence.PrePersist;

public class UserChatListener {

    @PrePersist
    public void postPersist(UserChat entity) {
        var chat = entity.getChat();
        chat.setCount(chat.getCount() + 1);
    }

    @PostRemove
    public void postRemove(UserChat entity) {
        var chat = entity.getChat();
        chat.setCount(chat.getCount() - 1);
    }
}
