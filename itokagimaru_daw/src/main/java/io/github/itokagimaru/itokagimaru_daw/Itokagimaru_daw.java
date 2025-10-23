package io.github.itokagimaru.itokagimaru_daw;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

import org.bukkit.event.Listener;

import java.util.*;

import io.github.itokagimaru.itokagimaru_daw.listeners.Daw_menu_listener;
import io.github.itokagimaru.itokagimaru_daw.listeners.Daw_Item_use_listener;
import io.github.itokagimaru.itokagimaru_daw.listeners.Daw_close_inventory_listeners;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public final class Itokagimaru_daw extends JavaPlugin implements Listener {
    public static NamespacedKey page_key = new NamespacedKey("itokagimaru_daw", "page");
    public static NamespacedKey BPM_key = new NamespacedKey("itokagimaru_daw", "bpm");
    public static NamespacedKey itemTag_key = new NamespacedKey("itokagimaru_daw", "itemtag");
    public static NamespacedKey tempo_key = new NamespacedKey("itokagimaru_daw", "tempo");
    public static NamespacedKey topnote_key = new NamespacedKey("itokagimaru_daw", "topnote");
    public static Itokagimaru_daw instance;
    public static Optional<HashMap<UUID,Itokagimaru_daw.play>> playing = Optional.of(new HashMap<>());
    public static class operation_playing{
        public void set_playing(Player player,Itokagimaru_daw.play play){
            playing.get().put(player.getUniqueId(),play);
        }
        public Itokagimaru_daw.play get_playing(Player player){
            return playing.get().get(player.getUniqueId());
        }
    }
    public static class get_key{
        public NamespacedKey page_key(){
            return page_key;
        }
        public NamespacedKey bpm_key(){
            return BPM_key;
        }
        public NamespacedKey itemTag_key(){
            return itemTag_key;
        }
        public NamespacedKey tempo_key(){
            return tempo_key;
        }
        public NamespacedKey topnote_key(){
            return topnote_key;
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        music music = new music();
        music.setSaved_music(MapOfMusic.getMap(this));
        Bukkit.getPluginManager().registerEvents(this,this);
        getServer().getPluginManager().registerEvents(new Daw_menu_listener(),this);
        getServer().getPluginManager().registerEvents(new Daw_Item_use_listener(),this);
        getServer().getPluginManager().registerEvents(new Daw_close_inventory_listeners(),this);
        instance = this;
    }

    @Override
    public void onDisable() {
        music music = new music();
        MapOfMusic map = new MapOfMusic();
        map.save_map(this, music.getSavedMusic());
    }

    public static Itokagimaru_daw getInstance() {
        return instance;
    }
    public static class MapOfMusic{
        public void save_map (JavaPlugin plugin, Map<UUID,int[]> data) {
            File file = new File(plugin.getDataFolder(), "music.yml");
            YamlConfiguration config = new YamlConfiguration();
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }

            for (Map.Entry<UUID, int[]> entry : data.entrySet()) {
                String uuid = entry.getKey().toString();
                int[] value = entry.getValue();
                config.set(uuid, value);
            }
            try {
                config.save(file);
                plugin.getLogger().info("music save success.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public static Map<UUID, int[]> getMap(JavaPlugin plugin) {
            File file = new File(plugin.getDataFolder(), "music.yml");
            Map<UUID,int[]> data = new HashMap<>();
            if (!file.exists()) {
                plugin.getLogger().warning("music save failed.");
                return data;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<Integer> music = config.getIntegerList(key);
                    int[] value = music.stream().mapToInt(Integer::intValue).toArray();
                    data.put(uuid, value);
                }catch (IllegalArgumentException e){
                    plugin.getLogger().warning("UUIDの変換に失敗:" + key);
                }
            }
            plugin.getLogger().info("music save success: " + data);
            return data;
        }
    }
    public static class make_item {
        public ItemMeta make_itemmeta(ItemStack itemstack, String name, int[] color, String model, NamespacedKey key, String tag) {
            ItemMeta meta = itemstack.getItemMeta();
            if (!(name == null)) {
                if (color == null) {
                    meta.displayName(Component.text(name));
                } else {
                    meta.displayName(Component.text(name).color(TextColor.color(color[0], color[2], color[3])));
                }
                if (!(model == null)) {
                    meta.setItemModel(NamespacedKey.minecraft(model));
                }
                if (key == null) {
                    tag = null;
                }
                if (!(tag == null)) {
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag);
                }
                return meta;
            }
            return null;
        }
    }
    public static class open_menu {
        make_item makeitem = new make_item();
        //give @a diamond[custom_name=[{"text":"daw","italic":false,"color":"#95e5f9"}],item_model="id:sample",custom_model_data={floats:[1]}]
        public void daw_menu(Player player) {
            Inventory menu = Bukkit.createInventory(null,9, Component.text("§bだう"));



            ItemStack writable = new ItemStack(Material.WRITABLE_BOOK);
            writable.setItemMeta(makeitem.make_itemmeta(writable,"§e打ち込みモード",null,null,null,null));
            menu.setItem(2, writable);

            ItemStack disc = new ItemStack(Material.MUSIC_DISC_13);
            disc.setItemMeta(makeitem.make_itemmeta(disc,"§e再生モード",null,null,null,null));
            menu.setItem(4, disc);

            ItemStack note = new ItemStack(Material.NOTE_BLOCK);
            note.setItemMeta(makeitem.make_itemmeta(note,"§e演奏モード",null,null,null,null));
            menu.setItem(6, note);

            ItemStack bar = new ItemStack(Material.BARRIER);
            bar.setItemMeta(makeitem.make_itemmeta(bar,"§4しゅうりょう",null,null,null,null));
            menu.setItem(8, bar);

            player.openInventory(menu);
        }
        public void daw_input_mode (Player player) {
            inventory_save inv_save = new inventory_save();
            inv_save.seaveInventory(player);
            player.getInventory().clear();
            Inventory input_mode = Bukkit.createInventory(null,54,Component.text("§b打ち込みモード"));

            ItemStack paper = new ItemStack(Material.PAPER);

            for (int i = 0 ; i <= 53 ; i++) {
                paper.setItemMeta(makeitem.make_itemmeta(paper,"",null,"note_def",null,null));
                input_mode.setItem(i, paper);
            }
            for (int i = 0 ; i <= 5 ; i++) {
                paper.setItemMeta(makeitem.make_itemmeta(paper,"",null,"key_board_" + String.valueOf(i),null,null));
                input_mode.setItem(i * 9, paper);
            }
            paper.setItemMeta(makeitem.make_itemmeta(paper,"選択中",null,"select",null,null));
            player.getInventory().setItem(10, paper);
            for( int i = 1;i <= 7;i++){
                paper.setItemMeta(makeitem.make_itemmeta(paper,"カーソルを移動",null,"next_b_up",null,null));
                player.getInventory().setItem(10 + i, paper);
            }
            paper.setItemMeta(makeitem.make_itemmeta(paper,"上へスクロール",null,"next_b_up",null,null));
            player.getInventory().setItem(18, paper);
            paper.setItemMeta(makeitem.make_itemmeta(paper,"下へスクロール",null,"next_b_down",null,null));
            player.getInventory().setItem(27, paper);
            paper.setItemMeta(makeitem.make_itemmeta(paper,"前のページへ",null,"next_b_left",null,null));
            player.getInventory().setItem(1, paper);
            paper.setItemMeta(makeitem.make_itemmeta(paper,"次のページへ",null,"next_b_right",null,null));
            player.getInventory().setItem(3, paper);

            ItemStack sign = new ItemStack(Material.OAK_HANGING_SIGN);
            sign.setItemMeta(makeitem.make_itemmeta(sign,"現在1ページ目",null,null, page_key,"1"));
            player.getInventory().setItem(2, sign);

            ItemStack bar = new ItemStack(Material.BARRIER);
            bar.setItemMeta(makeitem.make_itemmeta(bar,"§4しゅうりょう",null,null,null,null));
            player.getInventory().setItem(8, bar);

            String[] white_name = {"休符","null","ド/C","レ/D","ミ/E","ファ/F","ソ/G","ラ/A","シ/B"};
            for (int i = 0; i < white_name.length ; i++) {
                if (!white_name[i].equals("null")) {
                    ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                    white.setItemMeta(makeitem.make_itemmeta(white,white_name[i],null,null,null,null));
                    player.getInventory().setItem(i + 26, white);
                }
            }

            String[] black_name = {"ド#/C#","レ#/D#","null","ファ#/F#","ソ#/G#","ラ#/A#"};
            for (int i = 0; i < black_name.length ; i++) {
                if (!black_name[i].equals("null")) {
                    ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    black.setItemMeta(makeitem.make_itemmeta(black,black_name[i],null,null,null,null));
                    player.getInventory().setItem(i + 19, black);
                }
            }
            ItemStack Void = new ItemStack(Material.STRUCTURE_VOID);
            Void.setItemMeta(makeitem.make_itemmeta(Void,"全削除",null,null,null,null));
            player.getInventory().setItem(6, Void);


            daw_input_gui_update(player,input_mode,Component.text("§b打ち込みモード"),0,1);
            player.openInventory(input_mode);

        }
        public void daw_input_gui_update(Player player , Inventory inv , Component inv_name, Integer top_note,Integer page) {
            music Music = new music();
            int[] music_list = Music.load_Music(player);
            int note_int;


            if (inv_name == null || top_note == null || music_list == null) {
                return;
            }
            if (Objects.requireNonNull(inv_name).equals(Component.text("§b打ち込みモード"))){
                ItemStack paper = new ItemStack(Material.PAPER);

                for (int i = 0 ; i <= 53 ; i++) {
                    paper.setItemMeta(makeitem.make_itemmeta(paper,"",null,"note_def",null,null));
                    inv.setItem(i, paper);
                }
                for (int i = 0 ; i <= 5 ; i++) {
                    note_int = top_note + i;
                    if (note_int >= 7)note_int -= 6;
                    paper.setItemMeta(makeitem.make_itemmeta(paper,String.valueOf(top_note) + "/" + String.valueOf(note_int),null,"key_board_" + String.valueOf(note_int),topnote_key,String.valueOf(top_note)));
                    inv.setItem(i * 9, paper);
                }

                for(int i = 0 ; i < 8 ; i++){

                    int music = music_list[i+((page -1)*8)];
                    if (music != 0){
                        if (music % 2 == 1){
                            paper.setItemMeta(makeitem.make_itemmeta(paper,"",null,"note_up",null,null));
                        } else {
                            paper.setItemMeta(makeitem.make_itemmeta(paper,"",null,"note_dw",null,null));
                        }
                        if(music > top_note*2 && music <= (top_note + 6)*2){

                            music -= top_note * 2;
                            if (music == 25)music=1;
                            else if (music >=13)music-=12;

                            if(music % 2 == 1){
                                music += 1;
                            }
                            inv.setItem(i +1+ (9 * music / 2 - 9), paper);
                        }
                    }
                }
            }
        }
        public int daw_input_getPage(ItemStack page_item) {
            ItemMeta page_meta = page_item.getItemMeta();
            String page_tag = page_meta.getPersistentDataContainer().get(page_key, PersistentDataType.STRING);
            int tag_int = Integer.parseInt(page_tag);
            return tag_int;
        }
        public int daw_get_topnote(ItemStack topnote_item) {
            if (!topnote_item.hasItemMeta()) return 0;
            ItemMeta top_meta = topnote_item.getItemMeta();
            String top_tag = top_meta.getPersistentDataContainer().get(topnote_key, PersistentDataType.STRING);
            int tag_int = Integer.parseInt(top_tag);
            return tag_int;
        }
        public void daw_play_mode(Player player,Integer bpm) {
            Inventory menu = Bukkit.createInventory(null,9, Component.text("§b再生モード"));
            ItemStack clock = new ItemStack(Material.PAPER);
            if (bpm == null)bpm = 60;
            clock.setItemMeta(makeitem.make_itemmeta(clock,"現在のBPM:" + String.valueOf(bpm),null,"clock",BPM_key,String.valueOf(bpm)));
            menu.setItem(2, clock);
            ItemStack play = new ItemStack(Material.PAPER);
            play.setItemMeta(makeitem.make_itemmeta(play,"再生",null,"next_b_right",null,null));
            menu.setItem(4, play);

            player.openInventory(menu);
        }
        public void daw_play_setBPM(Player player, int bpm) {
            Inventory menu = Bukkit.createInventory(null,9, Component.text("§b設定/BPM"));
            ItemStack paper = new ItemStack(Material.PAPER);
            paper.setItemMeta(makeitem.make_itemmeta(paper,"現在のBPM:" + String.valueOf(bpm),null,"clock",BPM_key,String.valueOf(bpm)));

            menu.setItem(4, paper);
            ItemStack green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            for (int i = 3; i > 0 ; i--){
                red.setItemMeta(makeitem.make_itemmeta(red,"-" + String.valueOf((int) Math.pow(10,3-i)),null,null,itemTag_key,String.valueOf((int) Math.pow(10,3-i) * -1)));
                menu.setItem(i, red);
            }

            for (int i = 1; i <= 3 ; i++){
                green.setItemMeta(makeitem.make_itemmeta(green,"+" + String.valueOf((int) Math.pow(10,i-1)),null,null,itemTag_key,String.valueOf((int) Math.pow(10,i-1))));
                menu.setItem(i + 4, green);
            }
            player.openInventory(menu);
        }
        public int daw_play_getBPM(ItemStack bpm_item) {
            ItemMeta bpm_meta = bpm_item.getItemMeta();
            String bpm_tag = bpm_meta.getPersistentDataContainer().get(BPM_key, PersistentDataType.STRING);
            int bpm_int = Integer.parseInt(Objects.requireNonNull(bpm_tag));
            return bpm_int;
        }
        public boolean get_playmode(Player player) {
            Inventory inv = player.getOpenInventory().getTopInventory();
            ItemStack itemStack = inv.getItem(4);
            boolean tes = (itemStack.getType() == Material.PAPER && Objects.equals(itemStack.getItemMeta().getItemModel(), NamespacedKey.minecraft("clock")));
            player.sendMessage(String.valueOf(tes));
            return (itemStack.getType() == Material.PAPER && Objects.equals(itemStack.getItemMeta().getItemModel(), NamespacedKey.minecraft("clock")));
        }
    }
    public static class inventory_save {
        public static HashMap<UUID, ItemStack[]> inv=new HashMap<>();
        public void seaveInventory(Player player){
            inv.put(player.getUniqueId(),player.getInventory().getContents().clone());
        }
        public void loadInventory(Player player){
            if(!inv.containsKey(player.getUniqueId())){
                return;
            }
            player.getInventory().setContents(inv.get(player.getUniqueId()).clone());


        }
    }
    public static class music {
        public static Map<UUID, int[]> saved_music = new HashMap<>();
        public void saveMusic(Player player , int[] music){
            saved_music.put(player.getUniqueId(),music);
        }
        public int[] load_Music(Player player){
            if(!saved_music.containsKey(player.getUniqueId())){
                int[] music = new int[256];
                Arrays.fill(music, 0);
                saveMusic(player,music);
                return music;
            }
            return saved_music.get(player.getUniqueId()).clone();
        }
        public Map<UUID, int[]> getSavedMusic(){
            return saved_music;
        }
        public void setSaved_music (Map<UUID, int[]> music){
            saved_music = music;
        }

    }
    public static class play {
        music music = new music();
        HashMap<UUID , BukkitTask> tasks = new HashMap<>();
        public void set_task(Player player,BukkitTask task){
            tasks.put(player.getUniqueId(),task);
        }
        public void remove_task(Player player){
            tasks.remove(player.getUniqueId());
        }
//        public void stop_task(Player player){
//            BukkitTask task = tasks.get(player.getUniqueId());
//            player.sendMessage(String.valueOf(task));
//            task.cancel();
//            remove_task(player);
//        }
        BukkitTask task;
        public void play_music (Player player,long interval){
            task = new BukkitRunnable() {
                final int[] loded_music = music.load_Music(player);
                int count = 0;
                float pitch = 0;
                final open_menu open_menu = new open_menu();
                @Override
                public void run() {
                    if (loded_music[count] == -1 || count >= loded_music.length || open_menu.get_playmode(player)) {
                        cancel();
                    }else if(loded_music[count] != 0){
                        pitch = (float) Math.pow(2.0 , (double) (14 - loded_music[count]) / 12);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f,pitch);
                    }
                    count++;
                }
            }.runTaskTimer(getInstance(),0,(long) interval);
        }
        public void stop_task(){
            task.cancel();
        }
    }
}
