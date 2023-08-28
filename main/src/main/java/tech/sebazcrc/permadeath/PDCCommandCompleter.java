package tech.sebazcrc.permadeath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class PDCCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> subcommands = new ArrayList<>();

        if (args.length == 1) {

            subcommands.add("dias");
            subcommands.add("duracion");
            subcommands.add("awake");
            subcommands.add("info");
            subcommands.add("discord");
            subcommands.add("mensaje");

            if (sender.hasPermission("permadeathcore.cambiardia")) {
                subcommands.add("cambiardia");
            }
            if (sender.hasPermission("permadeathcore.reload")) {

                subcommands.add("reload");
            }

            if (sender.hasPermission("permadeathcore.give")) {

                subcommands.add("give");
            }

            if (sender.hasPermission("permadeathcore.locate")) {

                subcommands.add("locate");
            }

            if (sender.hasPermission("permadeathcore.admin")) {
                subcommands.add("storm");
                subcommands.add("afk");
            }
        }

        if (args.length == 2) {

            if (args[1].equalsIgnoreCase("give")) {
                subcommands.add("netheriteArmor");
                subcommands.add("infernalArmor");
                subcommands.add("medalla");
                subcommands.add("netheriteTools");
                subcommands.add("infernalBlock");
            }

            if (args[1].equalsIgnoreCase("locate")) {

                subcommands.add("beginning");
            }

            if (args[1].equalsIgnoreCase("cambiardia")) {

                subcommands.add("<día>");
            }
        }

        return subcommands;
    }
}