package com.zenith.discord.command;

import com.mojang.brigadier.context.CommandContext;

@FunctionalInterface
public interface IExecutes<S> {
    void execute(CommandContext<S> context);
}
