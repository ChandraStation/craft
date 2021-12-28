package com.crafteconomy.blockchain.commands.subcommands;

import java.util.UUID;

import com.crafteconomy.blockchain.commands.SubCommand;
import com.crafteconomy.blockchain.core.request.BlockchainRequest;
import com.crafteconomy.blockchain.utils.Util;
import com.crafteconomy.blockchain.wallets.WalletManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletBalance implements SubCommand {

    WalletManager walletManager = WalletManager.getInstance();

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String username = null;
        UUID uuid = null;

        // set UUID to user by default.
        if(sender instanceof Player) {
            Player player = (Player) sender;
            uuid = player.getUniqueId();
            username = player.getName();
        }

        // if they request another players address, get their offline UUID if they joined before
        if(args.length >= 2) {            
            Player player = Bukkit.getPlayer(args[1]);

            if(player == null) {
                // Player not found, get offline player info if they are cached
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[1]);
                if(offlinePlayer != null) {
                    uuid = offlinePlayer.getUniqueId();
                    username = offlinePlayer.getName();
                }
            } else {
                uuid = player.getUniqueId();
                username = player.getName();
            }            
        }

        String wallet;

        if(uuid == null) {
            String output = "&c&lERROR: &f&lInvalid wallet address.";
            if(args.length >= 2) { 
                output = getWalletBalanceOutput(args[1]);
            } 

            Util.colorMsg(sender, output);

        } else {
            wallet = walletManager.getAddress(uuid);

            Util.colorMsg(sender, getPlayerBalanceOutput(wallet, username));
            if(wallet != null) {
                Util.clickableWallet(sender, wallet, "&7&oWallet: &n%wallet%");
            }
            
        }
        
    }

    private String getPlayerBalanceOutput(String wallet, String otherUser){        
        if(wallet == null) {
            return "&c"+otherUser+" does not have a wallet set!";
        }

        long amount = BlockchainRequest.getBalance(wallet);

        if(otherUser != null) {
            return otherUser + " has " + amount + "craft";
        } else {
            return "You have " + amount + "craft";
        }
    }

    private String getWalletBalanceOutput(String wallet){
        if(!wallet.startsWith("craft")){
            return "";
        }

        long amount = BlockchainRequest.getBalance(wallet);
        return wallet + " has " + amount + "craft";
    }
    
}
