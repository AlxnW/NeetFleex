package com.example.neetflex.patterns.command;

/**
 * Command Interface: Declares the execution method for all commands.
 */
public interface Command {
    /**
     * Executes the action associated with this command.
     */
    void execute();

    /**
     * Optional: Executes the reverse action to undo the command.
     */
    // void undo();
}