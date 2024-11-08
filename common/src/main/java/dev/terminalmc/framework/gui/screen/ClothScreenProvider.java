/*
 * Framework by TerminalMC
 *
 * To the extent possible under law, the person who associated CC0 with
 * Framework has waived all copyright and related or neighboring rights
 * to Framework.
 *
 * You should have received a copy of the CC0 legalcode along with this
 * work. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package dev.terminalmc.framework.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import dev.terminalmc.framework.config.Config;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import static dev.terminalmc.framework.util.Localization.localized;

public class ClothScreenProvider {
    /**
     * Builds and returns a Cloth Config options screen.
     * @param parent the current screen.
     * @return a new options {@link Screen}.
     * @throws NoClassDefFoundError if the Cloth Config API mod is not
     * available.
     *
     * <p> Most entry-builder options are displayed, those not required are
     * marked as // op</p>
     *
     * <p>Optional always-available entry-builder options are:
     * <li>
     *     {@code setTooltipSupplier}, which allows you to set the tooltip based
     *     on the option value.
     * </li>
     * <li>
     *     {@code setErrorSupplier}, which allows you to provide custom error
     *     conditions and error text.
     * </li>
     * <li>
     *     {@code setRequirement}, which tells Cloth Config when to display the
     *     option.
     * </li>
     * <li>
     *     {@code setDisplayRequirement}, which tells Cloth Config when to allow
     *     the user to change the option value.
     * </li>
     * <li>
     *     {@code requireRestart}, self-explanatory.
     * </li>
     * </p>
     */
    static Screen getConfigScreen(Screen parent) {
        Config.Options options = Config.get().options;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(localized("name"))
                .setSavingRunnable(Config::save);
        ConfigEntryBuilder eb = builder.entryBuilder();

        // First category
        ConfigCategory firstCat = builder.getOrCreateCategory(localized("option", "cat1"));

        // Yes/No button
        firstCat.addEntry(eb.startBooleanToggle(
                localized("option", "cat1.booleanOption"), options.booleanOption)
                .setTooltip(localized("option", "cat1.booleanOption.tooltip"))
                .setDefaultValue(Config.Options.booleanOptionDefault)
                .setSaveConsumer(val -> options.booleanOption = val)
                .build());

        // Colored Custom/Custom button
        firstCat.addEntry(eb.startBooleanToggle(
                localized("option", "cat1.booleanOption"), options.booleanOption)
                .setTooltip(localized("option", "cat1.booleanOption.tooltip"))
                .setDefaultValue(Config.Options.booleanOptionDefault)
                .setSaveConsumer(val -> options.booleanOption = val)
                .setYesNoTextSupplier(val -> val // op
                        ? localized("option", "cat1.booleanOption.true")
                        .withStyle(ChatFormatting.GREEN)
                        : localized("option", "cat1.booleanOption.false")
                        .withStyle(ChatFormatting.RED))
                .build());

        // Integer slider with value text formatting (also available for Long)
        firstCat.addEntry(eb.startIntSlider(
                localized("option", "cat1.intOption"), options.intOption, 0, 10)
                .setTooltip(localized("option", "cat1.intOption.tooltip"))
                .setDefaultValue(Config.Options.intOptionDefault)
                .setSaveConsumer(val -> options.intOption = val)
                .setTextGetter(val -> localized("option", "cat1.intOption.value", val)) // op
                .build());

        // Double field with range (also available for Integer, Float, Long)
        firstCat.addEntry(eb.startDoubleField(
                localized("option", "cat1.doubleOption"), options.doubleOption)
                .setTooltip(localized("option", "cat1.doubleOption.tooltip"))
                .setDefaultValue(Config.Options.doubleOptionDefault)
                .setSaveConsumer(val -> options.doubleOption = val)
                .setMin(0d) // op
                .setMax(10d) // op
                .build());

        // String field (lenient)
        firstCat.addEntry(eb.startStrField(
                localized("option", "cat1.lenientStringOption"), options.lenientStringOption)
                .setTooltip(localized("option", "cat1.lenientStringOption.tooltip"))
                .setDefaultValue(Config.Options.lenientStringOptionDefault)
                .setSaveConsumer(val -> options.lenientStringOption = val)
                .build());

        // String field (strict) with dropdown suggestion provider
        firstCat.addEntry(eb.startStringDropdownMenu(
                localized("option", "cat1.strictStringOption"), options.strictStringOption)
                .setTooltip(localized("option", "cat1.strictStringOption.tooltip"))
                .setDefaultValue(Config.Options.strictStringOptionDefault)
                .setSaveConsumer(val -> options.strictStringOption = val)
                .setSelections(Config.Options.strictStringOptionValues)
                .setErrorSupplier(val -> {
                    if (Config.Options.strictStringOptionValues.contains(val)) return Optional.empty();
                    else return Optional.of(localized("option", "cat1.strictStringOption.error"));
                })
                .build());

        // Enum dropdown
        firstCat.addEntry(eb.startDropdownMenu(
                localized("option", "cat1.enumOption"), options.enumOption,
                        Config.TriState::valueOf)
                .setTooltip(localized("option", "cat1.enumOption.tooltip"))
                .setDefaultValue(Config.Options.enumOptionDefault)
                .setSaveConsumer(val -> options.enumOption = val)
                .setSelections(List.of(Config.TriState.values()))
                .setSuggestionMode(false) // Disable typing
                .build());

        // Enum cycling button
        firstCat.addEntry(eb.startEnumSelector(
                localized("option", "cat1.enumOption"),
                        Config.TriState.class, options.enumOption)
                .setTooltip(localized("option", "cat1.enumOption.tooltip"))
                .setDefaultValue(Config.Options.enumOptionDefault)
                .setSaveConsumer(val -> options.enumOption = val)
                .setEnumNameProvider(val ->
                        localized("option", "cat1.enumOption.value", val.name())) // op
                .build());

        // Object (in this case, string) list cycling button
        firstCat.addEntry(eb.startSelector(
                localized("option", "cat1.cyclingObjectOption"),
                        Config.Options.strictStringOptionValues.toArray(), options.strictStringOption)
                .setTooltip(localized("option", "cat1.cyclingObjectOption.tooltip"))
                .setDefaultValue(Config.Options.strictStringOptionDefault)
                .setSaveConsumer(val -> options.strictStringOption = (String)val)
                .setNameProvider(val -> Component.literal((String)val)) // op
                .build());

        // Second category
        ConfigCategory secondCat = builder.getOrCreateCategory(localized("option", "cat2"));

        // Collapsible list of strings (also available for Integer, Float, Double, Long)
        secondCat.addEntry(eb.startStrList(
                localized("option", "cat2.stringListOption"), options.stringListOption)
                .setTooltip(localized("option", "cat2.stringListOption.tooltip"))
                .setDefaultValue(Config.Options.stringListOptionDefault)
                .setSaveConsumer(val -> options.stringListOption = val)
                .setCreateNewInstance((entry) -> new StringListListEntry.StringListCell(
                        Config.Options.stringListOptionValueDefault, entry)) // op
                .setInsertInFront(false) // op, default false
                .setExpanded(true) // op, default false
                .build());

        // Third category
        ConfigCategory thirdCat = builder.getOrCreateCategory(localized("option", "cat3"));

        // Multiline text
        thirdCat.addEntry(eb.startTextDescription(
                localized("option", "cat3.message"))
                .build());

        // Collapsible group of options
        SubCategoryBuilder thirdCatFirstGroup = eb.startSubCategory(
                localized("option", "cat3.group1"))
                .setTooltip(localized("option", "cat3.group1.tooltip"))
                .setExpanded(true); // op, default false

        thirdCatFirstGroup.add(eb.startColorField(
                localized("option", "cat3.group1.rgbOption"), options.rgbOption)
                .setTooltip(localized("option", "cat3.group1.rgbOption.tooltip"))
                .setDefaultValue(Config.Options.rgbOptionDefault)
                .setSaveConsumer(val -> options.rgbOption = val)
                .setAlphaMode(false) // op, default false
                .build());

        thirdCatFirstGroup.add(eb.startColorField(
                        localized("option", "cat3.group1.argbOption"), options.argbOption)
                .setTooltip(localized("option", "cat3.group1.argbOption.tooltip"))
                .setDefaultValue(Config.Options.argbOptionDefault)
                .setSaveConsumer(val -> options.argbOption = val)
                .setAlphaMode(true) // op, default false
                .build());

        thirdCatFirstGroup.add(eb.startKeyCodeField(
                localized("option", "cat3.group1.keyOption"),
                        InputConstants.getKey(options.keyOption, options.keyOption))
                .setTooltip(localized("option", "cat3.group1.keyOption.tooltip"))
                .setDefaultValue(InputConstants.getKey(Config.Options.keyExampleDefault,
                        Config.Options.keyExampleDefault))
                .setKeySaveConsumer(val -> options.keyOption = val.getValue())
                .setAllowKey(true) // op, default true
                .setAllowMouse(true) // op, default true
                .build());

        // Item field with dropdown
        // Cloth Config does not have a dedicated item option like YACL
        Set<String> items = new HashSet<>(BuiltInRegistries.ITEM.keySet()
                .stream().map(ResourceLocation::toString).toList());
        thirdCatFirstGroup.add(eb.startStringDropdownMenu(
                        localized("option", "cat3.group1.itemOption"), options.itemOption)
                .setTooltip(localized("option", "cat3.group1.itemOption.tooltip"))
                .setDefaultValue(Config.Options.itemOptionDefault)
                .setSaveConsumer(val -> options.itemOption = val)
                .setSelections(items)
                .setErrorSupplier(val -> {
                    if (items.contains(val)) return Optional.empty();
                    else return Optional.of(localized("option", "cat3.group1.itemOption.error"));
                })
                .build());

        thirdCat.addEntry(thirdCatFirstGroup.build());

        return builder.build();
    }
}
