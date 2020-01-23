package land.pvp.core.utils.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClickableMessage {
    private final List<TextComponent> components = new ArrayList<>();
    private TextComponent current;

    public ClickableMessage(String msg) {
        add(msg);
    }

    public ClickableMessage add(String msg) {
        TextComponent component = new TextComponent(msg);
        components.add(component);
        current = component;
        return this;
    }

    private void hover(TextComponent component, String msg) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(msg).create()));
    }

    public ClickableMessage hover(String msg) {
        hover(current, msg);
        return this;
    }

    public ClickableMessage hoverAll(String msg) {
        for (TextComponent component : components) {
            hover(component, msg);
        }
        return this;
    }

    private void command(TextComponent component, String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    public ClickableMessage command(String command) {
        command(current, command);
        return this;
    }

    public ClickableMessage commandAll(String command) {
        for (TextComponent component : components) {
            command(component, command);
        }
        return this;
    }

    public ClickableMessage color(String color) {
        current.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.charAt(1)));
        return this;
    }

    public ClickableMessage color(ChatColor color) {
        current.setColor(color.asBungee());
        return this;
    }

    public ClickableMessage style(ChatColor color) {
        switch (color) {
            case UNDERLINE:
                current.setUnderlined(true);
                break;
            case BOLD:
                current.setBold(true);
                break;
            case ITALIC:
                current.setItalic(true);
                break;
            case MAGIC:
                current.setObfuscated(true);
                break;
        }
        return this;
    }

    public void sendToPlayer(Player player, boolean recording) {
        player.sendMessage(recording, components.toArray(new BaseComponent[0]));
    }

    public void sendToPlayer(Player player) {
        sendToPlayer(player, false);
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::sendToPlayer);
        CommandSender console = Bukkit.getConsoleSender();
        String msg = String.join("", components.stream().map(TextComponent::getText).collect(Collectors.toList()));
        console.sendMessage(msg);
    }
}
