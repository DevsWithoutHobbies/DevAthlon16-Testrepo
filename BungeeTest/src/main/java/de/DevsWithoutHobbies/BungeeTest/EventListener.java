package de.DevsWithoutHobbies.BungeeTest;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("WeakerAccess")
public class EventListener implements Listener {

    @EventHandler
    public void onServerConnected(final ServerSwitchEvent event) {
        event.getPlayer().sendMessage(new TextComponent(ChatColor.GREEN + "You switched the server"));

        TextComponent message = new TextComponent( "Click me" );
        message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
        message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Goto the Spigot website!").create() ) );
        event.getPlayer().sendMessage( message );
    }

    /*@EventHandler
    public void onServerConnected(final  event) {
        event.getPlayer().sendMessage(new TextComponent(ChatColor.GREEN + "You switched the server"));
    }*/
}
