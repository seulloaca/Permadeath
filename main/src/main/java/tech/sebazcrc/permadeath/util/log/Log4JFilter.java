package tech.sebazcrc.permadeath.util.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.logging.Level;

public class Log4JFilter extends AbstractFilter {

    private static final long serialVersionUID = -5594073755007974254L;

    private static Result validateMessage(Message message) {
        if (message == null) {
            return Result.NEUTRAL;
        }
        return validateMessage(message.getFormattedMessage());
    }

    private static Result validateMessage(String message) {

        if (message.contains("Ignoring unknown attribute")) {

            return Result.DENY;
        }

        if (message.contains("Summoned new Wither")) {

            return Result.DENY;
        }

        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        org.apache.logging.log4j.message.Message candidate = null;
        if (event != null) {
            candidate = event.getMessage();
        }
        return validateMessage(candidate);
    }

    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return validateMessage(msg);
    }

    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return validateMessage(msg);
    }

    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        String candidate = null;
        if (msg != null) {
            candidate = msg.toString();
        }
        return validateMessage(candidate);
    }
}