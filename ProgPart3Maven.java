package com.mycompany.progpart3maven;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProgPart3Maven {

    private static final List<Messages> SENT_MESSAGES = new ArrayList<>();
    
    // Part 3 Update: Added arrays for Disregarded, Stored messages, Message Hashes, and Message IDs
    private static final List<Messages> DISREGARDED_MESSAGES = new ArrayList<>();
    private static final List<Messages> STORED_MESSAGES = new ArrayList<>();
    private static final List<String> MESSAGE_HASHES = new ArrayList<>();
    private static final List<String> MESSAGE_IDS = new ArrayList<>();
    
    private static int sentCount = 0;
    private static int processedCount = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // 1. First Name and Last Name
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine().trim();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine().trim();

        System.out.println("Welcome " + firstName + " " + lastName
                + ", it's good to see you again!");

        // 2. Username Input (Up to 2 attempts)
        String username = "";

        for (int attempt = 1; attempt <= 2; attempt++) {
            System.out.print(attempt == 1
                    ? "Enter your username: "
                    : "Enter your username again: ");

            username = input.nextLine().trim();

            LogIn tempCheck = new LogIn(
                    firstName, lastName, username, "", "+27000000000");

            if (tempCheck.checkUsername()) {
                System.out.println("Username successfully captured.");
                break;
            } else {
                System.out.println("Username is not correctly formatted; "
                        + "please ensure that your username contains an "
                        + "underscore and is no more than 5 characters long.");
            }
        }

        // 3. Password Input (Up to 2 attempts)
        String password = "";

        for (int attempt = 1; attempt <= 2; attempt++) {
            System.out.print(attempt == 1
                    ? "Enter your password: "
                    : "Enter your password again: ");

            password = input.nextLine().trim();

            LogIn tempCheck = new LogIn(
                    firstName, lastName, username, password, "+27000000000");

            if (tempCheck.checkPasswordComplexity()) {
                System.out.println("Password successfully captured.");
                break;
            } else {
                System.out.println("Password is not correctly formatted; "
                        + "please ensure that it contains at least 8 "
                        + "characters, a capital letter, a number, "
                        + "and a special character.");
            }
        }

        // 4. Cell Number Input (Up to 2 attempts)
        String cellnumber = "";

        for (int attempt = 1; attempt <= 2; attempt++) {
            System.out.print(attempt == 1
                    ? "Enter your cell number: "
                    : "Enter your cell number again: ");

            cellnumber = input.nextLine().trim();

            LogIn tempCheck = new LogIn(
                    firstName, lastName, username, password, cellnumber);

            if (tempCheck.checkCellphoneNumber()) {
                System.out.println("Cell number successfully captured.");
                break;
            } else {
                System.out.println("Cell number is incorrectly formatted "
                        + "or does not contain an international code. "
                        + "Please correct the number and try again.");
            }
        }

        // Create the user account
        LogIn user = new LogIn(
                firstName, lastName, username, password, cellnumber);

        // 5. Login
        System.out.println("LOGIN");

        System.out.print("Enter username to login: ");
        String loginUser = input.nextLine().trim();

        System.out.print("Enter password to login: ");
        String loginPass = input.nextLine().trim();

        boolean success = user.loginUser(loginUser, loginPass);

        System.out.println(user.returnLoginStatus(success));

        // 6. QuickChat Menu
        if (success) {
            System.out.println("Welcome to QuickChat.");

            System.out.print("How many messages would you like to enter? ");
            int messageLimit = readPositiveInteger(input);

            boolean running = true;

            while (running) {
                //Part 3 Update: Added 4th menu option for Stored Messages & Reports 
                System.out.println("QUICKCHAT MENU");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Stored Messages & Reports");
                System.out.println("4) Quit");
                System.out.print("Choose an option (1-4): ");

                String option = input.nextLine().trim();

                switch (option) {
                    case "1":
                        sendMessages(input, messageLimit);
                        break;

                    case "2":
                        if (SENT_MESSAGES.isEmpty()) {
                            System.out.println("No messages have been sent.");
                        } else {
                            System.out.println("Recently sent messages:");

                            for (Messages message : SENT_MESSAGES) {
                                System.out.println(message.printMessages());
                            }
                        }
                        break;

                    case "3":
                        // === PART 3 UPDATE: Sub-menu option handler for stored messages and reports (a to f) ===
                        handleStoredMessagesMenu(input);
                        break;

                    case "4":
                        running = false;
                        System.out.println("Exiting QuickChat.");
                        break;

                    default:
                        System.out.println(
                                "Invalid option. Please enter 1, 2, 3, or 4.");
                }
            }

            System.out.println(
                    "Total messages successfully sent: " + sentCount);
            System.out.println(
                    "Total messages processed: " + processedCount);
        }

        input.close();
    }

    // Read a positive whole number
    private static int readPositiveInteger(Scanner input) {
        while (true) {
            String value = input.nextLine().trim();

            try {
                int number = Integer.parseInt(value);

                if (number > 0) {
                    return number;
                }

                System.out.print(
                        "Enter a number greater than zero: ");

            } catch (NumberFormatException ex) {
                System.out.print(
                        "That is not a whole number. Try again: ");
            }
        }
    }

    // Send, disregard, or store messages
    private static void sendMessages(Scanner input, int messageLimit) {
        if (processedCount >= messageLimit) {
            System.out.println(
                    "You have reached the message limit you selected.");
            return;
        }

        while (processedCount < messageLimit) {
            int messageNumber = processedCount + 1;

            System.out.println(" Message " + messageNumber
                    + " of " + messageLimit + " ---");

            // Validate recipient
            System.out.print(
                    "Enter recipient in international format "
                    + "(example +27718693002): ");

            String recipient = input.nextLine().trim();

            Messages draftForValidation = new Messages(
                    messageNumber, recipient, "placeholder");

            String recipientStatus =
                    draftForValidation.checkRecipientCell();

            System.out.println(recipientStatus);

            if (!recipientStatus.startsWith(
                    "Cell phone number successfully")) {
                System.out.println("Please enter a valid recipient.");
                continue;
            }

            // Read message text
            System.out.print(
                    "Enter your message (maximum 250 characters): ");

            String text = input.nextLine();

            Messages message = new Messages(
                    messageNumber, recipient, text);

            String lengthStatus = message.checkMessageLength();

            System.out.println(lengthStatus);

            if (!lengthStatus.equals("Message ready to send.")) {
                System.out.println(
                        "Please enter a message within the allowed length.");
                continue;
            }

            // Choose message action
            System.out.println("Choose what to do with this message:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Your choice: ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    //Part 3 Update: Flagged and added to Sent Messages array + Hash and ID collections
                    Messages sentMsg = new Messages(messageNumber, recipient, text, "Sent");
                    SENT_MESSAGES.add(sentMsg);
                    MESSAGE_HASHES.add(sentMsg.getMessageHash());
                    MESSAGE_IDS.add(sentMsg.getMessageId());
                    sentCount++;
                    processedCount++;

                    System.out.println("Message successfully sent.");
                    System.out.println(sentMsg.printMessages());
                    break;

                case "2":
                    System.out.print(
                            "Enter 0 to confirm disregarding the message: ");

                    String confirm = input.nextLine().trim();

                    if (confirm.equals("0")) {
                        // Part 3 Update: Flagged and added to Disregarded Messages array 
                        Messages disMsg = new Messages(messageNumber, recipient, text, "Disregard");
                        DISREGARDED_MESSAGES.add(disMsg);
                        processedCount++;
                        System.out.println("Message disregarded.");
                    } else {
                        System.out.println(
                                "Disregard cancelled. Message not processed.");
                    }
                    break;

                case "3":
                    try {
                        //Part 3 Update: Flagged and added to Stored Messages array, Hash/ID lists, and JSON storage 
                        Messages storeMsg = new Messages(messageNumber, recipient, text, "Stored");
                        STORED_MESSAGES.add(storeMsg);
                        MESSAGE_HASHES.add(storeMsg.getMessageHash());
                        MESSAGE_IDS.add(storeMsg.getMessageId());
                        storeMessage(storeMsg);
                        processedCount++;

                        System.out.println("Message successfully stored.");
                        System.out.println(storeMsg.printMessages());

                    } catch (IOException ex) {
                        System.out.println(
                                "The message could not be stored: "
                                + ex.getMessage());
                    }
                    break;

                default:
                    System.out.println(
                            "Invalid message option. Please try again.");
                    break;
            }

            // Return to menu after processing one message
            return;
        }

        System.out.println(
                "You have reached the message limit you selected.");
    }

    //Part 3 Update: Stored Messages Sub-menu Handler 
    private static void handleStoredMessagesMenu(Scanner input) {
        System.out.println("\n--- STORED MESSAGES & REPORTS ---");
        System.out.println("a) Display sender and recipient of all stored messages");
        System.out.println("b) Display the longest stored/sent message");
        System.out.println("c) Search for a message ID");
        System.out.println("d) Search messages by recipient");
        System.out.println("e) Delete a message using message hash");
        System.out.println("f) Display full report of all stored messages");
        System.out.print("Select an option (a-f): ");

        String subChoice = input.nextLine().trim().toLowerCase();

        switch (subChoice) {
            case "a":
                System.out.println("\n--- Sender & Recipient of Stored Messages ---");
                if (STORED_MESSAGES.isEmpty()) {
                    System.out.println("No stored messages found.");
                } else {
                    for (Messages m : STORED_MESSAGES) {
                        System.out.println("Recipient: " + m.getRecipient() + " | Message: " + m.getMessageText());
                    }
                }
                break;

            case "b":
                System.out.println("\n--- Longest Message ---");
                List<Messages> allMessages = new ArrayList<>();
                allMessages.addAll(SENT_MESSAGES);
                allMessages.addAll(STORED_MESSAGES);
                allMessages.addAll(DISREGARDED_MESSAGES);
                if (allMessages.isEmpty()) {
                    System.out.println("No messages available.");
                } else {
                    Messages longest = allMessages.get(0);
                    for (Messages m : allMessages) {
                        if (m.getMessageText().length() > longest.getMessageText().length()) {
                            longest = m;
                        }
                    }
                    System.out.println("Longest Message: " + longest.getMessageText());
                }
                break;

            case "c":
                System.out.print("Enter Message ID to search: ");
                String searchId = input.nextLine().trim();
                boolean foundId = false;
                for (Messages m : STORED_MESSAGES) {
                    if (m.getMessageId().equals(searchId)) {
                        System.out.println("Found -> Recipient: " + m.getRecipient() + " | Message: " + m.getMessageText());
                        foundId = true;
                    }
                }
                if (!foundId) {
                    System.out.println("Message ID not found in stored messages.");
                }
                break;

            case "d":
                System.out.print("Enter recipient number to search: ");
                String searchRecip = input.nextLine().trim();
                boolean foundRecip = false;
                for (Messages m : STORED_MESSAGES) {
                    if (m.getRecipient().equals(searchRecip)) {
                        System.out.println("Message: " + m.getMessageText());
                        foundRecip = true;
                    }
                }
                if (!foundRecip) {
                    System.out.println("No stored messages found for this recipient.");
                }
                break;

            case "e":
                System.out.print("Enter Message Hash to delete: ");
                String targetHash = input.nextLine().trim();
                boolean removed = STORED_MESSAGES.removeIf(m -> m.getMessageHash().equals(targetHash));
                MESSAGE_HASHES.remove(targetHash);
                if (removed) {
                    System.out.println("Message successfully deleted using its hash.");
                } else {
                    System.out.println("Message hash not found.");
                }
                break;

            case "f":
                System.out.println("\n--- Full Stored Messages Report ---");
                if (STORED_MESSAGES.isEmpty()) {
                    System.out.println("No stored messages to display.");
                } else {
                    for (Messages m : STORED_MESSAGES) {
                        System.out.println(m.printMessages() + "\n--------------------");
                    }
                }
                break;

            default:
                System.out.println("Invalid selection.");
        }
    }

    // Store messages in a JSON file
    private static void storeMessage(Messages message) throws IOException {
        Path file = Path.of("storedMessages.json");

        String existing = "[]";

        if (Files.exists(file)) {
            existing = Files.readString(
                    file, StandardCharsets.UTF_8).trim();
        }

        String object = message.toJson();
        String updated;

        if (existing.isEmpty() || existing.equals("[]")) {
            updated = "[" + object + "]";
        } else if (existing.endsWith("]")) {
            updated = existing.substring(0, existing.length() - 1)
                    + "," + object + "]";
        } else {
            throw new IOException(
                    "storedMessages.json is not a valid JSON array.");
        }

        Files.writeString(
                file,
                updated,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}