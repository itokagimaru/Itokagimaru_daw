package io.github.itokagimaru.itokagimaru_daw.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.itokagimaru.itokagimaru_daw.Itokagimaru_daw;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Daw_menu_listener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Itokagimaru_daw.open_menu openMenu = new Itokagimaru_daw.open_menu();
        Itokagimaru_daw.make_item makeItem = new Itokagimaru_daw.make_item();
        Itokagimaru_daw.music saveMusic = new Itokagimaru_daw.music();
        Itokagimaru_daw.get_key getKey = new Itokagimaru_daw.get_key();

        if (event.getView().getTitle().equals("§bだう")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = clicked.getItemMeta();
            if (clicked.getType() == Material.WRITABLE_BOOK && Objects.requireNonNull(meta.displayName()).equals(Component.text("§e打ち込みモード"))) {
                player.closeInventory();
                openMenu.daw_input_mode(player);
            } else if (clicked.getType() == Material.MUSIC_DISC_13 && Objects.requireNonNull(meta.displayName()).equals(Component.text("§e再生モード"))) {
                player.closeInventory();
                openMenu.daw_play_mode(player,null);
            } else if (clicked.getType() == Material.BARRIER && Objects.requireNonNull(meta.displayName()).equals(Component.text("§4しゅうりょう"))) {
                player.closeInventory();
            }
        } else if (event.getView().title().equals(Component.text("§b打ち込みモード"))) {

            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = clicked.getItemMeta();
            Inventory clicked_inv = event.getClickedInventory();
            Inventory pl_inv = player.getInventory();
            if (clicked.getType() == Material.BARRIER && Objects.requireNonNull(meta.displayName()).equals(Component.text("§4しゅうりょう"))) {
                player.closeInventory();
            } else if (clicked.getType() == Material.WHITE_STAINED_GLASS_PANE) {
                //▼musicを拾ってくる
                int[] lodedmusic = saveMusic.load_Music(player);
                ItemStack page_item = clicked_inv.getItem(2);
                int page = openMenu.daw_input_getPage(page_item);
                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = openMenu.daw_get_topnote(topnote_item);
                int select = 1;
                int add = 0;
                for (int i = 1; i <= 8; i++) {
                    ItemStack select_item = clicked_inv.getItem(i + 9);
                    if (Objects.requireNonNull(select_item).getType() == Material.PAPER && Objects.requireNonNull(select_item.getItemMeta().getItemModel()).equals(NamespacedKey.minecraft("select"))) {
                        select = i;
                    }
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ド/C"))) {
                    if (topnote == 0)add =0;
                    else if (1 <= topnote && topnote <= 6)add =1;
                    else if (topnote >= 7)add =2;
                    lodedmusic[(page - 1) * 8 + select - 1] = 2 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("レ/D"))){
                    if (0 <= topnote && topnote <= 5)add =0;
                    else if (topnote >= 6)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 12 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ミ/E"))){
                    if (0 <= topnote && topnote <= 4)add =0;
                    else if (topnote >= 5)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 10 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ファ/F"))){
                    if (0 <= topnote && topnote <= 4)add =0;
                    else if (topnote >= 5)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 9 + (12*add);}
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ソ/G"))){
                    if (0 <= topnote && topnote <= 3)add =0;
                    else if (topnote >= 4)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 7 + (12*add);}
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ラ/A"))){
                    if (0 <= topnote && topnote <= 2)add =0;
                    else if (topnote >= 3)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 5 + (12*add);}
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("シ/B"))){
                    if (0 <= topnote && topnote <= 1)add =0;
                    else if (topnote >= 2)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 3 + (12*add);}
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("休符")))
                    lodedmusic[(page - 1) * 8 + select - 1] = 0;
                //"ド/C","レ/D","ミ/E","ファ/F","ソ/G","ラ/A","シ/B"


                saveMusic.saveMusic(player, lodedmusic.clone());
                openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote, page);

                ItemStack update_select = new ItemStack(Material.PAPER);
                update_select.setItemMeta(makeItem.make_itemmeta(update_select, "カーソルを移動", null, "next_b_up", null, null));
                player.getInventory().setItem(select + 9, update_select);

                if (select >= 8) select = 0;
                select++;
                update_select.setItemMeta(makeItem.make_itemmeta(update_select, "選択中", null, "select", null, null));
                player.getInventory().setItem(select + 9, update_select);


            } else if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                //▼musicを拾ってくる
                int[] lodedmusic = saveMusic.load_Music(player);
                ItemStack page_item = clicked_inv.getItem(2);
                int page = openMenu.daw_input_getPage(page_item);
                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = openMenu.daw_get_topnote(topnote_item);
                int add = 0;
                int select = 1;
                for (int i = 1; i <= 8; i++) {
                    ItemStack select_item = clicked_inv.getItem(i + 9);
                    if (Objects.requireNonNull(select_item).getType() == Material.PAPER && Objects.requireNonNull(select_item.getItemMeta().getItemModel()).equals(NamespacedKey.minecraft("select"))) {
                        select = i;
                    }
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ド#/C#"))){
                    if (topnote != 0){
                        if (1 <= topnote && topnote <= 6)add =0;
                        else if (topnote >= 7)add =1;
                        lodedmusic[(page - 1) * 8 + select - 1] = 13 + add * 12;
                    }
                }

                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("レ#/D#"))){
                    if (0 <= topnote && topnote <= 5)add =0;
                    else if (topnote >= 6)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 11 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ファ#/F#"))){
                    if (0 <= topnote && topnote <= 3)add =0;
                    else if (topnote >= 4)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 8 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ソ#/G#"))){
                    if (0 <= topnote && topnote <= 2)add =0;
                    else if (topnote >= 3)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 6 + (12*add);
                }
                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("ラ#/A#"))){
                    if (0 <= topnote && topnote <= 1)add =0;
                    else if (topnote >= 2)add =1;
                    lodedmusic[(page - 1) * 8 + select - 1] = 4 + (12*add);}
                saveMusic.saveMusic(player, lodedmusic.clone());
                openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote, page);
                ItemStack update_select = new ItemStack(Material.PAPER);
                update_select.setItemMeta(makeItem.make_itemmeta(update_select, "カーソルを移動", null, "next_b_up", null, null));
                player.getInventory().setItem(select + 9, update_select);
                if (select >= 8) select = 0;
                select++;
                update_select.setItemMeta(makeItem.make_itemmeta(update_select, "選択中", null, "select", null, null));
                player.getInventory().setItem(select + 9, update_select);


            } else if (clicked.getType() == Material.PAPER) {

                if (Objects.requireNonNull(meta.displayName()).equals(Component.text("上へスクロール")) && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_up"))) {

                    ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                    int topnote = openMenu.daw_get_topnote(topnote_item);

                    ItemStack page_item = clicked_inv.getItem(2);
                    int tag_int = openMenu.daw_input_getPage(page_item);
                    if (topnote <=0) topnote += 1;
                    openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote-1, tag_int);
                } else if (Objects.requireNonNull(meta.displayName()).equals(Component.text("下へスクロール")) && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_down"))) {

                    ItemStack page_item = clicked_inv.getItem(2);
                    int tag_int = openMenu.daw_input_getPage(page_item);

                    ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                    int topnote = openMenu.daw_get_topnote(topnote_item);
                    if (topnote >=7) topnote -= 1;
                    openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote+1, tag_int);

                } else if (Objects.requireNonNull(meta.displayName()).equals(Component.text("カーソルを移動")) && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_up"))) {
                    meta.displayName(Component.text("選択中"));
                    meta.setItemModel(NamespacedKey.minecraft("select"));
                    clicked.setItemMeta(meta);
                    ItemStack paper = new ItemStack(Material.PAPER);
                    ItemMeta Meta_paper = paper.getItemMeta();
                    int slot = event.getRawSlot() - 55;
                    for (int i = 0; i <= 7; i++) {
                        if (i != slot) {
                            Meta_paper.setItemModel(NamespacedKey.minecraft("next_b_up"));
                            Meta_paper.displayName(Component.text("カーソルを移動"));
                            paper.setItemMeta(Meta_paper);
                            player.getInventory().setItem(10 + i, paper);
                        }
                    }
                } else if (Objects.requireNonNull(meta.displayName()).equals(Component.text("次のページへ")) && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_right"))) {

                    ItemStack page_item = Objects.requireNonNull(clicked_inv).getItem(2);
                    if (clicked_inv == pl_inv) {
                        NamespacedKey page_key = new NamespacedKey("itokagimaru_daw", "page");
                        int tag_int = openMenu.daw_input_getPage(Objects.requireNonNull(page_item));
                        if (tag_int > 31) return;
                        page_item.setItemMeta(makeItem.make_itemmeta(page_item,"現在" + String.valueOf(tag_int + 1) + "ページ目",null,null,page_key,String.valueOf(tag_int + 1)));
                        ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                        int topnote = openMenu.daw_get_topnote(topnote_item);
                        openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote, tag_int + 1);

                        ItemStack paper = new ItemStack(Material.PAPER);
                        paper.setItemMeta(makeItem.make_itemmeta(paper, "選択中", null, "select", null, null));
                        player.getInventory().setItem(10, paper);
                        for (int i = 1; i <= 7; i++) {
                            paper.setItemMeta(makeItem.make_itemmeta(paper, "カーソルを移動", null, "next_b_up", null, null));
                            player.getInventory().setItem(10 + i, paper);
                        }
                    }

                } else if (Objects.requireNonNull(meta.displayName()).equals(Component.text("前のページへ")) && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_left"))) {

                    ItemStack page_item = clicked_inv.getItem(2);
                    if (clicked_inv == pl_inv) {
                        NamespacedKey page_key = new NamespacedKey("itokagimaru_daw", "page");
                        int tag_int = openMenu.daw_input_getPage(page_item);
                        if (tag_int < 2) return;
                        page_item.setItemMeta(makeItem.make_itemmeta(page_item,"現在" + String.valueOf(tag_int - 1) + "ページ目",null, null, page_key, String.valueOf(tag_int - 1)));

                        ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                        int topnote = openMenu.daw_get_topnote(topnote_item);
                        openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote, tag_int - 1);
                        ItemStack paper = new ItemStack(Material.PAPER);
                        paper.setItemMeta(makeItem.make_itemmeta(paper, "選択中", null, "select", null, null));
                        player.getInventory().setItem(10, paper);
                        for (int i = 1; i <= 7; i++) {
                            paper.setItemMeta(makeItem.make_itemmeta(paper, "カーソルを移動", null, "next_b_up", null, null));
                            player.getInventory().setItem(10 + i, paper);
                        }


                    }

                }
            } else if (clicked.getType() == Material.STRUCTURE_VOID && Objects.requireNonNull(meta.displayName()).equals(Component.text("全削除"))) {
                int[] reset = new int[256];
                Arrays.fill(reset, 0);
                saveMusic.saveMusic(player,reset);

                ItemStack page_item = clicked_inv.getItem(2);
                Integer tag_int = openMenu.daw_input_getPage(page_item);

                ItemStack topnote_item = player.getOpenInventory().getTopInventory().getItem(0);
                int topnote = openMenu.daw_get_topnote(topnote_item);
                openMenu.daw_input_gui_update(player, event.getInventory(), event.getView().title(), topnote, tag_int);
            } else{
                return;
            }
        } else if (event.getView().title().equals(Component.text("§b再生モード"))) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = clicked.getItemMeta();
            Inventory clicked_inv = event.getClickedInventory();

            if (clicked.getType() == Material.PAPER && Objects.requireNonNull(meta.getItemModel()).equals(NamespacedKey.minecraft("clock"))) {
                int bpm_int = openMenu.daw_play_getBPM(clicked);
                player.closeInventory();
                openMenu.daw_play_setBPM(player, bpm_int);
            }else if (clicked.getType() == Material.PAPER && Objects.requireNonNull(meta.getItemModel()).equals(NamespacedKey.minecraft("next_b_right"))) {
                double bpm = (double) openMenu.daw_play_getBPM(Objects.requireNonNull(clicked_inv.getItem(2)));
                clicked.setItemMeta(makeItem.make_itemmeta(clicked,"再生停止",null, "elytra", null, null));
                Itokagimaru_daw.operation_playing playing = new Itokagimaru_daw.operation_playing();
                Itokagimaru_daw.play play = new Itokagimaru_daw.play();
                playing.set_playing(player,play);
                play.play_music(player,(long) (1200/bpm));

            }else if (clicked.getType() == Material.PAPER && Objects.requireNonNull(meta.getItemModel()).equals(NamespacedKey.minecraft("elytra"))) {
                Itokagimaru_daw.operation_playing playing = new Itokagimaru_daw.operation_playing();
                Itokagimaru_daw.play play = playing.get_playing(player);
                clicked.setItemMeta(makeItem.make_itemmeta(clicked, "再生", null, "next_b_right", null, null));
                play.stop_task();
            }
        } else if (event.getView().title().equals(Component.text("§b設定/BPM"))) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            Player player = (Player) event.getWhoClicked();

            ItemMeta meta = clicked.getItemMeta();
            Inventory clicked_inv = event.getClickedInventory();
            if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE && meta.getPersistentDataContainer().get(new NamespacedKey("itokagimaru_daw", "itemtag"), PersistentDataType.STRING) != null) {
                int tag_int =Integer.parseInt(meta.getPersistentDataContainer().get(new NamespacedKey("itokagimaru_daw", "itemtag"), PersistentDataType.STRING));
                openMenu.daw_play_mode(player, tag_int);
            }else if (clicked.getType() == Material.PAPER && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_right"))) {
                int bpm =Integer.parseInt(Objects.requireNonNull(player.getOpenInventory().getTopInventory().getItem(1)).getPersistentDataContainer().get(getKey.itemTag_key(), PersistentDataType.STRING));
                int[] bpmlist = {1,2,3,4,5,6,8,10,12,15,16,20,24,25,30,40,48,50,60,75,80,100,120,150,200,240,300,400,600,1200};
                int selected_bpm = 0;
                for (int i = 0;i < bpmlist.length;i++) {
                    if (bpm == bpmlist[i]){
                        selected_bpm = i;
                    }
                }
                if (selected_bpm >bpmlist.length-7 ) selected_bpm = bpmlist.length-7;

                openMenu.daw_play_setBPM(player, bpmlist[selected_bpm + 1]);
            }else if (clicked.getType() == Material.PAPER && Objects.equals(meta.getItemModel(), NamespacedKey.minecraft("next_b_left"))) {
                int bpm =Integer.parseInt(Objects.requireNonNull(player.getOpenInventory().getTopInventory().getItem(1)).getPersistentDataContainer().get(getKey.itemTag_key(), PersistentDataType.STRING));
                int[] bpmlist = {1,2,3,4,5,6,8,10,12,15,16,20,24,25,30,40,48,50,60,75,80,100,120,150,200,240,300,400,600,1200};
                int selected_bpm = 0;
                for (int i = 0;i < bpmlist.length;i++) {
                    if (bpm == bpmlist[i]){
                        selected_bpm = i;
                    }
                }
                if (selected_bpm >bpmlist.length-7 ) selected_bpm = bpmlist.length-7;
                if (selected_bpm<1)selected_bpm = 1;
                openMenu.daw_play_setBPM(player, bpmlist[selected_bpm - 1]);
            }

        }
    }

}
