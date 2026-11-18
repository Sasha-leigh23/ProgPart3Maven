package com.mycompany.progpart3maven;

import java.util.UUID;

public class Messages {
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;
    
    // Part 3 Update: Added flag attribute to track message status ("Sent", "Stored", "Disregard") 
    private final String flag;

    public Messages(int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = "Sent";
        this.messageId = createTenDigitId();
        this.messageHash = createMessageHash();
    }

    // Part 3 Update: Overloaded constructor to support explicit flag assignment from test data
    public Messages(int messageNumber, String recipient, String messageText, String flag) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = flag;
        this.messageId = createTenDigitId();
        this.messageHash = createMessageHash();
    }

    // Creates a message with a given ID (used by the unit tests so the hash can be checked)
    public Messages(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = "Sent";
        this.messageId = (messageId != null && messageId.matches("\\d{10}")) ? messageId : createTenDigitId();
        this.messageHash = createMessageHash();
    }

    // Generates a random 10-digit ID string (Fixed to strictly guarantee numeric length)
    private String createTenDigitId() {
        String source = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        while (source.length() < 10) {
            source += UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        }
        return source.substring(0, 10);
    }

    public boolean checkMessageID() {
        return messageId != null && messageId.matches("\\d{10}");
    }

    // Returns the message shown when the message ID has been generated
    public String messageIdStatus() {
        return "Message ID generated: " + messageId;
    }

    // Practical format check for South African numbers (+27 followed by 9 digits)
    public String checkRecipientCell() {
        if (recipient != null && (recipient.matches("\\+27[0-9]{9}") || recipient.matches("[0-9]{10}"))) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Validates message length against the 250-character limit
    public String checkMessageLength() {
        if (messageText == null) {
            return "Please enter a message.";
        }
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (messageText.length() - 250) + "; please reduce the size.";
    }

    // Builds the message hash: ID prefix, message number, and first/last words in uppercase
    public String createMessageHash() {
        if (messageText == null || messageText.trim().isEmpty()) {
            return messageId.substring(0, Math.min(2, messageId.length())) + ":" + messageNumber + ":";
        }
        String[] words = messageText.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);

        String prefix = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        return prefix + ":" + messageNumber + ":" + (firstWord + lastWord).toUpperCase();
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // Fixed: Now correctly maps test options 1, 2, and 3 to match unit assertions
    public String SentMessage(int par) {
        if (par == 1) return "Message successfully sent";
        if (par == 2) return "Press 0 to delete the message";
        if (par == 3) return "Message successfully stored";
        return "Choose Send Message, Disregard Message, or Store Message.";
    }

    public String printMessages() {
        return "Message ID: " + messageId + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient  + "\n" +
               "Message: " + messageText  + "\n" +
               "Flag: " + flag;
    }

    // Fixed: Properly returns the count parameter instead of hardcoded 1
    public int returnTotalMessagess(int count) {
        return count;
    }

    // Formats message data into a JSON string format (Fixed: Added missing commas between keys)
    public String toJson() {
        return "{\"messageId\":\"" + jsonEscape(messageId) + "\"," +
               "\"messageHash\":\"" + jsonEscape(messageHash) + "\"," +
               "\"recipient\":\"" + jsonEscape(recipient) + "\"," +
               "\"message\":\"" + jsonEscape(messageText) + "\"," +
               "\"flag\":\"" + jsonEscape(flag) + "\"}";
    }

    private String jsonEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getFlag() { return flag; }

    // Same as getMessageId() 
    public String getMessageID() { return messageId; }
}