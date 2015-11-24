package com.jiahaoliuliu.simplepubnubtest.model;

/**
 * Created by Jiahao on 11/22/15.
 */
public class Message {

    private String messageId;

    private String senderId;

    private String messageContent;

    private long time;

    public Message() {
    }

    public Message(String messageId, String senderId, String messageContent, long time) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (time != message.time) return false;
        if (messageId != null ? !messageId.equals(message.messageId) : message.messageId != null)
            return false;
        if (senderId != null ? !senderId.equals(message.senderId) : message.senderId != null)
            return false;
        return !(messageContent != null ? !messageContent.equals(message.messageContent) : message.messageContent != null);

    }

    @Override
    public int hashCode() {
        int result = messageId != null ? messageId.hashCode() : 0;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (messageContent != null ? messageContent.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", time=" + time +
                '}';
    }
}
