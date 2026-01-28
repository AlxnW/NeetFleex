package com.example.neetflex.patterns.command;

import java.util.Stack;

/**
 * Invoker: Triggers command execution and potentially manages history.
 */
public class ActionInvoker {
    // Using a Stack for potential LIFO undo functionality
    private Stack<Command> commandHistory = new Stack<>();

    public void executeCommand(Command command) {
        System.out.println("[Invoker] Received command: " + command.getClass().getSimpleName());
        command.execute();
        // Push onto history stack for potential undo
        commandHistory.push(command);
        System.out.println("[Invoker] Command executed and added to history.");
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            System.out.println("[Invoker] Attempting to undo: " + lastCommand.getClass().getSimpleName());
            // Check if the command supports undo (e.g., using instanceof or reflection)
            // For now, just print a message as undo() is commented out in commands
            System.out.println("[Invoker] Calling undo method (if implemented)...");
            // lastCommand.undo(); // Uncomment if undo is implemented
            System.out.println("[Invoker] Undo attempt finished.");
        } else {
            System.out.println("[Invoker] No commands in history to undo.");
        }
    }
}