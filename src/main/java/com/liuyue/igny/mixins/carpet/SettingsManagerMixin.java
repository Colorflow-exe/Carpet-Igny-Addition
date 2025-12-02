package com.liuyue.igny.mixins.carpet;

import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.data.RuleChangeDataManager;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.tracker.RuleChangeTracker;
import com.liuyue.igny.utils.CommandPermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import carpet.utils.Translations;

import static carpet.utils.Translations.tr;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {

    @Unique
    private static final ThreadLocal<carpet.api.settings.CarpetRule<?>> CURRENT_RULE = new ThreadLocal<>();

    @Inject(method = "displayRuleMenu", at = @At("HEAD"))
    private void captureCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.set(rule);
    }

    @Inject(method = "displayRuleMenu", at = @At("RETURN"))
    private void clearCurrentRule(CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        CURRENT_RULE.remove();
    }
    @Unique
    private static final String RECORD_OPERATOR = "igny.settings.record.operator";

    @Unique
    private static final String CHANGE_TIME = "igny.settings.record.change_time";

    @Unique
    private static final String RAW_VALUE = "igny.settings.record.raw_value";
    @Inject(
            method = "displayRuleMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/commands/CommandSourceStack;[Ljava/lang/Object;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void addOperationInfoAfterCurrentValue(
            CommandSourceStack source, CarpetRule<?> rule, CallbackInfoReturnable<Integer> cir) {
        if (!IGNYSettings.ShowRuleChangeHistory) {
            return;
        }

        carpet.api.settings.CarpetRule<?> currentRule = CURRENT_RULE.get();
        if (currentRule != null) {
            Optional<RuleChangeDataManager.RuleChangeRecord> lastChange =
                    RuleChangeDataManager.getLastChange(currentRule.name());

            if (lastChange.isPresent()) {
                RuleChangeDataManager.RuleChangeRecord record = lastChange.get();

                if (record.isValid()) {
                    carpet.utils.Messenger.m(source,
                            "g  "+Translations.tr(RECORD_OPERATOR,"Operator")+": ", "w " + record.sourceName,
                            "g  "+Translations.tr(CHANGE_TIME,"ChangeTime")+": ", "w " + record.formattedTime,
                            "g  "+Translations.tr(RAW_VALUE,"RawValue")+": ", "w " + objectToString(record.rawValue)
                    );
                }
            }
        }
    }


    @Unique
    private String objectToString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Boolean) return (Boolean) obj ? "true" : "false";
        return obj.toString();
    }
    @Inject(method = "setRule",at= @At(value = "INVOKE", target = "Lcarpet/api/settings/CarpetRule;set(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void onRuleChanged(CommandSourceStack source, CarpetRule<?> rule, String newValue, CallbackInfoReturnable<Integer> cir){
        RuleChangeTracker.ruleChanged(source, rule, newValue);
    }

    @Unique
    private static final String VERSION_TRANSLATION_KEY = "igny.settings.command.version";

    @Unique
    private static final String TOTAL_RULES_TRANSLATION_KEY = "igny.settings.command.total_rules";

    @Inject(
            method = "listAllSettings",
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=carpet.settings.command.version",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/api/settings/SettingsManager;getCategories()Ljava/lang/Iterable;",
                    ordinal = 0
            ),
            remap = false
    )
    private void printVersion(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
            Messenger.m(
                    source,
                    Messenger.c(
                            String.format("g %s ", IGNYServer.fancyName),
                            String.format("g %s: ", Translations.tr(VERSION_TRANSLATION_KEY, "Version")),
                            String.format("g %s ", IGNYServerMod.getVersion()),
                            String.format("g (%s: %d)", Translations.tr(TOTAL_RULES_TRANSLATION_KEY, "total rules"), IGNYServer.ruleCount)
                    )
            );

    }


    @Shadow
    public abstract carpet.api.settings.CarpetRule<?> getCarpetRule(String name);

    @Shadow
    public abstract String identifier();

    @Shadow
    protected abstract int listAllSettings(CommandSourceStack source);

    @Shadow
    @Final
    private String identifier;

    @Shadow
    public abstract boolean locked();

    @Shadow
    @Final
    private String fancyName;

    @Shadow
    protected abstract int listSettings(CommandSourceStack source, String title, Collection<CarpetRule<?>> settings_list);

    @Shadow
    protected abstract Collection<CarpetRule<?>> getRulesSorted();

    @Shadow
    protected abstract Collection<CarpetRule<?>> findStartupOverrides();

    @Shadow
    public abstract Iterable<String> getCategories();

    @Shadow
    protected abstract Collection<CarpetRule<?>> getRulesMatching(String search);

    @Shadow
    static CompletableFuture<Suggestions> suggestMatchingContains(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
        return null;
    }

    @Shadow
    protected abstract int removeDefault(CommandSourceStack source, CarpetRule<?> rule);

    @Shadow
    protected abstract CarpetRule<?> contextRule(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    @Shadow
    protected abstract int setDefault(CommandSourceStack source, CarpetRule<?> rule, String stringValue);

    @Shadow
    protected abstract int displayRuleMenu(CommandSourceStack source, CarpetRule<?> rule);


    @Inject(method = "registerCommand", at = @At("HEAD"))
    private void onRegisterCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        if (dispatcher.getRoot().getChildren().stream().anyMatch(node -> node.getName().equalsIgnoreCase(this.identifier)))
        {
            CarpetSettings.LOG.error("Failed to add settings command for " + this.identifier + ". It is masking previous command.");
            return;
        }

        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = literal(identifier).requires((player) ->
                CommandHelper.canUseCommand(player, CarpetSettings.carpetCommandPermissionLevel) && !this.locked());

        literalargumentbuilder.executes((context)-> this.listAllSettings(context.getSource())).
                then(literal("list").
                        executes( (c) -> this.listSettings(c.getSource(), String.format(tr(TranslationKeys.ALL_MOD_SETTINGS), this.fancyName),
                                this.getRulesSorted())).
                        then(literal("defaults").
                                executes( (c)-> listSettings(c.getSource(),
                                        String.format(tr(TranslationKeys.CURRENT_FROM_FILE_HEADER), fancyName, (identifier+".conf")),
                                        this.findStartupOverrides()))).
                        then(argument("tag", StringArgumentType.word()).
                                suggests( (c, b)->suggest(this.getCategories(), b)).
                                executes( (c) -> listSettings(c.getSource(),
                                        String.format(tr(TranslationKeys.MOD_SETTINGS_MATCHING), fancyName, RuleHelper.translatedCategory(identifier(),StringArgumentType.getString(c, "tag"))),
                                        this.getRulesMatching(StringArgumentType.getString(c, "tag")))))).
                then(literal("removeDefault").
                        requires(s -> !locked()).
                        then(argument("rule", StringArgumentType.word()).
                                suggests( (c, b) -> this.suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                                executes((c) -> this.removeDefault(c.getSource(), this.contextRule(c))))).
                then(literal("setDefault").
                        requires(s -> !locked()).
                        then(argument("rule", StringArgumentType.word()).
                                suggests( (c, b) -> suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                                then(argument("value", StringArgumentType.greedyString()).
                                        suggests((c, b)-> suggest(contextRule(c).suggestions(), b)).
                                        executes((c) -> this.setDefault(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value")))))).
                then(argument("rule", StringArgumentType.word()).
                        suggests( (c, b) -> suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                        requires(s -> !locked()).
                        executes( (c) -> this.displayRuleMenu(c.getSource(), contextRule(c))).
                        then(argument("value", StringArgumentType.string()).
                                suggests((c, b)-> suggest(contextRule(c).suggestions(),b)).
                                executes((c) -> customSetRule(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value"), false)).
                                then(argument("setDefault", BoolArgumentType.bool()).
                                        requires(this::canUseSetDefault).
                                        executes((c) -> customSetRule(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value"), BoolArgumentType.getBool(c, "setDefault"))))));

        dispatcher.register(literalargumentbuilder);
    }
    @Unique
    private boolean canUseSetDefault(CommandSourceStack source){
        return CommandPermissions.canUseCommand(source, IGNYSettings.SetDefaultArgument);
    }

    @Unique
    private int customSetRule(CommandSourceStack source, CarpetRule<?> rule, String newValue, Boolean setDefault)
    {
        try {
            if (setDefault) {
                this.setDefault(source,rule,newValue);
            }else {
                rule.set(source, newValue);
                Messenger.m(source, "w " + rule.toString() + ", ", "c [" + tr(TranslationKeys.CHANGE_PERMANENTLY) + "?]",
                        "^w " + String.format(tr(TranslationKeys.CHANGE_PERMANENTLY_HOVER), identifier + ".conf"),
                        "?/" + identifier + " setDefault " + rule.name() + " " + RuleHelper.toRuleString(rule.value()));
            }
        } catch (InvalidRuleValueException e) {
            e.notifySource(rule.name(), source);
        }
        return 1;
    }

}