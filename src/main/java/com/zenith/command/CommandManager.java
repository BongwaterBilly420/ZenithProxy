package com.zenith.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.zenith.command.impl.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.zenith.Shared.CONFIG;
import static com.zenith.Shared.saveConfig;
import static java.util.Arrays.asList;

@Getter
public class CommandManager {
    private final Object2ObjectOpenHashMap<Class<? extends Command>, Command> commandsClassMap = new Object2ObjectOpenHashMap<>();
    private final CommandDispatcher<CommandContext> dispatcher;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        init();
    }

    public void init() {
       asList(
           new ActiveHoursCommand(),
           new AntiAFKCommand(),
           new AutoDisconnectCommand(),
           new AutoEatCommand(),
           new AutoReconnectCommand(),
           new AutoReplyCommand(),
           new AutoRespawnCommand(),
           new AutoTotemCommand(),
           new AutoUpdateCommand(),
           new ChatRelayCommand(),
           new ConnectCommand(),
           new DatabaseCommand(),
           new DebugCommand(),
           new DisconnectCommand(),
           new DisplayCoordsCommand(),
           new ExtraChatCommand(),
           new HelpCommand(),
           new IgnoreCommand(),
           new KickCommand(),
           new KillAuraCommand(),
           new PrioCommand(),
           new ProxyClientConnectionCommand(),
           new QueueWarningCommand(),
           new ReconnectCommand(),
           new ReleaseChannelCommand(),
           new RespawnCommand(),
           new SendMessageCommand(),
           new ServerCommand(),
           new SpammerCommand(),
           new SpectatorCommand(),
           new SpookCommand(),
           new StalkCommand(),
           new StatusCommand(),
           new TablistCommand(),
           new UpdateCommand(),
           new VisualRangeCommand(),
           new WhitelistCommand()
       ).forEach(this::addCommand);
    }

    private void addCommand(Command command) {
        commandsClassMap.put(command.getClass(), command);
        registerCommand(command);
    }

    public <T extends Command> T getCommand(Class<T> clazz) {
        return (T) commandsClassMap.get(clazz);
    }

    public List<Command> getCommands() {
        return commandsClassMap.values().stream().toList();
    }

    private void registerCommand(final Command command) {
        final LiteralCommandNode<CommandContext> node = dispatcher.register(command.register());
        command.aliases().forEach(alias -> dispatcher.register(command.redirect(alias, node)));
    }

    public void execute(final CommandContext context) {
        final ParseResults<CommandContext> parse = this.dispatcher.parse(downcaseFirstWord(context.getInput()), context);
        try {
            executeWithErrorHandler(context, parse);
        } catch (final CommandSyntaxException e) {
            // fall through
            // errors handled by delegate
            // and if this not a matching root command we want to fallback to original commands
        }
        saveConfig();
    }

    private String downcaseFirstWord(final String sentence) {
        List<String> words = asList(sentence.split(" "));
        if (words.size() > 1) {
            return words.get(0).toLowerCase() + words.stream().skip(1).collect(Collectors.joining(" ", " ", ""));
        } else {
            return sentence.toLowerCase();
        }
    }

    private int executeWithErrorHandler(final CommandContext context, final ParseResults<CommandContext> parse) throws CommandSyntaxException {
        final Optional<Function<CommandContext, Void>> errorHandler = parse.getContext().getNodes().stream().findFirst()
                .map(ParsedCommandNode::getNode)
                .filter(node -> node instanceof CaseInsensitiveLiteralCommandNode)
                .flatMap(commandNode -> ((CaseInsensitiveLiteralCommandNode<CommandContext>) commandNode).getErrorHandler());
        if (parse.getReader().canRead()) {
            errorHandler.ifPresent(handler -> handler.apply(context));
            return -1;
        }
        errorHandler.ifPresent(handler -> dispatcher.setConsumer((commandContext, success, result) -> {
            if (!success) handler.apply(context);
        }));

        return dispatcher.execute(parse);
    }

    public String getCommandPrefix(final CommandSource source) {
        // todo: tie this to each output instead because multiple outputs can be used regardless of source
        //  insert a string that gets replaced?
        //      abstract the embed builder output to a mutable intermediary?
        return source == CommandSource.DISCORD ? CONFIG.discord.prefix : "";
    }
}
