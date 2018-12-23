package net.awesomepowered.rotator.utils;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.Spinnable;
import net.awesomepowered.rotator.types.EntitySpinner;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.ArrayList;
import java.util.List;

public class Windows {

    private RotatoR plugin;
    private Player p;

    public Windows(RotatoR plugin, Player player) {
        this.plugin = plugin;
        this.p = player;
    }

    public void openMainMenu() {
        if (getSpinner() == null) {
            openRotatorsMainMenu();
        } else {
            openSpinnerMenu();
        }
    }

    public void openRotatorsMainMenu() {
        plugin.debug("OPEN", "Rotators Main");
        String[] matrix = {
                "         ",
                "   x x   ", // Select | Edit
                "    c    "
        };

        GuiElementGroup group = new GuiElementGroup('x');
        InventoryGui gui = new InventoryGui(plugin, p, "&aR&fotato&aR Menu", matrix);

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    p.closeInventory();
                    openSpinnerSelector();
                    return true;
                },
                "&bSelect Spinner"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    p.closeInventory();
                    openMainMenu();
                    return true;
                },
                "&bEdit Spinner"
        ));

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    public void openSpinnerSelector() {
        plugin.debug("OPEN", "Spinner Selector");
        GuiElementGroup group = new GuiElementGroup('x');

        for (Spinnable spinner : plugin.blockSpinners.values()) {
            group.addElement(new StaticGuiElement('x',
                    new ItemStack(spinner.getLocation().getBlock().getType(), 1),
                    click -> {
                        ClickType type = click.getType();
                        if (type.isRightClick()) {
                            p.teleport(spinner.getLocation());
                        }  else {
                            setSpinnable(spinner);
                            openMainMenu();
                        }
                        return true;
                    },
                    "&bClick to select spinner"
            ));
        }

        for (Spinnable spinner : plugin.entitySpinners.values()) {
            EntitySpinner entitySpinner = (EntitySpinner) spinner;
            ItemStack egg = new ItemStack(Material.EGG);
            try {
                if (entitySpinner.getEntity() instanceof ItemFrame) {
                    egg = ((ItemFrame) entitySpinner.getEntity()).getItem();
                } else {
                    egg = new ItemStack(Material.valueOf(entitySpinner.getEntity().getType().name()+"_SPAWN_EGG"));
                }
            } catch (Exception ignored) {}
            group.addElement(new StaticGuiElement('x',
                    egg,
                    click -> {
                        ClickType type = click.getType();
                        if (type.isRightClick()) {
                            p.teleport(spinner.getLocation());
                        }  else {
                            setSpinnable(spinner);
                            openMainMenu();
                        }
                        return true;
                    },
                    "&bSelect " + entitySpinner.getEntity().getName() + " eSpinner"
            ));
        }

        InventoryGui gui = new InventoryGui(plugin, p, "&aSpinner Selector", buildMatrix(group.size()));

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.addElement(new GuiPageElement('b', new ItemStack(Material.COAL, 1), GuiPageElement.PageAction.PREVIOUS, "&cPREVIOUS"));
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.CHARCOAL, 1), GuiPageElement.PageAction.NEXT, "&aNEXT"));
        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    public void openSpeedSelector() {
        plugin.debug("OPEN", "Speed Selector");

        GuiElementGroup group = new GuiElementGroup('x');

        for (int i = 1; i <= 200; i++) {
            int finalI = i;
            group.addElement(new StaticGuiElement('x',
                    new ItemStack(Material.BREAD, 1),
                    click -> {
                        getSpinner().setRpm(finalI);
                        getSpinner().refresh();
                        return true;
                    },
                    "&bSet speed to &a" + finalI
            ));
        }

        InventoryGui gui = new InventoryGui(plugin, p, "&aSpeed Selector", buildMatrix(group.size()));

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.addElement(new GuiPageElement('b', new ItemStack(Material.COAL, 1), GuiPageElement.PageAction.PREVIOUS, "&cPREVIOUS"));
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.CHARCOAL, 1), GuiPageElement.PageAction.NEXT, "&aNEXT"));
        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    public void openSoundSelector() {
        plugin.debug("OPEN", "Sound Selector");
        GuiElementGroup group = new GuiElementGroup('x');

        for (Sound sound : Sound.values()) {
            group.addElement(new StaticGuiElement('x',
                    new ItemStack(Material.NOTE_BLOCK, 1),
                    click -> {
                        getSpinner().setSound(sound.name());
                        return true;
                    },
                    "&bSet sound to &a" + sound.name()
            ));
        }

        InventoryGui gui = new InventoryGui(plugin, p, "&aSound Selector", buildMatrix(group.size()));

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.addElement(new GuiPageElement('b', new ItemStack(Material.COAL, 1), GuiPageElement.PageAction.PREVIOUS, "&cPREVIOUS"));
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.CHARCOAL, 1), GuiPageElement.PageAction.NEXT, "&aNEXT"));
        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    public void openEffectSelector() {
        plugin.debug("OPEN", "Effect Selector");
        GuiElementGroup group = new GuiElementGroup('x');

        for (Effect effect : Effect.values()) {
            group.addElement(new StaticGuiElement('x',
                    new ItemStack(Material.BOOK, 1),
                    click -> {
                        getSpinner().setEffect(effect.name());
                        return true;
                    },
                    "&bSet effect to &a" + effect.name()
            ));
        }

        for (Particle particle : Particle.values()) {
            group.addElement(new StaticGuiElement('x',
                    new ItemStack(Material.ENCHANTED_BOOK, 1),
                    click -> {
                        getSpinner().setParticle(particle.name());
                        return true;
                    },
                    "&bSet particle to &a" + particle.name()
            ));
        }

        InventoryGui gui = new InventoryGui(plugin, p, "&aEffect Selector", buildMatrix(group.size()));

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.addElement(new StaticGuiElement('s',
                new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1),
                click -> {
                    getSpinner().setEffect(null);
                    return true;
                },
                "&cStop Effect"
        ));

        gui.addElement(new StaticGuiElement('s',
                new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1),
                click -> {
                    getSpinner().setParticle(null);
                    return true;
                },
                "&cStop Particle"
        ));

        gui.addElement(new GuiPageElement('b', new ItemStack(Material.COAL, 1), GuiPageElement.PageAction.PREVIOUS, "&cPREVIOUS"));
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.CHARCOAL, 1), GuiPageElement.PageAction.NEXT, "&aNEXT"));
        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    public void openSpinnerMenu() {
        plugin.debug("OPEN", "Spinner Menu");
        String[] matrix = {
                "         ",
                " x x x x ", // Mode | Speed | Sound | Effect
                " x x x x ", // Menu | Stop | Sound | Effect
                "    c    "
        };

        GuiElementGroup group = new GuiElementGroup('x');

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    getSpinner().setMode((getSpinner().getMode() == 0) ? 1 : 0);
                    getSpinner().refresh();
                    return true;
                },
                "&bChange Mode"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    openSpeedSelector();
                    return true;
                },
                "&bChange Speed"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    openSoundSelector();
                    return true;
                },
                "&bChange Sound"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.APPLE, 1),
                click -> {
                    openEffectSelector();
                    return true;
                },
                "&bChange Effect"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    openRotatorsMainMenu();
                    return true;
                },
                "&bMain Menu"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    getSpinner().selfDestruct();
                    setSpinnable(null);
                    p.closeInventory();
                    return true;
                },
                "&cStop spinner"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    getSpinner().setSound(null);
                    return true;
                },
                "&cStop Sound"
        ));

        group.addElement(new StaticGuiElement('x',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    getSpinner().setEffect(null);
                    return true;
                },
                "&cStop Effect"
        ));


        InventoryGui gui = new InventoryGui(plugin, p, "&aSpinner Menu", matrix);

        gui.addElement(new StaticGuiElement('c',
                new ItemStack(Material.RED_STAINED_GLASS_PANE, 1),
                click -> {
                    p.closeInventory();
                    return true;
                },
                "&c&lClose Menu"
        ));

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        group.setFiller(gui.getFiller());
        gui.addElement(group);
        gui.show(p);
    }

    private Spinnable getSpinner() {
        return plugin.leSigners.get(p.getUniqueId());
    }

    private void setSpinnable(Spinnable spinnable) {
        plugin.leSigners.put(p.getUniqueId(), spinnable);
    }

    private String[] buildMatrix(int i) {
        String defMatrix = " xxxxxxx ";
        List<String> matrix = new ArrayList<>();
        matrix.add("         ");
        matrix.add(defMatrix);
        if (i >= 8) {
            matrix.add(defMatrix);
        }
        if (i >= 15) {
            matrix.add(defMatrix);
        }
        if (i >= 22) {
            matrix.add(defMatrix);
            matrix.add("b  scs  f");
            return matrix.toArray(new String[0]);
        }
        matrix.add("    c    ");
        return matrix.toArray(new String[0]);
    }
}