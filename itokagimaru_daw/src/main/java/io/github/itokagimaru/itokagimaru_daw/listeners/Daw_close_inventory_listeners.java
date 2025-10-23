package io.github.itokagimaru.itokagimaru_daw.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;

public class Daw_close_inventory_listeners implements Listener {
    @EventHandler
    public void Daw_close_inventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (e.getView().title().equals(Component.text("§b打ち込みモード"))) {//打ち込みモードを閉じたことを検知
            Itokagimaru_daw.music music = new Itokagimaru_daw.music();
            Itokagimaru_daw.inventory_save inventory_lode = new Itokagimaru_daw.inventory_save();
            int[] loded_music = music.load_Music(p);
            int music_end_point = 0;
            for (int i = loded_music.length; i > 0 ; i--) {
                if (loded_music[i -1] == -1) loded_music[i -1] = 0;
                if (loded_music[i -1] != 0) {
                    music_end_point = i;
                    break;
                }
            }
            loded_music[music_end_point] = -1;
            music.saveMusic(p, loded_music);
            inventory_lode.loadInventory(p);
        }else if (e.getView().title().equals(Component.text("§b再生モード"))){
            Itokagimaru_daw.operation_playing playing = new Itokagimaru_daw.operation_playing();
            Itokagimaru_daw.play play = playing.get_playing(p);
            play.stop_task();
        }
    }
}
