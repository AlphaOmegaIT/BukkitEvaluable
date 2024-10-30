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
  private Map<String, BukkitEvaluable> argumentUsages;

  @CSAlways
  private CommandErrorMessagesSection errorMessages;

  public ACommandSection(String defaultCommandName, EvaluationEnvironmentBuilder environmentBuilder) {
    super(environmentBuilder);
    this.defaultCommandName = defaultCommandName;
  }

  @Override
  public @Nullable Object defaultFor(Field field) {
    if (field.getName().equals("name"))
      return this.defaultCommandName;

    if (field.getType() == String.class)
      return "";

    return null;
  }

  public String getMalformedDoubleMessage(ErrorContext errorContext) {
    return errorMessages.getMalformedDouble().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getMalformedFloatMessage(ErrorContext errorContext) {
    return errorMessages.getMalformedFloat().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getMalformedLongMessage(ErrorContext errorContext) {
    return errorMessages.getMalformedLong().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getMalformedIntegerMessage(ErrorContext errorContext) {
    return errorMessages.getMalformedInteger().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getMalformedUuidMessage(ErrorContext errorContext) {
    return errorMessages.getMalformedUuid().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getMalformedEnumMessage(ErrorContext errorContext, IEnumInfo enumInfo) {
    return errorMessages.getMalformedEnum().asScalar(
            ScalarType.STRING,
            new EvaluationEnvironmentBuilder()
                    .withStaticVariable("constant_names", enumInfo.getEnumConstantNames())
                    .build(getErrorContextEnvironment(errorContext))
    );
  }

  public String getMissingArgumentMessage(ErrorContext errorContext) {
    if (errorContext.argumentIndex == null)
      throw new IllegalStateException("Argument index cannot be null if a usage string is requested");

    int index = errorContext.argumentIndex + 1;
    BukkitEvaluable usageEvaluable = argumentUsages.get(String.valueOf(index));

    if (usageEvaluable == null)
      return "§cThere's no usage string configured for index " + index;

    return String.join("\n", usageEvaluable.asList(ScalarType.STRING, getErrorContextEnvironment(errorContext)));
  }

  public String getNotAPlayerMessage(ErrorContext errorContext) {
    return errorMessages.getNotAPlayer().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getNotAConsoleMessage(ErrorContext errorContext) {
    return errorMessages.getNotAConsole().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getPlayerUnknownMessage(ErrorContext errorContext) {
    return errorMessages.getPlayerUnknown().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getPlayerNotOnlineMessage(ErrorContext errorContext) {
    return errorMessages.getPlayerNotOnline().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public String getInternalErrorMessage(ErrorContext errorContext) {
    return errorMessages.getInternalError().asScalar(ScalarType.STRING, getErrorContextEnvironment(errorContext));
  }

  public @NotNull String getName() {
    return this.name;
  }

  public @NotNull List<String> getAliases() {
    return this.aliases;
  }

  public @NotNull String getDescription() {
    return this.description;
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
      value = context.arguments[context.argumentIndex];

    return new EvaluationEnvironmentBuilder()
            .withStaticVariable("value", value)
            .withStaticVariable("alias", context.alias)
            .withStaticVariable("sender_name", context.sender.getName())
            .build();
  }
}
