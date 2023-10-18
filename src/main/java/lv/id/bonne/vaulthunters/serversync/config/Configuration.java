//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.config;


import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;


/**
 * The configuration handling class. Holds all the config values.
 */
public class Configuration
{
    /**
     * The constructor for the config.
     */
    public Configuration()
    {
        this.builder = new ForgeConfigSpec.Builder();

        this.listSyncConfigs = this.builder.
            comment("This list holds config files that should be synced with server.").
            comment("Supported Values: `json` files").
            define("syncConfigs",
                Arrays.asList("researches.json",
                    "researches_groups.json",
                    "researches_groups_styles.json",
                    "researches_gui_styles.json"),
                entry -> entry instanceof String value &&
                    value.endsWith(".json"));

        Configuration.GENERAL_SPEC = this.builder.build();
    }


    /**
     * Gets list sync configs.
     *
     * @return the list sync configs
     */
    public ForgeConfigSpec.ConfigValue<List<? extends String>> getListSyncConfigs()
    {
        return this.listSyncConfigs;
    }


    // ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * The main builder for the config.
     */
    private final ForgeConfigSpec.Builder builder;

    /**
     * The config value for the list of configs to sync.
     */
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> listSyncConfigs;

    /**
     * The general config spec.
     */
    public static ForgeConfigSpec GENERAL_SPEC;
}
