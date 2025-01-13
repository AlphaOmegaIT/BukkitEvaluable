package me.blvckbytes.bukkitevaluable.section;

import me.blvckbytes.bbconfigmapper.ScalarType;
import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bbconfigmapper.sections.CSAlways;
import me.blvckbytes.bbconfigmapper.sections.CSIgnore;
import me.blvckbytes.bukkitevaluable.BukkitEvaluable;
import me.blvckbytes.bukkitevaluable.CommandUpdater;
import me.blvckbytes.bukkitevaluable.IEnumInfo;
import me.blvckbytes.bukkitevaluable.error.ErrorContext;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;
import me.blvckbytes.gpeee.interpreter.IEvaluationEnvironment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public abstract class ACommandSection extends AConfigSection {
  
  @CSIgnore
  private final String defaultCommandName;
  
  private String name, description, usage;
  
  @CSAlways
  private List<String> aliases;
  
  @CSAlways
  private PermissionsSection permissions;
  
  @CSAlways
  private Map<String, BukkitEvaluable> argumentUsages;
  
  @CSAlways
  private CommandErrorMessagesSection errorMessages;
  
  public ACommandSection(
      String defaultCommandName,
      EvaluationEnvironmentBuilder environmentBuilder
  ) {
    super(environmentBuilder);
    this.defaultCommandName = defaultCommandName;
  }
  
  @Override
  public @Nullable Object defaultFor(Field field) {
    if (field
            .getName()
            .equals("name"))
      return this.defaultCommandName;
    
    if (field.getType() == String.class)
      return "";
    
    return null;
  }
  
  public Component getMalformedDoubleMessage(ErrorContext errorContext) {
    return errorMessages
               .getMalformedDouble()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getMalformedFloatMessage(ErrorContext errorContext) {
    return errorMessages
               .getMalformedFloat()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getMalformedLongMessage(ErrorContext errorContext) {
    return errorMessages
               .getMalformedLong()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getMalformedIntegerMessage(ErrorContext errorContext) {
    return errorMessages
               .getMalformedInteger()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getMalformedUuidMessage(ErrorContext errorContext) {
    return errorMessages
               .getMalformedUuid()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getMalformedEnumMessage(
      ErrorContext errorContext,
      IEnumInfo enumInfo
  ) {
    return errorMessages
               .getMalformedEnum()
               .asComponent(new EvaluationEnvironmentBuilder()
                                .withStaticVariable("constant_names", enumInfo.getEnumConstantNames())
                                .build(getErrorContextEnvironment(errorContext)));
  }
  
  public Component getMissingArgumentMessage(ErrorContext errorContext) {
    if (errorContext.argumentIndex == null)
      throw new IllegalStateException("Argument index cannot be null if a usage string is requested");
    
    int             index          = errorContext.argumentIndex + 1;
    BukkitEvaluable usageEvaluable = argumentUsages.get(String.valueOf(index));
    
    if (usageEvaluable == null)
      return MiniMessage
                 .miniMessage()
                 .deserialize("<red>There's no usage string configured for index " + index + "</red>");
    
    return Component.join(JoinConfiguration.newlines(), usageEvaluable.asList(ScalarType.COMPONENT, getErrorContextEnvironment(errorContext)));
  }
  
  public Component getNotAPlayerMessage(ErrorContext errorContext) {
    return errorMessages
               .getNotAPlayer()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getNotAConsoleMessage(ErrorContext errorContext) {
    return errorMessages
               .getNotAConsole()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getPlayerUnknownMessage(ErrorContext errorContext) {
    return errorMessages
               .getPlayerUnknown()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getPlayerNotOnlineMessage(ErrorContext errorContext) {
    return errorMessages
               .getPlayerNotOnline()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public Component getInternalErrorMessage(ErrorContext errorContext) {
    return errorMessages
               .getInternalError()
               .asComponent(this.getErrorContextEnvironment(errorContext));
  }
  
  public @NotNull String getName() {
    return this.name;
  }
  
  public @NotNull List<String> getAliases() {
    return this.aliases == null ? List.of() : this.aliases;
  }
  
  public @NotNull String getDescription() {
    return this.description == null ? "" : this.description;
  }
  
  public PermissionsSection getPermissions() {
    return this.permissions;
  }
  
  public @NotNull String getUsage() {
    return this.usage;
  }
  
  public @NotNull String getDefaultCommandName() {
    return this.defaultCommandName;
  }
  
  private IEvaluationEnvironment getErrorContextEnvironment(ErrorContext context) {
    String value = null;
    
    if (context.argumentIndex != null && context.argumentIndex < context.arguments.length)
      value = context.arguments[ context.argumentIndex ];
    
    return new EvaluationEnvironmentBuilder()
               .withStaticVariable("value", value)
               .withStaticVariable("alias", context.alias)
               .withStaticVariable("sender_name", context.sender.getName())
               .build();
  }
}