package de.DevsWithoutHobbies.BungeeTest;

import net.md_5.bungee.api.plugin.Plugin;

class Main extends Plugin {

    @Override
    public void onEnable() {
        getLogger().info("Yay! It loads!");
    }
}
