package de.tubs.latexTool.core.config;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.logging.Level;

/**
 * Validiert den Log Level für JCommander
 */
public class LogLevelValidator implements IParameterValidator {
  /**
   * Validate the parameter.
   *
   * @param name  The mName of the parameter (e.g. "-host").
   * @param value The value of the parameter that we need to validate
   * @throws com.beust.jcommander.ParameterException Thrown if the value of the parameter is invalid.
   */
  @Override
  public void validate(String name, String value) throws ParameterException {
    try {
      Level.parse(value);
    } catch (IllegalArgumentException e) {
      throw new ParameterException(e.getLocalizedMessage());
    }
  }
}
